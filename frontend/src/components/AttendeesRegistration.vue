<template>
  <div>
    <v-container>
      <h1 class="h1">Teilnehmer {{ department.name }}</h1>
      <v-row justify="space-between">
        <div>
          <v-text-field
            prepend-icon="mdi-magnify"
            v-model="filterInput"
            label="Teilnehmerfilter"
          />
        </div>
        <div>
          Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend:
          {{ enteredAttendeesCount }})
        </div>
      </v-row>
    </v-container>
    <AttendeesTable
      headlineText="Jugendliche"
      :attendees="youthAttendees"
      :departmentId="department.id"
      :role="attendeeRoleYouth"
      :attendeesChanged="attendeesChanged"
    />
    <AttendeesTable
      headlineText="Jugendleiter"
      :attendees="youthLeaderAttendees"
      :role="attendeeRoleYouthLeader"
      :departmentId="department.id"
      :attendeesChanged="attendeesChanged"
    />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import {
  getAttendeesForMyDepartment,
  Attendee,
  AttendeeRole,
} from "../services/attendee";

// eslint-disable-next-line no-unused-vars
import { Department, getMyDepartment } from "../services/department";
import {
  youthLeaderAttendees,
  youthAttendees,
  filterEnteredAttendees,
} from "../helper/filterHelper";

import AttendeesTable from "./AttendeesTable.vue";

@Component({
  components: { AttendeesTable },
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];
  department: Department = {} as Department;
  filterInput: string = "";
  attendeeRoleYouth = AttendeeRole.YOUTH;
  attendeeRoleYouthLeader = AttendeeRole.YOUTH_LEADER;
  totalAttendeeCount: number = 0;

  get youthAttendees(): Attendee[] {
    if (!this.department || !this.department.id) {
      return [];
    }
    return youthAttendees(this.department.id, this.attendees, this.filterInput);
  }

  get youthLeaderAttendees(): Attendee[] {
    if (!this.department || !this.department.id) {
      return [];
    }
    return youthLeaderAttendees(
      this.department.id,
      this.attendees,
      this.filterInput
    );
  }

  attendeesChanged(change: number) {
    this.totalAttendeeCount += change;
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

  get enteredAttendeesCount(): number {
    return this.attendees.filter(filterEnteredAttendees).length;
  }

  mounted() {
    getAttendeesForMyDepartment().then((attendees) => {
      this.attendees = attendees;
      this.totalAttendeeCount = attendees.length;
    });
    getMyDepartment().then((department) => (this.department = department));
  }
}
</script>

<style scoped>
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
</style>
