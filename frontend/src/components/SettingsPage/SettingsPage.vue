<template>
  <div>
    <EditSettings />
    <v-card>
      <form v-on:submit.prevent="saveSettings(settings)">
        <v-text>
          <h3>Anmeldung</h3>
          <v-text-field
            type="date"
            v-model="settings.registrationEnd"
            label="Ende Registration"
          />
          <h3>Veranstalltung</h3>
          <v-text-field
            type="date"
            v-model="settings.eventStart"
            label="Anfang der Veranstalltung"
          />
          <v-text-field
            type="date"
            v-model="settings.eventEnd"
            label="Ende der Veranstalltung"
          />
          <v-text-field
            type="text"
            v-model="settings.hostCity"
            label="Veranstalltungsort"
          />
          <v-text-field
            type="text"
            v-model="settings.eventAddress"
            label="Veranstalltungsadresse"
          />
          <h3>Organisator</h3>
          <v-text-field
            type="text"
            v-model="settings.organizer"
            label="Organisator"
          />
          <v-textarea
            v-model="settings.organisationAddress"
            label="Adresse des Organisator"
          />
          <h3>Zuschuss</h3>
          <v-text-field
            type="text"
            v-model="settings.moneyPerYouthLoader"
            label="Zuschuss pro Betreuer"
          />
        </v-text>
        <v-card-actions>
          <v-row justify="end">
            <v-btn color="primary" type="submit">Speichern</v-btn>
          </v-row>
        </v-card-actions>
      </form>
    </v-card>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import EditSettings from "./EditSettings.vue";

// eslint-disable-next-line no-unused-vars
import { getSettings, updateSettings, Settings } from "../../services/settings";
@Component({
  components: { EditSettings },
})
export default class SettingsPage extends Vue {
  settings: Settings = {} as Settings;

  saveSettings(settings: Settings) {
    console.log(settings, this.settings);
    updateSettings(settings);
  }

  mounted() {
    getSettings().then((settings) => {
      console.log(settings);
      this.settings = settings;
    });
  }
}
</script>
