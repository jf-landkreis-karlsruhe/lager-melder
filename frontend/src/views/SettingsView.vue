<template>
  <div>
    <h1>Einstellungen</h1>
    <EditSettings />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import EditSettings from "../components/settings/EditSettings.vue";

@Component({
  components: { EditSettings },
})
export default class SettingsView extends Vue {}
</script>
