<script setup lang="ts">
import EditSettings from '../components/settings/LmEditSettings.vue'
import EventsConfiguration from '../components/settings/LmEventsConfiguration.vue'
import MailSettings from '../components/settings/LmMailSettings.vue'
import LmContainer from '@/components/LmContainer.vue'
</script>

<template>
  <LmContainer>
    <h1>Einstellungen</h1>
    <EditSettings />
    <EventsConfiguration />
    <MailSettings />
  </LmContainer>
</template>
