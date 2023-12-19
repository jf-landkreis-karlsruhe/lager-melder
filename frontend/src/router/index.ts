import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/teilnehmer'
    }
    // {
    //   path: '/login',
    //   name: 'Login',
    //   // route level code-splitting
    //   // this generates a separate chunk (About.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () => import('../views/LoginView.vue')
    // },
    // {
    //   path: '/teilnehmer',
    //   name: 'AttendeesRegistration',
    //   component: () => import('../views/AttendeesRegistrationView.vue')
    // },
    // {
    //   path: '/overview',
    //   name: 'Lagerübersicht',
    //   component: () => import('../components/Overview.vue')
    // },
    // {
    //   path: '/files',
    //   name: 'Registrierungsunterlagen',
    //   component: () => import('../components/RegistrationFiles.vue')
    // },
    // {
    //   path: '/feuerwehr',
    //   name: 'Meine Feuerwehr',
    //   component: () => import('../components/ListDepartment.vue')
    // },
    // {
    //   path: '/events/:eventCode',
    //   name: 'Events',
    //   component: () => import('../views/EventsView.vue')
    // },
    // {
    //   path: '/pcr-tests',
    //   name: 'PCR Tests',
    //   component: () => import('../views/PcrTestsView.vue')
    // },
    // {
    //   path: '/pcr-tests/:poolId',
    //   name: 'PCR Test Pool',
    //   component: () => import('../views/PcrTestView.vue')
    // },
    // {
    //   path: '/einstellungen',
    //   name: 'Einstellungen',
    //   component: () => import('../views/SettingsView.vue')
    // },
    // {
    //   path: '/anwesend',
    //   name: 'Anwesende',
    //   component: () => import('../views/EventSummaryView.vue')
    // },
    // {
    //   path: '/account',
    //   name: 'Account',
    //   component: () => import('../views/AccountView.vue')
    // }
  ]
})

export default router
