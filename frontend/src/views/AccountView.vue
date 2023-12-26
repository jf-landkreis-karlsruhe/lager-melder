<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { logout, isLoggedIn } from '@/services/authentication'
import { getMyDepartment } from '@/services/department'
import type { Department } from '@/services/department'
import { changePassword, getMe } from '@/services/user'
import type { User } from '@/services/user'
import EditDepartment from '../components/LmEditDepartment.vue'

const myDepartment = ref<Department>({} as Department)
const password = ref<string>('')
const repeatPassword = ref<string>('')
const passwordLoading = ref<boolean>(false)
const passwordSuccess = ref<boolean>(false)
const user = ref<User>({} as User)
const showPasswordError = ref<boolean>(false)

const updateUser = () => {
  if (password.value !== repeatPassword.value) {
    showPasswordError.value = true
    return
  }
  passwordLoading.value = true
  changePassword({ ...user.value, password: password.value })
    .then(() => {
      passwordLoading.value = false
      passwordSuccess.value = true
      password.value = ''
      repeatPassword.value = ''
      setTimeout(() => (passwordSuccess.value = false), 2000)
    })
    .catch((error) => {
      console.error(error)
      passwordLoading.value = false
    })
}

const onLogout = () => {
  logout()
  document.location.assign('/login')
}

onMounted(() => {
  getMe().then((newUser) => (user.value = newUser))
  getMyDepartment().then((department) => {
    myDepartment.value = department
  })
})

watch([password, repeatPassword], () => {
  showPasswordError.value = false
})
</script>

<template>
  <v-container>
    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="6">
        <div v-if="myDepartment.id">
          <h2>Deine Feuerwehr - {{ myDepartment.name }}</h2>
          <EditDepartment :department="myDepartment" class="mb-8" />
        </div>
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="6">
        <h2>Passwort</h2>
        <form v-on:submit.prevent="updateUser()" class="mb-8">
          <v-text-field
            v-model="password"
            label="Passwort"
            hint="Mindestlänge 8 Zeichen"
            variant="underlined"
            required
          />
          <v-text-field
            variant="underlined"
            v-model="repeatPassword"
            label="Passwort wiederholen"
            required
          />
          <v-alert v-if="showPasswordError" type="error">
            Die Passwörter sind nicht gleich.
          </v-alert>

          <v-container>
            <v-row justify="end">
              <v-btn
                :color="passwordSuccess ? 'success' : 'primary'"
                :loading="passwordLoading"
                type="submit"
                rounded
              >
                <div v-if="passwordSuccess"><v-icon medium>mdi-check</v-icon> geändert</div>
                <div v-if="!passwordSuccess">Passwort ändern</div>
              </v-btn>
            </v-row>
          </v-container>
        </form>
      </v-col>
    </v-row>

    <hr class="mt-16 mb-8" />
    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="6">
        <div v-if="isLoggedIn()" class="d-flex justify-space-between align-center">
          <p class="mb-0">Du bist eingeloggt:</p>
          <v-btn rounded color="error" @click="onLogout"> Ausloggen </v-btn>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>
