<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTentsForDepartment, updateTents } from '@/services/tents'
import type { Tents } from '@/services/tents'
import { useToast } from 'vue-toastification'
import { getErrorMessage, showErrorToast } from '@/helper/fetch'

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
  const newTents = await updateTents(tents.value).catch(async (error) => {
    saving.value = false
    await showErrorToast(toast, error, 'Fehler beim speichern der Zelte.')
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
    <v-card-text class="d-flex align-center flex-column">
      <p v-if="!short">
        Bitte gebt hier an welche Zelten ihr plant aufzustellen. Sollte euer exaktes Zelt nicht
        vorhanden sein, wählt das Zelt mit ähnlichen Abmaßen aus. Die Anzahl und Größen der Zelte
        soll uns helfen die größe des Zeltplatzes planen zu können.
      </p>

      <p class="pt-6">Die Größen der Zelte sind angegeben in Länge x Breite.</p>
      <form v-if="tents" v-on:submit.prevent="saveTents" class="w-30">
        <label class="text-caption d-block text-grey">SG 200 (4m x 5,9m)</label>
        <v-btn-toggle class="mb-2" v-model="tents.sg200" divided variant="outlined" required>
          <v-btn icon="mdi-numeric-0"></v-btn>
          <v-btn icon="mdi-numeric-1"></v-btn>
          <v-btn icon="mdi-numeric-2"></v-btn>
          <v-btn icon="mdi-numeric-3"></v-btn>
          <v-btn icon="mdi-numeric-4"></v-btn>
          <v-btn icon="mdi-numeric-5"></v-btn>
          <v-btn icon="mdi-numeric-6"></v-btn>
        </v-btn-toggle>
        <label class="text-caption d-block text-grey">SG 20 (5m x 4,74m)</label>
        <v-btn-toggle class="mb-2" v-model="tents.sg20" divided variant="outlined" required>
          <v-btn icon="mdi-numeric-0"></v-btn>
          <v-btn icon="mdi-numeric-1"></v-btn>
          <v-btn icon="mdi-numeric-2"></v-btn>
          <v-btn icon="mdi-numeric-3"></v-btn>
          <v-btn icon="mdi-numeric-4"></v-btn>
          <v-btn icon="mdi-numeric-5"></v-btn>
          <v-btn icon="mdi-numeric-6"></v-btn>
        </v-btn-toggle>
        <label class="text-caption d-block text-grey">SG 30 (6m x 5,64m)</label>
        <v-btn-toggle class="mb-2" v-model="tents.sg30" divided variant="outlined" required>
          <v-btn icon="mdi-numeric-0"></v-btn>
          <v-btn icon="mdi-numeric-1"></v-btn>
          <v-btn icon="mdi-numeric-2"></v-btn>
          <v-btn icon="mdi-numeric-3"></v-btn>
          <v-btn icon="mdi-numeric-4"></v-btn>
          <v-btn icon="mdi-numeric-5"></v-btn>
          <v-btn icon="mdi-numeric-6"></v-btn>
        </v-btn-toggle>
        <label class="text-caption d-block text-grey">SG 40 (8m x 5,64m)</label>
        <v-btn-toggle class="mb-2" v-model="tents.sg40" divided variant="outlined" required>
          <v-btn icon="mdi-numeric-0"></v-btn>
          <v-btn icon="mdi-numeric-1"></v-btn>
          <v-btn icon="mdi-numeric-2"></v-btn>
          <v-btn icon="mdi-numeric-3"></v-btn>
          <v-btn icon="mdi-numeric-4"></v-btn>
          <v-btn icon="mdi-numeric-5"></v-btn>
          <v-btn icon="mdi-numeric-6"></v-btn>
        </v-btn-toggle>
        <label class="text-caption d-block text-grey">SG 50 (10m x 5,64m)</label>
        <v-btn-toggle class="mb-2" v-model="tents.sg50" divided variant="outlined" required>
          <v-btn icon="mdi-numeric-0"></v-btn>
          <v-btn icon="mdi-numeric-1"></v-btn>
          <v-btn icon="mdi-numeric-2"></v-btn>
          <v-btn icon="mdi-numeric-3"></v-btn>
          <v-btn icon="mdi-numeric-4"></v-btn>
          <v-btn icon="mdi-numeric-5"></v-btn>
          <v-btn icon="mdi-numeric-6"></v-btn>
        </v-btn-toggle>

        <v-row justify="end" class="my-4">
          <v-btn class="mx-2" rounded color="primary" :loading="saving" type="submit">
            Zelte speichern
          </v-btn>
        </v-row>
      </form>
      <p v-if="!short" class="pt-4">
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
