<script setup lang="ts">
import EditSettings from '../components/settings/LmEditSettings.vue'
import EventsConfiguration from '../components/settings/LmEventsConfiguration.vue'
import MailSettings from '../components/settings/LmMailSettings.vue'
import LmContainer from '@/components/LmContainer.vue'
import TShirtSizeConfiguration from '@/components/settings/LmTShirtSizeConfiguration.vue'
import { hasSpecializedFieldDirectorRole } from '@/services/authentication'
</script>

<template>
  <LmContainer v-if="hasSpecializedFieldDirectorRole()">
    <h1>Einstellungen</h1>
    <EditSettings />
    <EventsConfiguration />
    <TShirtSizeConfiguration />
    <MailSettings />
  </LmContainer>
</template>
