<script setup lang="ts">
import EditSettings from '../components/settings/LmEditSettings.vue'
import EventsConfiguration from '../components/settings/LmEventsConfiguration.vue'
import MailSettings from '../components/settings/LmMailSettings.vue'
</script>

<template>
  <v-container>
    <h1>Einstellungen</h1>
    <EditSettings />
    <EventsConfiguration />
    <MailSettings />
  </v-container>
</template>
