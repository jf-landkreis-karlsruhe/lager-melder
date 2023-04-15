<template>
  <v-container>
    <h1>Anmeldeunterlagen</h1>
    <div
      v-if="
        startDownloadRegistrationFiles &&
        startDownloadRegistrationFiles.registrationFilesCanBeDownloaded
      "
    >
      <p>
        Die hier aufgelisteten Dateien sind für die Anmeldung am Kreiszeltlager.
        Die Dateien sind mit den angemeldeten Teilnehmerlisten vorbefüllt und
        sollen so bei der unterschrieben Anmeldung abgegeben werden, damit eine
        reibungslose Anmeldung gewährleistet werden kann.
      </p>
      <FileList :departmentId="departmentId" :departmentName="departmentName" />
      <p>
        ⚠️ In einigen PDF Viewern kommt es zu Probleme mit der Anzeige, es
        funktionieren mit Google Chrome und Adobe Acrobat Reader. ⚠️
      </p>
      <div>
        <YouthPlanDistribution />
      </div>
      <div v-if="hasAdministrationRole() && departments">
        <h2>Anmeldeunterlagen aller Feuerwehren</h2>
        <div v-for="department in departments" :key="department.id">
          <h3>Feuerwehr {{ department.name }}</h3>
          <FileList
            :departmentId="department.id"
            :departmentName="department.name"
          />
          <br />
        </div>
      </div>
    </div>
    <div
      v-if="
        startDownloadRegistrationFiles &&
        !startDownloadRegistrationFiles.registrationFilesCanBeDownloaded
      "
    >
      Die Anmeldeunterlagen sind noch nicht bereit zum Herunterladen. Sie stehen
      ab {{ localizedStartDate && localizedStartDate }} zur Verfügung.
    </div>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import FileList from "./FileList.vue";
import YouthPlanDistribution from "./YouthPlanDistribution.vue";

import { hasAdministrationRole } from "../services/authentication";
import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments,
  getMyDepartment,
} from "../services/department";
import {
  getStartDownloadRegistrationFiles,
  StartDownloadRegistrationFiles,
} from "../services/settings";
import { dateLocalized } from "@/helper/displayDate";

@Component({
  components: { FileList, YouthPlanDistribution },
})
export default class RegistrationFiles extends Vue {
  departmentId: string = "";
  departmentName: string = "";
  departments?: Department[] = [];
  startDownloadRegistrationFiles: StartDownloadRegistrationFiles | null = null;
  localizedStartDate: string | null = null;

  hasAdministrationRole = hasAdministrationRole;
  mounted() {
    getMyDepartment()
      .then((department) => {
        this.departmentName = department.name;
        this.departmentId = department.id;
      })
      .then(() => {
        if (hasAdministrationRole()) {
          return getDepartments();
        }
      })
      .then((departments) => {
        this.departments = departments?.filter(
          (department) => department.id !== this.departmentId
        );
      });

    getStartDownloadRegistrationFiles().then(
      (startDownloadRegistrationFiles) => {
        this.startDownloadRegistrationFiles = startDownloadRegistrationFiles;
        this.localizedStartDate = dateLocalized(
          startDownloadRegistrationFiles.startDownloadRegistrationFiles
        );
      }
    );
  }
}
</script>

<style scoped></style>
