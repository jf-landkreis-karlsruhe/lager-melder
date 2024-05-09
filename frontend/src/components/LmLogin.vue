<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { login, isLoggedIn } from '../services/authentication'
import LmContainer from './LmContainer.vue'
import {showErrorToast} from "@/helper/fetch";

const router = useRouter()
const toast = useToast()

const username = ref<string>('')
const password = ref<string>('')
const loggedIn = ref<boolean>(false)
const loading = ref<boolean>(false)

const loginHandler = async () => {
  loading.value = true
  const success = await login(username.value, password.value).catch(async (err) => {
    loading.value = false
    await showErrorToast(toast, err, 'Der Login war nicht erfolgreich. Benutzername oder Passwort falsch.')
    return undefined
  })
  if (success) {
    loading.value = false
    loggedIn.value = true
    router.push('/')
  }
}

onMounted(() => {
  loggedIn.value = isLoggedIn()
})
</script>

<template>
  <div>
    <LmContainer>
      <v-row justify="center">
        <v-col sm="12" md="6">
          <v-card v-if="!loggedIn">
            <v-card-title>Login</v-card-title>
            <form @submit.prevent="loginHandler">
              <v-card-text>
                <v-text-field
                  variant="underlined"
                  prepend-icon="mdi-account"
                  v-model="username"
                  label="Benutzername"
                  type="email"
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
          <router-link to="/passwort-vergessen" class="account">
            <span>Password vergessen</span>
          </router-link>
        </v-col>
      </v-row>
    </LmContainer>
  </div>
</template>
