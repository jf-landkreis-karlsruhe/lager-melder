<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { Tents } from '@/services/tents'
import { getTentsForDepartment } from '@/services/tents'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'
import { updateRegistration } from '@/services/department'

const toast = useToast()

const props = defineProps<{
  departmentId: number
  departmentPhoneNumber: string
}>()

const tents = ref<Tents | undefined>()
const phoneNumber = ref<string>(props.departmentPhoneNumber)
const saving = ref<boolean>(false)
const activePanel = ref<number>(0)

const saveRegistrationInformation = async () => {
  if (!tents.value) return
  saving.value = true
  const registrationData = {
    departmentId: props.departmentId,
    tents: tents.value,
    departmentPhoneNumber: phoneNumber.value
  }
  tents.value = await updateRegistration(registrationData).catch(async (error) => {
    saving.value = false
    await showErrorToast(toast, error, 'Fehler beim Speichern der Anmeldeinformationen.')
    return registrationData.tents
  })
  saving.value = false
  toast.success('Anmeldeinformationen wurden gespeichert.')
}

onMounted(() => {
  getTentsForDepartment(props.departmentId).then((newTents) => {
    tents.value = newTents
  })
})
</script>

<template>
  <v-expansion-panels v-model="activePanel">
    <v-expansion-panel>
      <v-expansion-panel-title expand-icon="mdi-menu-down">
        <h3 class="mt-4 mb-2">Weitere Anmeldeinformationen</h3>
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        <v-card-title> </v-card-title>
        <v-card-text class="d-flex align-center flex-column">
          <h4 class="mt-2">Voranmeldung Zelte</h4>
          <p>
            Bitte gebt hier an welche Zelten ihr plant aufzustellen. Sollte euer exaktes Zelt nicht vorhanden sein,
            wählt das Zelt mit ähnlichen Abmaßen aus. Die Anzahl und Größen der Zelte soll uns helfen die größe des
            Zeltplatzes planen zu können.
          </p>
          <p class="pt-4">
            Weitere Informationen gibt es auch auf der Herstellerseite von
            <a
              href="https://www.lanco.eu/produkte/zelte/zelte-mit-40-mm-aluminiumgerust/sanitats-und-aufenthaltszelte"
              target="_blank"
              rel="noopener noreferrer"
              >Lanco</a
            >.
          </p>

          <p class="pt-6">Die Größen der Zelte sind angegeben in Länge x Breite.</p>
          <form
            v-if="tents"
            v-on:submit.prevent="saveRegistrationInformation"
            class="w-30 d-flex align-center flex-column"
          >
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

            <h4 class="mt-12">Erreichbarkeit während dem Zeltlager</h4>
            <p>
              Damit wir euch während dem Zeltlager erreichen können, bitte hier eine Handynummer eingeben, auf der wir
              während der ganzen Zeit des Zeltlagers einen Jugendleiter erreichen können.
            </p>
            <v-text-field
              class="w-75 pt-2"
              type="text"
              v-model="phoneNumber"
              label="Kontaktnummer"
              variant="underlined"
            />

            <v-row justify="end" class="my-4 w-100">
              <v-btn class="mx-2" rounded color="primary" :loading="saving" type="submit"> Speichern </v-btn>
            </v-row>
          </form>
        </v-card-text>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<style scoped></style>
