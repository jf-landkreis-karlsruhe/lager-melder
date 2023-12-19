<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createEvent, getEvents, deleteEvent, updateEvent, EventType } from '../../services/event'
import type { Event } from '../../services/event'
import { getEventCodes } from '../../services/adminFiles'
import { showFile } from '../../services/filesHelper'
import { useToast } from 'vue-toastification'

const toast = useToast()

const eventName = ref<string>('')
const events = ref<Event[]>([])
const editingEventIds = ref<string[]>([])
const eventTypeLocation = ref<EventType>(EventType.LOCATION)

const loadingEventId = ref<string>('')
const loadingDownload = ref<boolean>(false)

onMounted(() => {
  getEvents().then((data) => (events.value = data))
})

const addToEditing = (event: Event) => {
  editingEventIds.value.push(event.id)
}

const createEventInternal = () => {
  loadingEventId.value = '0'
  createEvent({ name: eventName.value })
    .then(() => {
      eventName.value = ''
      loadingEventId.value = ''
      toast.success(`Neues Event gespeichert.`)
      return getEvents()
    })
    .then((data) => (events.value = data))
    .catch(() => {
      toast.error('Neues Event konnte nicht gespeichert werden.')
    })
}

const saveEvent = (event: Event) => {
  loadingEventId.value = event.id
  updateEvent(event)
    .then((data: Event) => {
      const indexOfAttendee = editingEventIds.value.indexOf(data.id)
      editingEventIds.value.splice(indexOfAttendee, 1)
      loadingEventId.value = ''
      toast.success(`${event.name} gespeichert.`)
    })
    .catch(() => {
      toast.error('Event konnte nicht gespeichert werden.')
    })
}

const createFormName = (event: Event) => {
  return `form-event-${event.id}`
}

const deleteEventInteral = (id: string) => {
  deleteEvent(id)
    .then(() => toast.success('Event wurde gelöscht.'))
    .catch(() => toast.error('Event konnte nicht gelöscht werden.'))
    .then(() => getEvents())
    .then((data) => {
      events.value = data
    })
}

const downloadEventsPDF = () => {
  loadingDownload.value = true
  getEventCodes().then((fileData) => {
    showFile(fileData.data, fileData.fileName)
    loadingDownload.value = false
  })
}
</script>

<template>
  <v-card class="mb-16">
    <h2 class="pa-4 ml-md-6">Events</h2>
    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="6">
        <h3>Event erstellen</h3>
        <form class="pa-4" v-on:submit.prevent="createEventInternal()">
          <v-text-field
            v-model="eventName"
            label="Titel des Event"
            required
            :variant="'underlined'"
          />
          <v-row class="v-row" justify="end">
            <v-btn color="primary" :loading="loadingEventId === '0'" type="submit" rounded>
              <span>Erstellen</span>
            </v-btn>
          </v-row>
        </form>

        <h2>Event QR Codes</h2>
        <p>
          Die QR Codes für alle Events herrunterladen.
          <v-btn
            small
            class="underline"
            :loading="loadingDownload"
            @click="downloadEventsPDF"
            rounded
          >
            Herunterladen
            <v-icon right dark> mdi-cloud-download </v-icon>
          </v-btn>
        </p>

        <h3>Event verwalten</h3>
        <div class="flex-row flex-center">
          <v-card class="card event-card mt-6">
            <p v-if="!events || events.length === 0" class="mb-0">ℹ️ Keine Events vorhanden.</p>
            <div class="flex-row event" v-for="event in events" :key="event.id">
              <div class="flex-row flex-grow">
                <div v-if="!editingEventIds.includes(event.id)">
                  {{ event.name }}
                </div>
                <div v-if="editingEventIds.includes(event.id)">
                  <v-text-field
                    type="text"
                    v-model="event.name"
                    label="Titel des Event"
                    :variant="'underlined'"
                    required
                    :form="createFormName(event)"
                  />
                </div>
              </div>

              <div>
                <div class="flex-row">
                  <div v-if="!editingEventIds.includes(event.id)">
                    <a
                      target="_blank"
                      rel="noopener noreferrer"
                      :href="'/events/' + event.code"
                      class="link-button"
                    >
                      <v-icon medium class="mr-2"> mdi-open-in-new </v-icon>
                    </a>
                    <v-icon medium class="mr-2" @click.prevent="addToEditing(event)">
                      mdi-pencil
                    </v-icon>
                  </div>
                  <div v-if="editingEventIds.includes(event.id)">
                    <v-btn
                      type="sumbit"
                      :loading="loadingEventId === event.id"
                      :form="createFormName(event)"
                      rounded
                    >
                      <v-icon medium class="mr-2"> mdi-content-save </v-icon>
                    </v-btn>
                  </div>
                  <v-icon
                    medium
                    @click.prevent="deleteEventInteral(event.id)"
                    v-if="event.type === eventTypeLocation"
                  >
                    mdi-delete
                  </v-icon>
                  <div
                    style="width: 24px; height: 24px"
                    v-if="event.type !== eventTypeLocation"
                  ></div>
                </div>
              </div>
            </div>

            <form
              v-for="event in events"
              :key="'form-' + event.id"
              :id="createFormName(event)"
              v-on:submit.prevent="saveEvent(event)"
            />
          </v-card>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>
.flex-row {
  display: flex;
}
.v-row {
  padding: 0 14px;
}
.flex-grow {
  flex: 1 1 auto;
}
.flex-center {
  justify-content: center;
}
.event {
  margin-bottom: 12px;
}
.event-card {
  flex: 0 1 800px;
}
.link-button {
  text-decoration: none;
}
</style>
