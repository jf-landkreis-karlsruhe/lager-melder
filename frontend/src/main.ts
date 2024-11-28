import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { VDateInput } from 'vuetify/labs/VDateInput'

const vuetify = createVuetify({
  locale: { locale: 'de' },
  date: { locale: { de: 'de-DE' } },
  components: { ...components, VDateInput },
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: { mdi }
  }
})

const app = createApp(App)

app.use(router)
app.use(toast, {
  timeout: 4000
})
app.use(vuetify)

app.mount('#app')
