<template>
  <div>
    <v-container>
      <v-row justify="space-between">
        <div class="search"></div>
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
  // getAttendeesForMyDepartment,
  // eslint-disable-next-line no-unused-vars
  Attendee,
  AttendeeRole,
  getAttendees
} from "../services/attendee";

import AttendeesTable from "./AttendeesTable.vue";

@Component({
  components: { AttendeesTable }
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];

  get youthAttendees(): Attendee[] {
    return this.attendees
      .filter(attendee => attendee.role === AttendeeRole.YOUTH)
      .slice(0, 10);
  }

  get youthLeaderAttendees(): Attendee[] {
    return this.attendees
      .filter(attendee => attendee.role === AttendeeRole.YOUTH_LEADER)
      .slice(0, 10);
  }

  mounted() {
    // getAttendeesForMyDepartment().then(
    getAttendees().then(attendees => (this.attendees = attendees));
  }
}
</script>

<style scoped>
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
</style>
