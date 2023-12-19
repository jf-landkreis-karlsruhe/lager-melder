<template>
  <v-container v-if="hasAdministrationRole()">
    <h1>Lagerausweise</h1>
    <div class="d-flex align-center justify-space-between">
      <p class="mr-8">
        Hier können alle Lagerausweise heruntergeladen werden.
        <br />
        <v-btn
          color="#ffe760"
          @click="downloadBatchesPDF"
          small
          :loading="loadingBatches"
        >
          Herunterladen
          <v-icon right dark> mdi-cloud-download </v-icon>
        </v-btn>
      </p>
      <img
        src="../assets/Zeltlager-Ausweis-Beispiel.png"
        alt="Beispiel eines Teilnehmer-Ausweises"
        title="Beispiel eines Teilnehmer-Ausweises"
        width="20%"
        height="auto"
      />
    </div>

    <h1>Essensübersicht</h1>
    <div class="d-flex align-center justify-space-between">
      <p class="mr-8">
        Hier kann die Liste der Essen, die nicht Fleisch sind heruntergeladen
        werden.
        <br />
        <v-btn
          color="#ffe760"
          @click="downloadFoodPDF"
          small
          :loading="loadingFood"
        >
          Herunterladen
          <v-icon right dark> mdi-cloud-download </v-icon>
        </v-btn>
      </p>
    </div>

    <h1>T-Shirt- und Armbandübersicht</h1>
    <div class="d-flex align-center justify-space-between">
      <p class="mr-8">
        Hier kann die Liste der TShirts und Armbänder pro Feuerwehr
        heruntergeladen werden.
        <br />
        <v-btn
          color="#ffe760"
          @click="downloadTShirtsPDF"
          small
          :loading="loadingTshirt"
        >
          Herunterladen
          <v-icon right dark> mdi-cloud-download </v-icon>
        </v-btn>
      </p>
    </div>

    <h1>Feuerwehr T-Shirt- und Armbandverteilliste</h1>
    <div class="d-flex align-center justify-space-between">
      <p class="mr-8">
        Hier kann eine Liste pro Feuerwehr zur Verteilung der TShirts und
        Armbänder heruntergeladen werden.
        <br />
        <v-btn
          color="#ffe760"
          @click="downloadDepartmentOverview"
          small
          :loading="loadingDepartmentOverview"
        >
          Herunterladen
          <v-icon right dark> mdi-cloud-download </v-icon>
        </v-btn>
      </p>
    </div>

    <div v-if="initialLoanding">
      <v-progress-circular indeterminate :size="24" />
    </div>
    <div v-if="!initialLoanding">
      <h1>Zeltübersicht</h1>
      <div>
        <p class="mr-8">
          Hier stehen wie viele Zelte insgesamt angemeldet wurden.
        </p>
        <v-simple-table>
          <template v-slot:default>
            <thead>
              <tr>
                <th class="text-left">Größe (Länge x Breite)</th>
                <th class="text-left">Anzahl</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>SG 200 (4m x 5,9m)</td>
                <td>{{ totalTents.sg200 }}</td>
              </tr>
              <tr>
                <td>SG 20 (5m x 4,74m)</td>
                <td>{{ totalTents.sg20 }}</td>
              </tr>
              <tr>
                <td>SG 30 (6m x 5,64m)</td>
                <td>{{ totalTents.sg30 }}</td>
              </tr>
              <tr>
                <td>SG 40 (8m x 5,64m)</td>
                <td>{{ totalTents.sg40 }}</td>
              </tr>
              <tr>
                <td>SG 50 (10m x 5,64m)</td>
                <td>{{ totalTents.sg50 }}</td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>
      </div>

      <div class="d-flex align-baseline">
        <h1 class="mr-4 mb-2">Teilnehmer</h1>
        <div class="department-count">
          Gesamt: {{ totalAttendeeCount }} (Anwesend:
          {{ enteredAttendeesCount }})
        </div>
      </div>
      <v-row>
        <v-col cols="5">
          <v-text-field
            prepend-icon="mdi-magnify"
            v-model="filterInput"
            label="Filter nach Teilnehmer"
            class="w"
          />
        </v-col>
      </v-row>

      <div
        v-for="registration in departmentWithAttendees"
        :key="registration.department.id"
        class="indented-1"
      >
        <div class="d-flex align-baseline">
          <h2 class="mr-4 my-8">
            Feuerwehr {{ registration.department.name }}
          </h2>
          <div class="department-count">
            Gesamt Teilnehmerzahl:
            {{
              registration.youthAttendees.length +
              registration.youthLeader.length
            }}
          </div>
          <div class="d-flex justify-end align-center flex-grow-1">
            <v-btn
              @click="checkinDepartment(registration.department.id)"
              class="checkin"
              rounded
            >
              ⛺ Teilnehmer {{ registration.department.name }} einchecken
            </v-btn>
          </div>
        </div>
        <div class="indented-2">
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
    </div>

    <!-- <Distribution :attendees="attendees" /> -->
  </v-container>
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
  TShirtSize,
  AttendeeStatus,
} from "../services/attendee";

