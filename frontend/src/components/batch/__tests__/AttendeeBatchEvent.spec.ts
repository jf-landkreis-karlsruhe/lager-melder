import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import AttendeeBatchEvent from '../AttendeeBatchEvent.vue'
import { type Attendee, AttendeeRole, Food } from '@/services/attendee'
import { batchEnterAttendees } from '@/services/event'

vi.mock('@/services/event', () => ({ batchEnterAttendees: vi.fn() }))
vi.mock('vue-toastification', () => ({ useToast: () => ({ success: vi.fn(), error: vi.fn() }) }))

const attendee = (code: string, firstName: string): Attendee => ({
  id: code,
  code,
  firstName,
  lastName: 'Muster',
  departmentId: 1,
  birthday: '2010-01-01',
  food: Food.MEAT,
  tShirtSize: 'M',
  additionalInformation: '',
  role: AttendeeRole.YOUTH,
  juleikaNumber: '',
  juleikaExpireDate: '',
  partOfDepartmentId: undefined,
  helperDays: [],
  status: undefined
})

const mountComponent = () =>
  mount(AttendeeBatchEvent, {
    props: {
      headline: 'Feuerwehr Ettlingen',
      enterCode: 'enter123',
      leaveCode: 'leave123',
      attendeeGroups: []
    }
  })

describe('AttendeeBatchEvent', () => {
  beforeEach(() => {
    vi.mocked(batchEnterAttendees).mockReset().mockResolvedValue([])
  })

  it('lists attendees per group', async () => {
    const wrapper = mountComponent()
    await wrapper.setProps({
      attendeeGroups: [{ headline: 'Jugendliche', attendees: [attendee('a1', 'Anna'), attendee('b1', 'Ben')] }]
    })

    expect(wrapper.text()).toContain('Jugendliche')
    expect(wrapper.text()).toContain('Anna Muster')
    expect(wrapper.text()).toContain('Ben Muster')
  })

  it('enters all attendees after selecting all', async () => {
    const wrapper = mountComponent()
    await wrapper.setProps({
      attendeeGroups: [{ headline: 'Jugendliche', attendees: [attendee('a1', 'Anna'), attendee('b1', 'Ben')] }]
    })

    await wrapper.find('input[type="checkbox"]').setValue(true)
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(batchEnterAttendees).toHaveBeenCalledWith('enter123', ['a1', 'b1'])
  })
})
