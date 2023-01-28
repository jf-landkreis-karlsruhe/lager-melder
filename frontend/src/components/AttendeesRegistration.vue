<template>
  <v-container>
    <div>
      <v-alert color="yellow darken-2" type="warning" border="top">
        <div style="color: #333333">
          <b>
            Anmeldeschluss {{ registrationEndDiff ? "ist" : "war" }} am:
            {{ registrationEndLocalized }} Uhr
          </b>
          <br />
          <div v-if="registrationEndDiff">
            Das sind noch {{ registrationEndDiff.days }}
            {{ registrationEndDiff.days === 1 ? "Tag" : "Tage" }}
            {{ registrationEndDiff.hours }}
            {{ registrationEndDiff.hours === 1 ? "Stunde" : "Stunden" }}
            {{ registrationEndDiff.minutes }}
            {{ registrationEndDiff.minutes === 1 ? "Minute" : "Minuten" }}
            <!-- {{ registrationEndDiff.seconds }}
            {{ registrationEndDiff.seconds === 1 ? "Sekunde" : "Sekunden" }} -->
          </div>
          <div v-else>Anmeldeschluss ist bereits erreicht!</div>
        </div>
      </v-alert>

      <div class="d-flex align-baseline">
        <h1 class="mr-4">Teilnehmer {{ department.name }}</h1>
        <div>
          Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend:
          {{ enteredAttendeesCount }})
        </div>
      </div>
      <v-row>
        <v-col cols="4">
          <v-text-field
            prepend-icon="mdi-magnify"
            v-model="filterInput"
            label="Teilnehmerfilter"
          />
        </v-col>
      </v-row>
    </div>
    <AttendeesTable
      headlineText="Jugendliche"
      :attendees="youthAttendees"
      :departmentId="department.id"
      :role="attendeeRoleYouth"
      :attendeesChanged="attendeesChanged"
      :disabled="!attendeesCanBeEdited"
    />
    <AttendeesTable
      headlineText="Jugendleiter"
      :attendees="youthLeaderAttendees"
      :role="attendeeRoleYouthLeader"
      :departmentId="department.id"
      :attendeesChanged="attendeesChanged"
      :disabled="!attendeesCanBeEdited"
    />

    <TentsPreregistration :departmentId="department.id" />
  </v-container>
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
import TentsPreregistration from './TentsPreregistration.vue';
import { getRegistrationEnd } from "@/services/settings";
import { dateTimeLocalized } from "@/helper/displayDate";

@Component({
  components: { AttendeesTable, TentsPreregistration },
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];
  department: Department = {} as Department;
  filterInput: string = "";
  attendeeRoleYouth = AttendeeRole.YOUTH;
  attendeeRoleYouthLeader = AttendeeRole.YOUTH_LEADER;
  totalAttendeeCount: number = 0;
  private now = new Date();
  private registrationEnd: Date | null = null;
  private registrationEndLocalized: string = "";
  private attendeesCanBeEdited: boolean = false;

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

  public async created(): Promise<void> {
    const response = await getRegistrationEnd();
    this.registrationEnd = response.registrationEnd;
    this.registrationEndLocalized = dateTimeLocalized(response.registrationEnd);
    this.attendeesCanBeEdited = response.attendeesCanBeEdited;

    setInterval(() => {
      this.now = new Date();
    }, 1000);
  }

  private get registrationEndDiff():
    | {
        days: number;
        minutes: number;
        hours: number;
        seconds: number;
      }
    | undefined {
    if (!this.registrationEnd) {
      return undefined;
    }
    const diff = this.registrationEnd.getTime() - this.now.getTime();
    if (diff < 0) return undefined;

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);
    return { days, hours, minutes, seconds };
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
