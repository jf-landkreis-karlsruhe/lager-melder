import { describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import LmAttendeeAddForm from '../LmAttendeeAddForm.vue'
import { type Attendee, AttendeeRole, getAttendeeDefault } from '@/services/attendee'
import { validateJuleika } from '@/services/juleika'

vi.mock('@/services/juleika', () => ({ validateJuleika: vi.fn() }))

const mountForm = (attendee: Attendee) =>
  mount(LmAttendeeAddForm, {
    props: {
      attendee,
      role: AttendeeRole.YOUTH_LEADER,
      roleTitle: 'Jugendleiter',
      tShirtSizes: [{ title: 'M', props: {} }],
      eventDays: [],
      departments: []
    }
  })

const saveButton = (wrapper: ReturnType<typeof mountForm>) => wrapper.find('button[type="submit"]')

const juleikaNumberInput = (wrapper: ReturnType<typeof mountForm>) =>
  wrapper.findAll('input').find((input) => input.element.closest('.v-input')?.textContent?.includes('Juleika-Nummer'))!

const storedLeader = (): Attendee => ({
  ...getAttendeeDefault(AttendeeRole.YOUTH_LEADER, 1),
  id: 'leader-1',
  firstName: 'Lea',
  lastName: 'Leiter',
  birthday: '2000-01-01',
  tShirtSize: 'M',
  juleikaNumber: '12340',
  juleikaExpireDate: '2030-01-01'
})

describe('LmAttendeeAddForm', () => {
  it('lets an existing leader with stored Juleika be saved without a new check', async () => {
    const wrapper = mountForm(storedLeader())
    await flushPromises()

    expect(saveButton(wrapper).attributes('disabled')).toBeUndefined()
    expect(validateJuleika).not.toHaveBeenCalled()
  })

  it('requires a check for a new leader', async () => {
    const wrapper = mountForm(getAttendeeDefault(AttendeeRole.YOUTH_LEADER, 1))
    await flushPromises()

    expect(saveButton(wrapper).attributes('disabled')).toBeDefined()
  })

  it('requires a new check when the Juleika number of an existing leader changes', async () => {
    const wrapper = mountForm(storedLeader())
    await juleikaNumberInput(wrapper).setValue('99990')
    await flushPromises()

    expect(saveButton(wrapper).attributes('disabled')).toBeDefined()
  })
})
