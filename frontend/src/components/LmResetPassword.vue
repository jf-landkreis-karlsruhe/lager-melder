<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { resetPasswordWithToken } from '../services/authentication'
import LmContainer from './LmContainer.vue'
import { showErrorToast } from '@/helper/fetch'

const router = useRouter()
const route = useRoute()
const toast = useToast()

const password = ref<string>('')
const password_repeat = ref<string>('')
const loading = ref<boolean>(false)
let password_differs = ref<boolean>(false)

const resetPasswordHandler = async () => {
  if (password.value !== password_repeat.value) {
    password_differs.value = true
    return
  }
  loading.value = true
  const success = await resetPasswordWithToken(route.params.token as string, password.value).catch(
    async (err) => {
      loading.value = false
      await showErrorToast(
        toast,
        err,
        'Password konnte nicht zurückgesetzt werden. Bitte versuchen Sie es erneut.'
      )
      return undefined
    }
  )
  if (success) {
    toast.success('Password erfolgreich zurückgesetzt.')
    loading.value = false
    router.push('/login')
  }
}

watch(
  () => [password.value, password_repeat.value],
  () => {
    password_differs.value = false
  }
)
</script>

<template>
  <div>
    <LmContainer>
      <v-row justify="center">
        <v-col sm="12" md="6">
          <v-card>
            <v-card-title>Passwort zurücksetzen</v-card-title>
            <form @submit.prevent="resetPasswordHandler">
              <v-card-text>
                <v-text-field
                  type="password"
                  prepend-icon="mdi-lock"
                  v-model="password"
                  label="Passwort"
                  variant="underlined"
                />
                <v-text-field
                  type="password"
                  prepend-icon="mdi-lock"
                  v-model="password_repeat"
                  label="Passwort wiederholen"
                  variant="underlined"
                  :error-messages="password_differs ? 'Passwörter stimmen nicht überein.' : ''"
                />
              </v-card-text>
              <v-card-actions>
                <v-container>
                  <v-row justify="end">
                    <v-btn color="primary" type="submit" :loading="loading" rounded>
                      Passwort zurücksetzen
                    </v-btn>
                  </v-row>
                </v-container>
              </v-card-actions>
            </form>
          </v-card>
        </v-col>
      </v-row>
    </LmContainer>
  </div>
</template>
