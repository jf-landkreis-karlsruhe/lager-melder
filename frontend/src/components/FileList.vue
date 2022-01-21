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
        <button class="underline" @click="downloadAttendeesBW">
          Teilnehmerlisten
        </button>
        für den Landesjugendplan
      </li>
      <li>
        <button class="underline" @click="downloadYouthPlan">
          Pädagogische Betreuer
        </button>
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
  getAttendeesBW,
  getAttendeesCommunal,
  getAttendeesKarlsruhe,
  getYouthPlan,
} from "../services/registrationFiles";
import { showFile } from "../services/filesHelper";

@Component({})
export default class RegistrationFiles extends Vue {
  @Prop() departmentId!: string;
  @Prop() departmentName!: string;

  downloadYouthPlan = () => {
    getYouthPlan(this.departmentId, this.departmentName).then((fileData) =>
      showFile(fileData.data, fileData.fileName)
    );
  };

  downloadAttendeesKarlsruhe = () => {
    getAttendeesKarlsruhe(this.departmentId, this.departmentName).then(
      (fileData) => showFile(fileData.data, fileData.fileName)
    );
  };

  downloadAttendeesBW = () => {
    getAttendeesBW(this.departmentId, this.departmentName).then((fileData) =>
      showFile(fileData.data, fileData.fileName)
    );
  };

  downloadAttendeesCommunal = () => {
    getAttendeesCommunal(this.departmentId, this.departmentName).then(
      (fileData) => showFile(fileData.data, fileData.fileName)
    );
  };
}
</script>

<style scoped></style>
