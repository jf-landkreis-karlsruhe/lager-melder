<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { Settings } from '../../services/settings'
import { getSettings, updateSettings } from '../../services/settings'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'

const toast = useToast()

const settings = ref<Settings>({} as Settings)
const loading = ref<boolean>(false)

const downloadAfterEndRegistration = computed<boolean>(() => {
  return (
    new Date(settings.value.registrationEnd).getTime() -
      new Date(settings.value.startDownloadRegistrationFiles).getTime() <
    0
  )
})

const saveSettings = (settings: Settings) => {
  loading.value = true
  updateSettings(settings)
    .then(() => {
      loading.value = false
      toast.success('Einstellungen gespeichert.')
    })
    .catch(async (err) => {
      loading.value = false
      await showErrorToast(toast, err, 'Fehler beim Speichern der Einstellungen.')
    })
}

onMounted(() => {
  getSettings().then((newSettings) => {
    settings.value = newSettings
  })
})
</script>

<template>
  <section>
    <v-card class="pa-4 mb-16">
      <h2 class="ml-md-4">Allgemein</h2>
      <v-row justify="center">
        <v-col sm="12">
          <form class="pa-4" @submit.prevent="saveSettings(settings)">
            <div>
              <h3>Anmeldung</h3>
              <v-text-field
                v-model="settings.registrationEnd"
                type="date"
                label="Registrierungsende Teilnehmer"
                :variant="'underlined'"
                :error-messages="
                  downloadAfterEndRegistration
                    ? ''
                    : 'Registrierungsende muss nach dem Start des Downloads der Anmeldeunterlagen liegen.'
                "
              />
              <v-text-field
                v-model="settings.startDownloadRegistrationFiles"
                type="date"
                label="Anfangszeitpunkt des Downloads der Anmeldeunterlagen"
                :variant="'underlined'"
                :error-messages="
                  downloadAfterEndRegistration
                    ? ''
                    : 'Ende der Registrierung für Teilnehmer muss nach dem Start des Downloads der Anmeldeunterlagen liegen.'
                "
              />
              <v-text-field
                v-model="settings.childGroupsRegistrationEnd"
                type="date"
                label="Registrierungsende Kindergruppen"
                :variant="'underlined'"
              />
              <v-text-field
                v-model="settings.helpersRegistrationEnd"
                type="date"
                label="Registrierungsende Helfer"
                :variant="'underlined'"
                :error-messages="
                  downloadAfterEndRegistration
                    ? ''
                    : 'Registrierungsende muss nach dem Start des Downloads der Anmeldeunterlagen liegen.'
                "
              />
              <h3>Veranstalltung</h3>
              <v-text-field
                v-model="settings.eventStart"
                type="date"
                label="Anfang der Veranstalltung"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Teilnehmerliste Landkreis, Anmeldeliste, Pädagogische Betreuer"
              />
              <v-text-field
                v-model="settings.eventEnd"
                type="date"
                label="Ende der Veranstalltung"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Teilnehmerliste Landkreis, Anmeldeliste, Pädagogische Betreuer"
              />
              <v-text-field
                v-model="settings.eventName"
                type="text"
                label="Veranstalltungsname"
                :variant="'underlined'"
                hint="Benutzt für Teilnehmerliste Landkreis"
              />
              <v-text-field
                v-model="settings.hostCity"
                type="text"
                label="Veranstalltungsort (Ort, Gemeinde)"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Anmeldeliste"
              />
              <v-text-field
                v-model="settings.eventAddress"
                type="text"
                label="Veranstalltungsadresse"
                :variant="'underlined'"
                hint="Benutzt für Teilnehmerliste Landkreis"
              />
              <h3>Organisator</h3>
              <v-text-field
                v-model="settings.organizer"
                type="text"
                label="Organisator"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Pädagogische Betreuer"
              />
              <v-textarea
                v-model="settings.organisationAddress"
                label="Adresse des Organisator"
                :variant="'underlined'"
                hint="Benutzt für Pädagogische Betreuer"
                rows="4"
              />
              <h3>Zuschuss</h3>
              <v-text-field
                v-model="settings.moneyPerYouthLoader"
                type="text"
                label="Zuschuss pro Betreuer"
                :variant="'underlined'"
                hint="Benutzt für Pädagogische Betreuer"
              />
              <h3>Schichten</h3>
              <v-text-field
                v-model="settings.numberOfDuties"
                type="number"
                label="Anzahl der Schichten"
                :variant="'underlined'"
                :min="0"
              />
            </div>
            <v-card-actions>
              <v-row justify="end">
                <v-btn
                  color="primary"
                  :loading="loading"
                  :disabled="!downloadAfterEndRegistration"
                  type="submit"
                  class="mb-8"
                  rounded
                >
                  Speichern
                </v-btn>
              </v-row>
            </v-card-actions>
          </form>
        </v-col>
      </v-row>
    </v-card>
  </section>
</template>
