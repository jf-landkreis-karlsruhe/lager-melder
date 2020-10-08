<template>
  <p>
    <ul>
      <li>
        <button
          class="underline"
          @click="downloadAttendeesCommunal"
        >
          Anmeldung
        </button> mit Unterschrift des Kommandanten
      </li>
      <li>
        <button
          class="underline"
          @click="downloadAttendeesBW"
        >
          Teilnehmerlisten
        </button> für den Landesjugendplan
      </li>
      <li>
        <button
          class="underline"
          @click="downloadYouthPlan"
        >
          Pädagogische Betreuer
        </button>
      </li>
      <li>
        <button
          class="underline"
          @click="downloadAttendeesKarlsruhe"
        >
        Teilnehmerliste
        </button>
          für den Zuschuss des Landkreis Karlsruhe
      </li>
    </ul>
  </p>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

import {
  getAttendeesBW,
  getAttendeesCommunal,
  getAttendeesKarlsruhe,
  getYouthPlan
} from "../services/registrationFiles";

@Component({})
export default class RegistrationFiles extends Vue {
  @Prop() departmentId!: string;
  @Prop() departmentName!: string;

  showFile(blob: Blob, fileName: string) {
    // It is necessary to create a new blob object with mime-type explicitly set
    // otherwise only Chrome works like it should
    var newBlob = new Blob([blob], { type: "application/pdf" });

    // IE doesn't allow using a blob object directly as link href
    // instead it is necessary to use msSaveOrOpenBlob
    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
      window.navigator.msSaveOrOpenBlob(newBlob);
      return;
    }

    // For other browsers:
    // Create a link pointing to the ObjectURL containing the blob.
    const data = window.URL.createObjectURL(newBlob);
    var link = document.createElement("a");
    link.href = data;
    link.download = fileName;
    link.click();
    setTimeout(function() {
      // For Firefox it is necessary to delay revoking the ObjectURL
      window.URL.revokeObjectURL(data);
    }, 100);
  }

  downloadYouthPlan = () => {
    getYouthPlan(this.departmentId, this.departmentName).then(fileData =>
      this.showFile(fileData.data, fileData.fileName)
    );
  };

  downloadAttendeesKarlsruhe = () => {
    getAttendeesKarlsruhe(
      this.departmentId,
      this.departmentName
    ).then(fileData => this.showFile(fileData.data, fileData.fileName));
  };

  downloadAttendeesBW = () => {
    getAttendeesBW(this.departmentId, this.departmentName).then(fileData =>
      this.showFile(fileData.data, fileData.fileName)
    );
  };

  downloadAttendeesCommunal = () => {
    getAttendeesCommunal(
      this.departmentId,
      this.departmentName
    ).then(fileData => this.showFile(fileData.data, fileData.fileName));
  };
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>