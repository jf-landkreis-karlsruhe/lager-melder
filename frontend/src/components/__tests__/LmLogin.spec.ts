import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import LmLogin from '../LmLogin.vue'
import { isLoggedIn, login } from '@/services/authentication'

const push = vi.fn()
const toastError = vi.fn()

vi.mock('vue-router', () => ({ useRouter: () => ({ push }) }))
vi.mock('vue-toastification', () => ({ useToast: () => ({ error: toastError, success: vi.fn() }) }))
vi.mock('@/services/authentication', () => ({ login: vi.fn(), isLoggedIn: vi.fn() }))

const fillAndSubmit = async (wrapper: ReturnType<typeof mount>) => {
  await wrapper.find('input#username').setValue('ettlingen@email.de')
  await wrapper.find('input#password').setValue('secret')
  await wrapper.find('form').trigger('submit')
  await flushPromises()
}

describe('LmLogin', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(isLoggedIn).mockReturnValue(false)
  })

  it('logs in with the entered credentials and navigates to the start page', async () => {
    vi.mocked(login).mockResolvedValue({ sub: 'ettlingen@email.de' } as Awaited<ReturnType<typeof login>>)
    const wrapper = mount(LmLogin, { global: { stubs: { RouterLink: true } } })

    await fillAndSubmit(wrapper)

    expect(login).toHaveBeenCalledWith('ettlingen@email.de', 'secret')
    expect(push).toHaveBeenCalledWith('/')
  })

  it('shows an error toast when the login fails', async () => {
    vi.mocked(login).mockRejectedValue(
      new Response(JSON.stringify({ path: '/api/login', status: 401 }), { status: 401 })
    )
    const wrapper = mount(LmLogin, { global: { stubs: { RouterLink: true } } })

    await fillAndSubmit(wrapper)

    expect(push).not.toHaveBeenCalled()
    expect(toastError).toHaveBeenCalledWith('Benutzername oder Passwort sind falsch')
  })
})
