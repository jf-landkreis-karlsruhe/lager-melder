<template>
  <div>
    <EditSettings />
    <form v-on:submit.prevent="saveSettings(settings)">
      <v-text-field
        type="date"
        v-model="settings.registrationEnd"
        label="Ende Registration"
      />
      <v-row justify="end">
        <v-btn color="primary" type="submit">Speichern</v-btn>
      </v-row>
    </form>
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
  settings: Settings = { id: "1", registrationEnd: "1212-05-21" } as Settings;

  saveSettings(settings: Settings) {
    updateSettings(settings);
  }

  mounted() {
    getSettings().then((settings) => (this.settings = settings));
  }
}
</script>
