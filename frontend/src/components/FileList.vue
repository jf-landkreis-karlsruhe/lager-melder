<template>
  <div>
    <ul>
      <li>
        <button class="underline" @click="downloadAttendeesCommunal">
          Anmeldung
        </button>
        mit Unterschrift des Kommandanten
      </li>
      <li>
        <button class="underline" @click="downloadStateYouthPlanAttendees">
          Teilnehmerlisten
        </button>
        für den Landesjugendplan
      </li>
      <li>
        <button class="underline" @click="downloadStateYouthPlanLeader">
          Pädagogische Betreuer
        </button>
        für den Landesjugendplan
      </li>
      <li>
        <button class="underline" @click="downloadAttendeesKarlsruhe">
          Teilnehmerliste
        </button>
        für den Zuschuss des Landkreis Karlsruhe
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

import {
  getStateYouthPlanAttendees,
  getAttendeesCommunal,
  getAttendeesKarlsruhe,
  getStateYouthPlanLeader,
} from "../services/registrationFiles";
import { showFile } from "../services/filesHelper";

@Component({})
export default class RegistrationFiles extends Vue {
  @Prop() departmentId!: string;
  @Prop() departmentName!: string;

  downloadStateYouthPlanLeader() {
    getStateYouthPlanLeader(this.departmentId, this.departmentName).then(
      (fileData) => showFile(fileData.data, fileData.fileName)
    );
  }

  downloadAttendeesKarlsruhe() {
    getAttendeesKarlsruhe(this.departmentId, this.departmentName).then(
      (fileData) => showFile(fileData.data, fileData.fileName)
    );
  }

  downloadStateYouthPlanAttendees() {
    getStateYouthPlanAttendees(this.departmentId, this.departmentName).then(
      (fileData) => showFile(fileData.data, fileData.fileName)
    );
  }

  downloadAttendeesCommunal() {
    getAttendeesCommunal(this.departmentId, this.departmentName).then(
      (fileData) => showFile(fileData.data, fileData.fileName)
    );
  }
}
</script>

<style scoped></style>
