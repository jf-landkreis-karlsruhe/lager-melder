<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { login, isLoggedIn } from '../services/authentication'

const router = useRouter()
const toast = useToast()

const username = ref<string>('')
const password = ref<string>('')
const loggedIn = ref<boolean>(false)
const loading = ref<boolean>(false)

const loginHandler = async () => {
  loading.value = true
  await login(username.value, password.value).catch(() => {
    loading.value = false
    toast.error('Der Login war nicht erfolgreich. Benutzername oder Passwort falsch.')
  })
  loading.value = false
  loggedIn.value = true
  router.push('/')
}

onMounted(() => {
  loggedIn.value = isLoggedIn()
})
</script>

<template>
  <div>
    <v-container>
      <v-row justify="center">
        <v-col cols="4">
          <v-card v-if="!loggedIn">
            <v-card-title>Login</v-card-title>
            <form @submit.prevent="loginHandler">
              <v-card-text>
                <v-text-field
                  variant="underlined"
                  prepend-icon="mdi-account"
                  v-model="username"
                  label="Benutzername"
                />
                <v-text-field
                  type="password"
                  prepend-icon="mdi-lock"
                  v-model="password"
                  label="Passwort"
                  variant="underlined"
                />
              </v-card-text>
              <v-card-actions>
                <v-container>
                  <v-row justify="end">
                    <v-btn color="primary" type="submit" :loading="loading" rounded>
                      Einloggen
                    </v-btn>
                  </v-row>
                </v-container>
              </v-card-actions>
            </form>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>
