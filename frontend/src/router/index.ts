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
      path: '/password-vergessen',
      name: 'Password vergessen',
      component: () => import('../views/ForgotPasswordView.vue')
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
      name: 'Meine Feuerwehr',
      component: () => import('../views/ListDepartmentView.vue')
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

export default router
