<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, nextTick } from 'vue'
import { getEventByCode, loginToEvent } from '../services/event'
import { isValidTestCode } from '../assets/config'
import { useToast } from 'vue-toastification'
import { useRoute } from 'vue-router'
import { renewToken } from '@/services/authentication'
import { showErrorToast } from '@/helper/fetch'

const toast = useToast()
const route = useRoute()
const eventCode = ref<string>('')
const eventName = ref<string>('')
let intervalId: number | undefined

const manualCode = ref<string>('')
const manualCodeForm = ref<HTMLElement | undefined>()
const manualCodeInput = ref<HTMLElement | undefined>()
const manualCodeValid = ref<boolean>(false)

const manualCodeInputRules = computed<((value: string) => boolean | string)[]>(() => {
  return [(value: string) => !!value || 'Required.', (value: string) => isValidTestCode(value) || '8 Zeichen benötigt']
})

const submitEvent = async (attendeeCode: string) => {
  if (!isValidTestCode(attendeeCode)) {
    return
  }
  const attendeeRes = await loginToEvent(eventCode.value, attendeeCode).catch((e) => {
    console.error(e)
    showErrorToast(toast, e, `${eventName.value} mit "${attendeeCode}" hat nicht geklappt.`)
  })
  if (attendeeRes) {
    toast.success(`${eventName.value} von "${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName}".`)
  }
}

const manualCodeSubmit = async () => {
  const manCodeForm = manualCodeForm.value as any
  const manCode = manualCode.value
  manCodeForm.validate()
  await submitEvent(manCode)
  manCodeForm.reset()
  ;(document?.activeElement as any)?.blur()
  await nextTick()
  ;(manualCodeInput.value as any)?.focus()
}

onMounted(async () => {
  eventCode.value = Array.isArray(route.params.eventCode) ? route.params.eventCode[0] : route.params.eventCode
  const event = await getEventByCode(eventCode.value)
  eventName.value = event.name

  intervalId = setInterval(renewToken, 60 * 60 * 1000) as unknown as number // each hour
})

onBeforeUnmount(() => {
  clearInterval(intervalId)
})
</script>

<template>
  <div>
    <v-container v-if="eventName" class="event-root">
      <h1>🏕 Event: {{ eventName }}</h1>
      <v-row justify="center">
        <v-form
          ref="manualCodeForm"
          v-model="manualCodeValid"
          @submit.prevent="manualCodeSubmit"
          class="manual-code-form d-flex justify-center mt-8 mb-12"
        >
          <v-row class="manual-code-row align-center ga-3">
            <v-text-field
              v-model="manualCode"
              ref="manualCodeInput"
              label="Manuelle Eingabe"
              :autofocus="true"
              :hide-details="false"
              hint="8 Zeichen benötigt"
              :rules="manualCodeInputRules"
              class="manual-code-input mr-3"
              variant="underlined"
            />
            <v-btn :disabled="!manualCode || !manualCodeValid" type="submit" small outlined rounded>
              Abschicken
            </v-btn>
          </v-row>
        </v-form>
      </v-row>
    </v-container>

    <v-container v-else>
      <v-alert color="error" type="error" border="top">
        <b> Dieses Event existiert nicht! </b>
        <p>Bitte prüfe den Eventnamen in der URL</p>
        <br />
      </v-alert>
    </v-container>
  </div>
</template>

<style scoped lang="scss">
.event-root {
  position: relative;

  .manual-code-form {
    .manual-code-input {
      min-width: 200px;
    }
  }
}
</style>
