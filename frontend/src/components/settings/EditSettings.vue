<template>
  <section>
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
            hint="Benutzt für Landesjugendplan, Teilnehmerliste Landkreis, Anmeldeliste, Pädagogische Betreuer"
          />
          <v-text-field
            type="date"
            v-model="settings.eventEnd"
            label="Ende der Veranstalltung"
            hint="Benutzt für Landesjugendplan, Teilnehmerliste Landkreis, Anmeldeliste, Pädagogische Betreuer"
          />
          <v-text-field
            type="text"
            v-model="settings.eventName"
            label="Veranstalltungsname"
            hint="Benutzt für Teilnehmerliste Landkreis"
          />
          <v-text-field
            type="text"
            v-model="settings.hostCity"
            label="Veranstalltungsort (Ort, Gemeinde)"
            hint="Benutzt für Landesjugendplan, Anmeldeliste"
          />
          <v-text-field
            type="text"
            v-model="settings.eventAddress"
            label="Veranstalltungsadresse"
            hint="Benutzt für Teilnehmerliste Landkreis"
          />
          <h3>Organisator</h3>
          <v-text-field
            type="text"
            v-model="settings.organizer"
            label="Organisator"
            hint="Benutzt für Landesjugendplan, Pädagogische Betreuer"
          />
          <v-textarea
            v-model="settings.organisationAddress"
            label="Adresse des Organisator"
            hint="Benutzt für Pädagogische Betreuer"
          />
          <h3>Zuschuss</h3>
          <v-text-field
            type="text"
            v-model="settings.moneyPerYouthLoader"
            label="Zuschuss pro Betreuer"
            hint="Benutzt für Pädagogische Betreuer"
          />
        </v-text>
        <v-card-actions>
          <v-row justify="end">
            <v-btn color="primary" :loading="loading" type="submit"
              >Speichern</v-btn
            >
          </v-row>
        </v-card-actions>
      </form>
    </v-card>
  </section>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

// eslint-disable-next-line no-unused-vars
import { getSettings, updateSettings, Settings } from "../../services/settings";

@Component({})
export default class EditSettings extends Vue {
  settings: Settings = {} as Settings;
  loading: boolean = false;

  saveSettings(settings: Settings) {
    this.loading = true;
    updateSettings(settings).then(() => {
      this.loading = false;
    });
  }

  mounted() {
    getSettings().then((settings) => {
      console.log(settings);
      this.settings = settings;
    });
  }
}
</script>
