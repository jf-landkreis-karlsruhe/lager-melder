<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getEventByCode, loginToEvent } from '../services/event'
import { isValidTestCode } from '../assets/config'
import { useToast } from 'vue-toastification'
import { useRoute } from 'vue-router'
import Scanner from '../components/LmScanner.vue'
import { onBeforeUnmount } from '@vue/runtime-core'
import { renewToken } from '@/services/authentication'

const toast = useToast()
const route = useRoute()
const eventCode = ref<string>('')
const eventName = ref<string>('')
let intervalId

const manualCodeInputRules = computed<((value: string) => boolean | string)[]>(() => {
  return [(value: string) => !!value || 'Required.', (value: string) => isValidTestCode(value) || '8 Zeichen benötigt']
})

const submitEvent = async (attendeeCode: string) => {
  if (!isValidTestCode(attendeeCode)) {
    return
  }
  const attendeeRes = await loginToEvent(eventCode.value, attendeeCode)
  if (attendeeRes) {
    toast.success(`${eventName.value} von "${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName}".`)
  }
}

onMounted(async () => {
  eventCode.value = Array.isArray(route.params.eventCode) ? route.params.eventCode[0] : route.params.eventCode
  const event = await getEventByCode(eventCode.value)
  eventName.value = event.name

  intervalId = setInterval(renewToken, 60 * 60 * 1000) // each hour
})

onBeforeUnmount(() => {
  clearInterval(intervalId)
})
</script>

<template>
  <div>
    <v-container class="event-root">
      <h1>🏕 Event: {{ eventName || 'Anstehendes Event' }}</h1>
      <v-row justify="center">
        <Scanner
          manualCodeHint="8 Zeichen benötigt"
          :manualCodeInputRules="manualCodeInputRules"
          @submitCode="submitEvent($event)"
        />
      </v-row>
    </v-container>
  </div>
</template>

<style scoped lang="scss">
.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
