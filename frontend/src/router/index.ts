import Vue from "vue";
import VueRouter, { RouteConfig } from "vue-router";
import HomeView from "../views/HomeView.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: "/",
    name: "Home",
    component: HomeView
  },
  {
    path: "/login",
    name: "Login",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/LoginView.vue")
  },
  {
    path: "/teilnehmer",
    name: "AttendeesRegistration",
    component: () => import("../views/AttendeesRegistrationView.vue")
  },
  {
    path: "/overview",
    name: "Lagerübersicht",
    component: () => import("../components/Overview.vue")
  },
  {
    path: "/files",
    name: "Registrierungsunterlagen",
    component: () => import("../components/RegistrationFiles.vue")
  },
  {
    path: "/feuerwehr",
    name: "Meine Feuerwehr",
    component: () => import("../components/ListDepartment.vue")
  },
  {
    path: "/scanner/:eventCode",
    name: "Scanner",
    component: () => import("../components/Scanner.vue")
  },
  {
    path: "/events/",
    name: "Events",
    component: () => import("../components/EventsConfiguration.vue")
  },
  {
    path: "/einstellungen",
    name: "Einstellungen",
    component: () => import("../views/SettingsView.vue")
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
