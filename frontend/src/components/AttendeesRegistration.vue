<template>
  <div>
    <v-container>
      <h1 class="h1">Teilnehmer {{department.name}}</h1>
      <v-row justify="space-between">
        <div>
          <v-text-field prepend-icon="mdi-magnify" v-model="filterInput" label="Teilnehmerfilter" />
        </div>
        <div class="department-count">Anzahl Teilnehmer: {{attendees.length}}</div>
      </v-row>
    </v-container>
    <AttendeesTable headlineText="Jugendliche" :attendees="youthAttendees" />
    <AttendeesTable headlineText="Jugendleiter" :attendees="youthLeaderAttendees" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import {
  getAttendeesForMyDepartment,
  // eslint-disable-next-line no-unused-vars
  Attendee,
  AttendeeRole
} from "../services/attendee";

// eslint-disable-next-line no-unused-vars
import { Department, getMyDepartment } from "../services/department";

import AttendeesTable from "./AttendeesTable.vue";

@Component({
  components: { AttendeesTable }
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];
  department: Department = {} as Department;
  filterInput: string = "";

  get youthAttendees(): Attendee[] {
    return this.attendees
      .filter(attendee => attendee.role === AttendeeRole.YOUTH)
      .filter(this.filterByFilterInput);
  }

  get youthLeaderAttendees(): Attendee[] {
    return this.attendees
      .filter(attendee => attendee.role === AttendeeRole.YOUTH_LEADER)
      .filter(this.filterByFilterInput);
  }

  filterByFilterInput(attendee: Attendee) {
    if (this.filterInput.length > 0) {
      return (
        attendee.firstName.includes(this.filterInput) ||
        attendee.lastName.includes(this.filterInput) ||
        attendee.additionalInformation.includes(this.filterInput)
      );
    }
    return true;
  }

  mounted() {
    getAttendeesForMyDepartment().then(
      attendees => (this.attendees = attendees)
    );
    getMyDepartment().then(department => (this.department = department));
  }
}
</script>

<style scoped>
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
</style>
