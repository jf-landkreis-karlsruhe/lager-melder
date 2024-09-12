<script setup lang="ts">
import { computed, onBeforeMount } from 'vue'
import { dateTimeLocalized } from '@/helper/displayDate'

const props = defineProps<{
  registrationEnd: Date | null
}>()


let now = new Date()

const registrationEndLocalized = computed<string | undefined>(() => dateTimeLocalized(props.registrationEnd))

const registrationEndDiff = computed<
  | {
      days: number
      minutes: number
      hours: number
      seconds: number
    }
  | undefined
>(() => {
  if (!props.registrationEnd) {
    return undefined
  }
  const diff = props.registrationEnd.getTime() - now.getTime()
  if (diff < 0) return undefined

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  return { days, hours, minutes, seconds }
})


onBeforeMount(async () => {
  setInterval(() => {
    now = new Date()
  }, 1000)
})
</script>

<template>
  <div v-if="props.registrationEnd && registrationEndDiff" class="mb-5">
    <v-alert color="warning" type="warning" border="top">
      <div style="color: #333333">
        <b>
          Anmeldeschluss {{ registrationEndDiff ? 'ist' : 'war' }} am:
          {{ registrationEndLocalized }} Uhr
        </b>
        <br />
        <div v-if="registrationEndDiff">
          Das sind noch {{ registrationEndDiff.days }}
          {{ registrationEndDiff.days === 1 ? 'Tag' : 'Tage' }}
          {{ registrationEndDiff.hours }}
          {{ registrationEndDiff.hours === 1 ? 'Stunde' : 'Stunden' }}
          {{ registrationEndDiff.minutes }}
          {{ registrationEndDiff.minutes === 1 ? 'Minute' : 'Minuten' }}
        </div>
        <div v-else>Anmeldeschluss ist bereits erreicht!</div>
      </div>
    </v-alert>
  </div>
</template>

<style scoped>
</style>