import {
  checkinDepartmentToEvent,
  EventType,
  getEventByType,
  Event,
} from "../services/event";

import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments,
} from "../services/department";

import AttendeesTable from "./AttendeesTable.vue";
import TentsPreregistration from "./TentsPreregistration.vue";
import {
  youthLeaderAttendees,
  youthAttendees,
  filterEnteredAttendees,
} from "../helper/filterHelper";

import { hasAdministrationRole } from "../services/authentication";
import {
  getBatches,
  getDepartmentOverview,
  getFoodPDF,
  getTShirtPDF,
} from "../services/adminFiles";
import { showFile } from "../services/filesHelper";
import { getTents, Tents } from "@/services/tents";

interface DepartmentWithAttendees {
  department: Department;
  youthAttendees: Attendee[];
  youthLeader: Attendee[];
}

@Component({
  components: { AttendeesTable, Distribution, TentsPreregistration },
})
export default class AttendeesRegistration extends Vue {
  attendees: Attendee[] = [];
  departments: Department[] = [];
  filterInput: string = "";
  attendeeRoleYouth = AttendeeRole.YOUTH;
  attendeeRoleYouthLeader = AttendeeRole.YOUTH_LEADER;
  hasAdministrationRole = hasAdministrationRole;
  totalTents: Tents = {} as Tents;
  enterEvent: Event = {} as Event;
  initialLoanding = true;
  loadingBatches = false;
  loadingFood = false;
  loadingTshirt = false;
  loadingDepartmentOverview = false;

  get departmentWithAttendees(): DepartmentWithAttendees[] {
    return this.departments
      .map((department) => {
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
          ),
        };
      })
      .filter(
        (registration) =>
          registration.youthAttendees.length > 0 ||
          registration.youthLeader.length > 0
      );
  }

  downloadBatchesPDF() {
    this.loadingBatches = true;
    getBatches().then((fileData) => {
      this.loadingBatches = false;
      showFile(fileData.data, fileData.fileName);
    });
  }

  downloadFoodPDF() {
    this.loadingFood = true;
    getFoodPDF().then((fileData) => {
      this.loadingFood = false;
      showFile(fileData.data, fileData.fileName);
    });
  }

  downloadTShirtsPDF() {
    this.loadingTshirt = true;
    getTShirtPDF().then((fileData) => {
      this.loadingTshirt = false;
      showFile(fileData.data, fileData.fileName);
    });
  }

  downloadDepartmentOverview() {
    this.loadingDepartmentOverview = true;
    getDepartmentOverview().then((fileData) => {
      this.loadingDepartmentOverview = false;
      showFile(fileData.data, fileData.fileName);
    });
  }

  get totalAttendeeCount(): number {
    return this.attendees.length;
  }

  get enteredAttendeesCount(): number {
    return this.attendees.filter(filterEnteredAttendees).length;
  }

  checkinDepartment(departmentId: string) {
    checkinDepartmentToEvent(this.enterEvent, departmentId).then(() => {
      this.attendees = this.attendees.map((att) => {
        if (att.departmentId === departmentId) {
          att.status = AttendeeStatus.ENTERED;
        }
        return att;
      });
    });
  }

  mounted() {
    getAttendees().then((attendees) => {
      this.attendees = attendees;
    });
    getDepartments().then((departments) => (this.departments = departments));
    getEventByType(EventType.GLOBAL_ENTER).then(
      (event: Event) => (this.enterEvent = event)
    );
    getTents().then((tentsList) => {
      this.initialLoanding = false;
      this.totalTents = tentsList.reduce(
        (acc, current) => {
          acc.sg200 = acc.sg200 + current.sg200;
          acc.sg20 = acc.sg20 + current.sg20;
          acc.sg30 = acc.sg30 + current.sg30;
          acc.sg40 = acc.sg40 + current.sg40;
          acc.sg50 = acc.sg50 + current.sg50;
          return acc;
        },
        {
          sg200: 0,
          sg20: 0,
          sg30: 0,
          sg40: 0,
          sg50: 0,
        } as Tents
      );
    });
  }
}
</script>

<style scoped lang="scss">
.indented-1 {
  margin-left: 16px;
  margin-right: 16px;
}
.indented-2 {
  margin-left: 26px;
  margin-right: 16px;
}
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
.v-btn.checkin {
  background-color: #00ff0030;
}
</style>
