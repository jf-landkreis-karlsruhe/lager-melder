import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import LmAttendeeAddForm from '../LmAttendeeAddForm.vue'
import { type Attendee, AttendeeRole, getAttendeeDefault } from '@/services/attendee'
import { type JuleikaValidationResult, validateJuleika } from '@/services/juleika'

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

const submit = async (wrapper: ReturnType<typeof mountForm>) => {
  await flushPromises()
  await wrapper.find('form').trigger('submit')
  await flushPromises()
}

const savedAttendee = (wrapper: ReturnType<typeof mountForm>) =>
  wrapper.emitted('save')?.[0]?.[0] as Attendee | undefined

const inputWithLabel = (wrapper: ReturnType<typeof mountForm>, label: string) =>
  wrapper.findAll('input').find((input) => input.element.closest('.v-input')?.textContent?.includes(label))!

const juleikaNumberInput = (wrapper: ReturnType<typeof mountForm>) => inputWithLabel(wrapper, 'Juleika-Nummer')

const newLeader = (): Attendee => ({
  ...getAttendeeDefault(AttendeeRole.YOUTH_LEADER, 1),
  firstName: 'Lea',
  lastName: 'Leiter',
  birthday: '2000-01-01',
  tShirtSize: 'M'
})

const newLeaderWithJuleika = (): Attendee => ({ ...newLeader(), juleikaNumber: '12340' })

const deferred = <T>() => {
  let resolve: (value: T) => void = () => {}
  const promise = new Promise<T>((res) => (resolve = res))
  return { promise, resolve }
}

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
  beforeEach(() => {
    vi.mocked(validateJuleika).mockReset()
  })

  it('saves an existing leader with stored Juleika without a new check', async () => {
    const wrapper = mountForm(storedLeader())
    await submit(wrapper)

    expect(validateJuleika).not.toHaveBeenCalled()
    expect(savedAttendee(wrapper)).toMatchObject({ juleikaNumber: '12340', juleikaExpireDate: '2030-01-01' })
  })

  it('saves a new leader without Juleika', async () => {
    const wrapper = mountForm(newLeader())
    await submit(wrapper)

    expect(validateJuleika).not.toHaveBeenCalled()
    expect(savedAttendee(wrapper)).toMatchObject({ juleikaNumber: '' })
    expect(savedAttendee(wrapper)?.juleikaExpireDate).toBeUndefined()
  })

  it('drops the stored expire date when the Juleika number of an existing leader changes', async () => {
    const wrapper = mountForm(storedLeader())
    await juleikaNumberInput(wrapper).setValue('99990')
    await submit(wrapper)

    expect(savedAttendee(wrapper)).toMatchObject({ juleikaNumber: '99990' })
    expect(savedAttendee(wrapper)?.juleikaExpireDate).toBeUndefined()
  })

  it('saves the expire date of a successful check', async () => {
    vi.mocked(validateJuleika).mockResolvedValue({ valid: true, expireDate: '2030-01-01' })
    const wrapper = mountForm(newLeaderWithJuleika())

    await juleikaNumberInput(wrapper).trigger('blur')
    await submit(wrapper)

    expect(validateJuleika).toHaveBeenCalledWith('12340', 'Leiter')
    expect(wrapper.text()).toContain('Juleika gültig')
    expect(savedAttendee(wrapper)).toMatchObject({ juleikaNumber: '12340', juleikaExpireDate: '2030-01-01' })
  })

  it('saves a leader whose Juleika is invalid without expire date', async () => {
    vi.mocked(validateJuleika).mockResolvedValue({ valid: false })
    const wrapper = mountForm(newLeaderWithJuleika())

    await juleikaNumberInput(wrapper).trigger('blur')
    await flushPromises()
    expect(wrapper.text()).toContain('Juleika ungültig oder nicht gefunden.')

    await submit(wrapper)
    expect(savedAttendee(wrapper)).toMatchObject({ juleikaNumber: '12340' })
    expect(savedAttendee(wrapper)?.juleikaExpireDate).toBeUndefined()
  })

  it('does not accept a valid Juleika without expire date', async () => {
    vi.mocked(validateJuleika).mockResolvedValue({ valid: true })
    const wrapper = mountForm(newLeaderWithJuleika())

    await juleikaNumberInput(wrapper).trigger('blur')
    await flushPromises()

    expect(wrapper.text()).toContain('Juleika ungültig oder nicht gefunden')
  })

  it('waits for a running check before saving', async () => {
    const pending = deferred<JuleikaValidationResult>()
    vi.mocked(validateJuleika).mockReturnValue(pending.promise)
    const wrapper = mountForm(newLeaderWithJuleika())

    await juleikaNumberInput(wrapper).trigger('blur')
    await submit(wrapper)
    expect(wrapper.emitted('save')).toBeUndefined()

    pending.resolve({ valid: true, expireDate: '2030-01-01' })
    await flushPromises()
    expect(savedAttendee(wrapper)).toMatchObject({ juleikaExpireDate: '2030-01-01' })
  })

  it('ignores the result of a check for a number that was changed in the meantime', async () => {
    const pending = deferred<JuleikaValidationResult>()
    vi.mocked(validateJuleika).mockReturnValue(pending.promise)
    const wrapper = mountForm(newLeaderWithJuleika())

    await juleikaNumberInput(wrapper).trigger('blur')
    await juleikaNumberInput(wrapper).setValue('99999')
    pending.resolve({ valid: true, expireDate: '2030-01-01' })
    await flushPromises()

    expect(wrapper.text()).not.toContain('Juleika gültig')
  })

  it('only applies the result of the latest check', async () => {
    const first = deferred<JuleikaValidationResult>()
    const second = deferred<JuleikaValidationResult>()
    vi.mocked(validateJuleika).mockReturnValueOnce(first.promise).mockReturnValueOnce(second.promise)
    const wrapper = mountForm(newLeaderWithJuleika())

    await inputWithLabel(wrapper, 'Nachname').trigger('blur')
    await juleikaNumberInput(wrapper).trigger('blur')
    second.resolve({ valid: false })
    await flushPromises()
    first.resolve({ valid: true, expireDate: '2030-01-01' })
    await submit(wrapper)

    expect(savedAttendee(wrapper)?.juleikaExpireDate).toBeUndefined()
  })
})
