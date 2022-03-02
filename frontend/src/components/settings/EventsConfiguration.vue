<template>
  <v-card>
    <h2>Events</h2>
    <h3>Event erstellen</h3>
    <form v-on:submit.prevent="createEventInternal()">
      <v-text-field v-model="eventName" label="Titel des Event" required />
      <v-row class="v-row" justify="end">
        <v-btn color="primary" :loading="loadingEventId === '0'" type="submit">
          <span>Erstellen</span>
        </v-btn>
      </v-row>
    </form>

    <h2>Event QR Codes</h2>
    <p>
      Die QR Codes für alle Events herrunterladen.
      <v-btn
        class="underline"
        :loading="loadingDownload"
        @click="downloadEventsPDF"
      >
        Download
      </v-btn>
    </p>

    <h3>Event verwalten</h3>
    <div class="flex-row flex-center">
      <v-card class="card event-card mt-6">
        <p v-if="!events || events.length === 0" class="mb-0">
          ℹ️ Keine Events vorhanden.
        </p>
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
                required
                :form="createFormName(event)"
              />
            </div>
          </div>

          <div>
            <div class="flex-row">
              <div v-if="!editingEventIds.includes(event.id)">
                <v-icon
                  medium
                  class="mr-2"
                  @click.prevent="addToEditing(event)"
                >
                  mdi-pencil
                </v-icon>
              </div>
              <div v-if="editingEventIds.includes(event.id)">
                <v-btn
                  type="sumbit"
                  :loading="loadingEventId === event.id"
                  :form="createFormName(event)"
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
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  createEvent,
  getEvents,
  // eslint-disable-next-line no-unused-vars
  Event,
  deleteEvent,
  updateEvent,
  EventType,
} from "../../services/event";
import { getEventCodes } from "../../services/adminFiles";
import { showFile } from "../../services/filesHelper";

@Component({ name: "EventsConfiguration" })
export default class EventsConfiguration extends Vue {
  eventName: string = "";
  events: Event[] = [];
  editingEventIds: string[] = [];
  eventTypeLocation: EventType = EventType.LOCATION;

  loadingEventId: string = "";
  loadingDownload: boolean = false;

  mounted() {
    getEvents().then((data) => (this.events = data));
  }

  addToEditing(event: Event) {
    this.editingEventIds.push(event.id);
  }

  createEventInternal() {
    this.loadingEventId = "0";
    createEvent({ name: this.eventName })
      .then(() => {
        this.eventName = "";
        this.loadingEventId = "";
        return getEvents();
      })
      .then((data) => (this.events = data));
  }

  saveEvent(event: Event) {
    this.loadingEventId = event.id;
    updateEvent(event).then((data: Event) => {
      const indexOfAttendee = this.editingEventIds.indexOf(data.id);
      this.editingEventIds.splice(indexOfAttendee, 1);
      this.loadingEventId = "";
    });
  }

  createFormName(event: Event) {
    return `form-event-${event.id}`;
  }

  deleteEventInteral(id: string) {
    deleteEvent(id)
      .then(() => {
        return getEvents();
      })
      .then((data) => (this.events = data));
  }

  downloadEventsPDF = () => {
    this.loadingDownload = true;
    getEventCodes().then((fileData) => {
      showFile(fileData.data, fileData.fileName);
      this.loadingDownload = false;
    });
  };
}
</script>

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
</style>
