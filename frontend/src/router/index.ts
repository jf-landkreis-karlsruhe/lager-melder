import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/teilnehmer'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/passwort-vergessen',
      name: 'Passwort vergessen',
      component: () => import('../views/ForgotPasswordView.vue')
    },
    {
      path: '/passwort-zuruecksetzen/:token',
      name: 'Password zurücksetzen',
      component: () => import('../views/ResetPasswordView.vue')
    },
    {
      path: '/teilnehmer',
      name: 'AttendeesRegistration',
      component: () => import('../views/AttendeesRegistrationView.vue')
    },
    {
      path: '/overview',
      name: 'Lagerübersicht',
      component: () => import('../views/OverviewView.vue')
    },
    {
      path: '/files',
      name: 'Registrierungsunterlagen',
      component: () => import('../views/RegistrationFilesView.vue')
    },
    {
      path: '/feuerwehr',
      name: 'Feuerwehren',
      component: () => import('../views/ListDepartmentView.vue')
    },
    {
      path: '/feuerwehr/:id',
      name: 'Feuerwehr Detail',
      component: () => import('../views/DepartmentDetailView.vue')
    },
    {
      path: '/events/:eventCode',
      name: 'Events',
      component: () => import('../views/EventsView.vue')
    },
    {
      path: '/einstellungen',
      name: 'Einstellungen',
      component: () => import('../views/SettingsView.vue')
    },
    {
      path: '/anwesend',
      name: 'Anwesende',
      component: () => import('../views/EventSummaryView.vue')
    },
    {
      path: '/account',
      name: 'Account',
      component: () => import('../views/AccountView.vue')
    }
  ]
})

export const pathNeedsAuthentication = (path: string) => {
  return !(
      path.startsWith('/passwort-zuruecksetzen') || ['/login', '/passwort-vergessen'].includes(path)
  )
}

export default router
