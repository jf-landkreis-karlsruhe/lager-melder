<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTentsForDepartment, updateTents } from '@/services/tents'
import type { Tents } from '@/services/tents'
import { useToast } from 'vue-toastification'

const toast = useToast()

const props = defineProps<{
  departmentId: number
  short?: boolean
}>()

const tents = ref<Tents | undefined>()
const saving = ref<boolean>(false)

const saveTents = async () => {
  if (!tents.value) return
  saving.value = true
  const newTents = await updateTents(tents.value).catch(() => {
    saving.value = false
    toast.error('Fehler beim speichern der Zelte.')
  })
  if (!newTents) return

  tents.value = newTents
  saving.value = false
  toast.success('Zelte wurden gespeichert.')
}

onMounted(() => {
  getTentsForDepartment(props.departmentId).then((newTents) => {
    tents.value = newTents
  })
})
</script>

<template>
  <v-card>
    <v-card-title>
      <h3 class="mt-2">Voranmeldung Zelte</h3>
    </v-card-title>
    <v-card-text>
      <p v-if="!short">
        Bitte gebt hier an welche Zelten ihr plant aufzustellen. Sollte euer exaktes Zelt nicht
        vorhanden sein, wählt das Zelt mit ähnlichen Abmaßen aus. Die Anzahl und Größen der Zelte
        soll uns helfen die größe des Zeltplatzes planen zu können.
      </p>

      <form v-if="tents" v-on:submit.prevent="saveTents">
        <p>Die Größen der Zelte sind angegeben in Länge x Breite.</p>
        <v-text-field
          type="number"
          v-model="tents.sg200"
          label="SG 200 (4m x 5,9m)"
          min="0"
          variant="underlined"
          required
          :error-messages="tents.sg200 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''"
        />
        <v-text-field
          type="number"
          v-model="tents.sg20"
          label="SG 20 (5m x 4,74m)"
          min="0"
          variant="underlined"
          required
          :error-messages="tents.sg20 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''"
        />
        <v-text-field
          type="number"
          v-model="tents.sg30"
          label="SG 30 (6m x 5,64m)"
          min="0"
          variant="underlined"
          required
          :error-messages="tents.sg30 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''"
        />
        <v-text-field
          type="number"
          v-model="tents.sg40"
          label="SG 40 (8m x 5,64m)"
          min="0"
          variant="underlined"
          required
          :error-messages="tents.sg40 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''"
        />
        <v-text-field
          type="number"
          v-model="tents.sg50"
          label="SG 50 (10m x 5,64m)"
          min="0"
          variant="underlined"
          required
          :error-messages="tents.sg50 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''"
        />
        <v-row justify="end" class="my-4">
          <v-btn class="mx-2" rounded color="primary" :loading="saving" type="submit">
            Zelte speichern
          </v-btn>
        </v-row>
      </form>
      <p v-if="!short">
        Weitere Informationen gibt es auch auf der Herstellerseite von
        <a
          href="https://www.lanco.eu/produkte/zelte/zelte-mit-40-mm-aluminiumgerust/sanitats-und-aufenthaltszelte"
          target="_blank"
          rel="noopener noreferrer"
          >Lanco</a
        >.
      </p>
    </v-card-text>
  </v-card>
</template>

<style scoped></style>
