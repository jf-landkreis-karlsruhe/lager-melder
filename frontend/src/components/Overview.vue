<template>
  <div v-if="hasAdministrationRole()">
    <Distribution :attendees="attendees" />
    <h1>Lagerausweise</h1>
    <p>
      Hier können alle Lagerausweise heruntergeladen werden: herunterladen
      <button class="underline" @click="downloadBatchesPDF">Download</button>
    </p>
    <h1>Teilnehmer</h1>
    <div>
      <v-text-field
        prepend-icon="mdi-magnify"
        v-model="filterInput"
        label="Teilnehmerfilter"
      />
    </div>
    <div
      v-for="registration in departmentWithAttendees"
      :key="registration.department.id"
    >
      <v-container>
        <h2 class="h1">Teilnehmer {{ registration.department.name }}</h2>
        <v-row justify="end">
          <div class="department-count">
            Anzahl Teilnehmer:
            {{
              registration.youthAttendees.length +
                registration.youthLeader.length
            }}
          </div>
        </v-row>
      </v-container>
      <AttendeesTable
        headlineText="Jugendliche"
        :attendees="registration.youthAttendees"
        :departmentId="registration.department.id"
        :role="attendeeRoleYouth"
      />
      <AttendeesTable
        headlineText="Jugendleiter"
        :attendees="registration.youthLeader"
        :departmentId="registration.department.id"
        :role="attendeeRoleYouthLeader"
      />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import Distribution from "./Distribution.vue";
import {
  getAttendees,
  // eslint-disable-next-line no-unused-vars
  Attendee,
  AttendeeRole,
  // eslint-disable-next-line no-unused-vars
  Food,
  // eslint-disable-next-line no-unused-vars
  TShirtSize
} from "../services/attendee";

import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments
} from "../services/department";

import AttendeesTable from "./AttendeesTable.vue";
import { youthLeaderAttendees, youthAttendees } from "../helper/filterHelper";

import { hasAdministrationRole } from "../services/authentication";
import { getBatches } from "../services/adminFiles";
import { showFile } from "../services/filesHelper";

interface DepartmentWithAttendees {
  department: Department;
  youthAttendees: Attendee[];
  youthLeader: Attendee[];
}

@Component({
  components: { AttendeesTable, Distribution }
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];
  departments: Department[] = [];
  filterInput: string = "";
  attendeeRoleYouth = AttendeeRole.YOUTH;
  attendeeRoleYouthLeader = AttendeeRole.YOUTH_LEADER;
  hasAdministrationRole = hasAdministrationRole;

  get departmentWithAttendees(): DepartmentWithAttendees[] {
    return this.departments
      .map(department => {
        return {
          department: department,
          youthAttendees: youthAttendees(
            department.id,
            this.attendees,
            this.filterInput
          ),
          youthLeader: youthLeaderAttendees(
            department.id,
            this.attendees,
            this.filterInput
          )
        };
      })
      .filter(
        registration =>
          registration.youthAttendees.length > 0 ||
          registration.youthLeader.length > 0
      );
  }

  downloadBatchesPDF = () => {
    getBatches().then(fileData => showFile(fileData.data, fileData.fileName));
  };

  mounted() {
    getAttendees().then(attendees => {
      this.attendees = attendees;
    });
    getDepartments().then(departments => (this.departments = departments));
  }
}
</script>

<style scoped>
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
</style>
