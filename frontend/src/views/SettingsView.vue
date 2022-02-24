<template>
  <div>
    <h1>Einstellungen</h1>
    <EditSettings />
    <EventsConfiguration />
    <PcrTestsConfiguration />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import EditSettings from "../components/settings/EditSettings.vue";
import EventsConfiguration from "../components/settings/EventsConfiguration.vue";
import PcrTestsConfiguration from "../components/settings/PcrTestsConfiguration.vue";

@Component({
  components: { EditSettings, EventsConfiguration, PcrTestsConfiguration },
})
export default class SettingsView extends Vue {}
</script>
