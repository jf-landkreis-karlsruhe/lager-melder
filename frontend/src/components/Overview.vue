<template>
  <div>
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
        <h1 class="h1">Teilnehmer {{ registration.department.name }}</h1>
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

import {
  getAttendees,
  // eslint-disable-next-line no-unused-vars
  Attendee,
  AttendeeRole
} from "../services/attendee";

import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments
} from "../services/department";

import AttendeesTable from "./AttendeesTable.vue";

interface DepartmentWithAttendees {
  department: Department;
  youthAttendees: Attendee[];
  youthLeader: Attendee[];
}

@Component({
  components: { AttendeesTable }
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];
  departments: Department[] = [];
  filterInput: string = "";
  attendeeRoleYouth = AttendeeRole.YOUTH;
  attendeeRoleYouthLeader = AttendeeRole.YOUTH_LEADER;

  youthAttendees = (
    departmentId: string,
    attendees: Attendee[]
  ): Attendee[] => {
    return attendees
      .filter(attendee => attendee.departmentId === departmentId)
      .filter(attendee => attendee.role === AttendeeRole.YOUTH)
      .filter(this.filterByFilterInput);
  };

  youthLeaderAttendees = (
    departmentId: string,
    attendees: Attendee[]
  ): Attendee[] => {
    return attendees
      .filter(attendee => attendee.departmentId === departmentId)
      .filter(attendee => attendee.role === AttendeeRole.YOUTH_LEADER)
      .filter(this.filterByFilterInput);
  };

  get departmentWithAttendees(): DepartmentWithAttendees[] {
    return this.departments
      .map(department => {
        return {
          department: department,
          youthAttendees: this.youthAttendees(department.id, this.attendees),
          youthLeader: this.youthLeaderAttendees(department.id, this.attendees)
        };
      })
      .filter(
        registration =>
          registration.youthAttendees.length > 0 ||
          registration.youthLeader.length > 0
      );
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
