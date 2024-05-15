<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getSettings, updateSettings } from '../../services/settings'
import type { Settings } from '../../services/settings'
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
          <form class="pa-4" v-on:submit.prevent="saveSettings(settings)">
            <div>
              <h3>Anmeldung</h3>
              <v-text-field
                type="date"
                v-model="settings.registrationEnd"
                label="Ende Registration"
                :variant="'underlined'"
                :error-messages="
                  downloadAfterEndRegistration
                    ? ''
                    : 'Ende der Registrierung muss nach dem Start des Downloads der Anmeldeunterlagen liegen.'
                "
              />
              <v-text-field
                type="date"
                v-model="settings.startDownloadRegistrationFiles"
                label="Anfangszeitpunkt des Downloads der Anmeldeunterlagen"
                :variant="'underlined'"
                :error-messages="
                  downloadAfterEndRegistration
                    ? ''
                    : 'Ende der Registrierung muss nach dem Start des Downloads der Anmeldeunterlagen liegen.'
                "
              />
              <h3>Veranstalltung</h3>
              <v-text-field
                type="date"
                v-model="settings.eventStart"
                label="Anfang der Veranstalltung"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Teilnehmerliste Landkreis, Anmeldeliste, Pädagogische Betreuer"
              />
              <v-text-field
                type="date"
                v-model="settings.eventEnd"
                label="Ende der Veranstalltung"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Teilnehmerliste Landkreis, Anmeldeliste, Pädagogische Betreuer"
              />
              <v-text-field
                type="text"
                v-model="settings.eventName"
                label="Veranstalltungsname"
                :variant="'underlined'"
                hint="Benutzt für Teilnehmerliste Landkreis"
              />
              <v-text-field
                type="text"
                v-model="settings.hostCity"
                label="Veranstalltungsort (Ort, Gemeinde)"
                :variant="'underlined'"
                hint="Benutzt für Landesjugendplan, Anmeldeliste"
              />
              <v-text-field
                type="text"
                v-model="settings.eventAddress"
                label="Veranstalltungsadresse"
                :variant="'underlined'"
                hint="Benutzt für Teilnehmerliste Landkreis"
              />
              <h3>Organisator</h3>
              <v-text-field
                type="text"
                v-model="settings.organizer"
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
                type="text"
                v-model="settings.moneyPerYouthLoader"
                label="Zuschuss pro Betreuer"
                :variant="'underlined'"
                hint="Benutzt für Pädagogische Betreuer"
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
