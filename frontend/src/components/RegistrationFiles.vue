<template>
  <div>
    <h1>Anmeldeunterlagen</h1>
    <p>
      Die hier aufgelisteten Dateien sind für die Anmeldung am Kreiszeltlager.
      Die Dateien sind mit den angemeldeten Teilnehmerlisten vorbefüllt und
      sollen so bei der unterschrieben Anmeldung abgegeben werden, damit eine
      reibungslose Anmeldung gewährleistet werden kann.
    </p>
    <FileList :departmentId="departmentId" :departmentName="departmentName" />
    <p>
      In einegen PDF Viewern kommt es zu Probleme mit der Anzeige, funktionieren
      tut es mit Google Chrome und Adobe Acrobat Reader.
    </p>
    <div v-if="hasAdministrationRole() && departments">
      <h2>Anmeldeunterlagen aller Feuerwehren</h2>
      <div v-for="department in departments" :key="department.id">
        <h3>Feuerwehr {{ department.name }}</h3>
        <FileList
          :departmentId="department.id"
          :departmentName="department.name"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import FileList from "./FileList.vue";

import { hasAdministrationRole } from "../services/authentication";
import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments,
  getMyDepartment
} from "../services/department";

@Component({
  components: { FileList }
})
export default class RegistrationFiles extends Vue {
  departmentId: string = "";
  departmentName: string = "";
  departments?: Department[] = [];

  hasAdministrationRole = hasAdministrationRole;
  mounted() {
    getMyDepartment()
      .then(department => {
        this.departmentName = department.name;
        this.departmentId = department.id;
      })
      .then(() => {
        if (hasAdministrationRole()) {
          return getDepartments();
        }
      })
      .then(departments => {
        this.departments = departments?.filter(
          department => department.id !== this.departmentId
        );
      });
  }
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>
