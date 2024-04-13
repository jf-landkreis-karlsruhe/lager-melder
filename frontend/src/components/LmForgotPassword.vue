<script setup lang="ts">
import {ref} from 'vue'
import {useToast} from 'vue-toastification'
import {forgotPassword} from '../services/authentication'
import LmContainer from './LmContainer.vue'

const toast = useToast()

const username = ref<string>('')
const loading = ref<boolean>(false)

const forgotPasswordHandler = async () => {
  loading.value = true
  const success = await forgotPassword(username.value).catch(() => {
    loading.value = false
  })
  if (success) {
    loading.value = false
    toast.success('Reset Mail wurde versendet. Bitte überprüfen Sie Ihr Postfach.')
  }
}
</script>

<template>
  <div>
    <LmContainer>
      <v-row justify="center">
        <v-col sm="12" md="6">
          <v-card>
            <v-card-title>Password vergessen</v-card-title>
            <form @submit.prevent="forgotPasswordHandler">
              <v-card-text>
                <v-text-field
                  type="email"
                  variant="underlined"
                  prepend-icon="mdi-account"
                  v-model="username"
                  label="Benutzername"
                />
              </v-card-text>
              <v-card-actions>
                <v-container>
                  <v-row justify="end">
                    <v-btn color="primary" type="submit" :loading="loading" rounded>
                      Mail versenden
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
