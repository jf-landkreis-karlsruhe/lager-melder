<template>
  <v-container>
    <h1>Events</h1>
    <h2>Event erstellen</h2>
    <form v-on:submit.prevent="createEventInternal()">
      <v-text-field v-model="eventName" label="Titel des Event" required />
      <v-alert v-if="error" type="error">
        Es gab einen Fehler beim erzeugen der Feuerwehr oder des Benutzers.
      </v-alert>
      <v-row justify="end">
        <v-btn color="primary" :loading="loading" type="submit">
          <span v-if="created">
            <v-icon medium>mdi-check</v-icon> Erstellt
          </span>
          <span v-if="!created"> Erstellen </span>
        </v-btn>
      </v-row>
    </form>

    <h2>Event QR Codes</h2>
    <p>
      Die QR Codes für alle Events herrunterladen.
      <button class="underline" @click="downloadEventsPDF">Download</button>
    </p>

    <h2>Event verwalten</h2>
    <div class="flex-row flex-center">
      <v-card class="card event-card">
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
                <button type="sumbit" :form="createFormName(event)">
                  <v-icon medium class="mr-2"> mdi-content-save </v-icon>
                </button>
              </div>
              <v-icon medium @click.prevent="deleteEventInteral(event.id)">
                mdi-delete
              </v-icon>
            </div>
          </div>
        </div>

        <form
          v-for="event in events"
          :key="event.id"
          :id="createFormName(event)"
          v-on:submit.prevent="saveEvent(event)"
        />
      </v-card>
    </div>
  </v-container>
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
} from "../services/event";
import { getEventCodes } from "../services/adminFiles";
import { showFile } from "../services/filesHelper";

@Component({ name: "EventsConfiguration" })
export default class EventsConfiguration extends Vue {
  eventName: string = "";
  events: Event[] = [];
  editingEventIds: string[] = [];

  // ToDO: needed?
  created: boolean = false;
  loading: boolean = false;

  networkError: string = "";

  mounted() {
    getEvents().then((data) => (this.events = data));
  }

  addToEditing(event: Event) {
    this.editingEventIds.push(event.id);
  }

  createEventInternal() {
    createEvent({ name: this.eventName })
      .then(() => {
        this.eventName = "";
        return getEvents();
      })
      .then((data) => (this.events = data));
  }

  saveEvent(event: Event) {
    console.log("save", event);
    updateEvent(event).then((data: Event) => {
      const indexOfAttendee = this.editingEventIds.indexOf(data.id);
      this.editingEventIds.splice(indexOfAttendee, 1);
    });
  }

  createFormName(event: Event) {
    return `form-${event.id}`;
  }

  deleteEventInteral(id: string) {
    deleteEvent(id)
      .then(() => {
        return getEvents();
      })
      .then((data) => (this.events = data));
  }

  downloadEventsPDF = () => {
    getEventCodes().then((fileData) =>
      showFile(fileData.data, fileData.fileName)
    );
  };
}
</script>

<style scoped>
.flex-row {
  display: flex;
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
.card {
  padding: 14px;
  margin-bottom: 30px;
  max-width: 800px;
}
</style>
