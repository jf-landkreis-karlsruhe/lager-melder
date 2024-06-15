<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { Attendee } from '../services/attendee'
import { getAttendees } from '../services/attendee'
import type { Department } from '../services/department'
import { getDepartments } from '../services/department'
import {
  filterEnteredAttendees,
  youthAttendees,
  youthLeaderAttendees
} from '../helper/filterHelper'
import { hasAdministrationRole as hasAdministrationRole } from '../services/authentication'
import { getBatches, getDepartmentOverview, getFoodPDF, getTShirtPDF } from '../services/adminFiles'
import { showFile } from '../services/filesHelper'
import type { Tents } from '@/services/tents'
import { getTents } from '@/services/tents'
import LmContainer from './LmContainer.vue'

interface DepartmentWithAttendees {
  department: Department
  youthAttendees: Attendee[]
  youthLeader: Attendee[]
}

const attendees = ref<Attendee[]>([])
const departments = ref<Department[]>([])
const filterInput = ref<string>('')
const totalTents = ref<Tents>({} as Tents)
const initialLoading = ref<boolean>(true)
const loadingBatches = ref<boolean>(false)
const loadingFood = ref<boolean>(false)
const loadingTshirt = ref<boolean>(false)
const loadingDepartmentOverview = ref<boolean>(false)

const departmentWithAttendees = computed<DepartmentWithAttendees[]>(() => {
  return departments.value
    .map((department) => {
      return {
        department: department,
        youthAttendees: youthAttendees(department.id, attendees.value, filterInput.value),
        youthLeader: youthLeaderAttendees(department.id, attendees.value, filterInput.value)
      }
    })
    .filter(
      (registration) =>
        registration.youthAttendees.length > 0 || registration.youthLeader.length > 0
    )
})

const downloadBatchesPDF = () => {
  loadingBatches.value = true
  getBatches().then((fileData) => {
    loadingBatches.value = false
    showFile(fileData.data, fileData.fileName)
  })
}

const downloadFoodPDF = () => {
  loadingFood.value = true
  getFoodPDF().then((fileData) => {
    loadingFood.value = false
    showFile(fileData.data, fileData.fileName)
  })
}

const downloadTShirtsPDF = () => {
  loadingTshirt.value = true
  getTShirtPDF().then((fileData) => {
    loadingTshirt.value = false
    showFile(fileData.data, fileData.fileName)
  })
}

const downloadDepartmentOverview = () => {
  loadingDepartmentOverview.value = true
  getDepartmentOverview().then((fileData) => {
    loadingDepartmentOverview.value = false
    showFile(fileData.data, fileData.fileName)
  })
}

const totalAttendeeCount = computed<number>(() => {
  return attendees.value.length
})

const enteredAttendeesCount = computed<number>(() => {
  return attendees.value.filter(filterEnteredAttendees).length
})

onMounted(() => {
  getAttendees().then((newAttendees) => {
    attendees.value = newAttendees
  })
  getDepartments().then((newDepartments) => (departments.value = newDepartments))
  getTents().then((tentsList) => {
    initialLoading.value = false
    totalTents.value = tentsList.reduce(
      (acc, current) => {
        acc.sg200 = acc.sg200 + current.sg200
        acc.sg20 = acc.sg20 + current.sg20
        acc.sg30 = acc.sg30 + current.sg30
        acc.sg40 = acc.sg40 + current.sg40
        acc.sg50 = acc.sg50 + current.sg50
        return acc
      },
      {
        sg200: 0,
        sg20: 0,
        sg30: 0,
        sg40: 0,
        sg50: 0
      } as Tents
    )
  })
})
</script>

<template>
  <LmContainer v-if="hasAdministrationRole()">
    <section class="mb-12">
      <h1>Lagerausweise</h1>
      <div class="d-flex align-center justify-space-between">
        <p class="mr-8">
          Hier können alle Lagerausweise heruntergeladen werden.
          <br />
          <v-btn
            color="var(--lm-c-accent)"
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
    </section>

    <section class="mb-12">
      <h1>Essensübersicht</h1>
      <div class="d-flex align-center justify-space-between">
        <p class="mr-8">
          Hier kann die Liste der Essen, die nicht Fleisch sind heruntergeladen werden.
          <br />
          <v-btn color="var(--lm-c-accent)" @click="downloadFoodPDF" small :loading="loadingFood">
            Herunterladen
            <v-icon right dark> mdi-cloud-download </v-icon>
          </v-btn>
        </p>
      </div>
    </section>

    <section class="mb-12">
      <h1>T-Shirt- und Armbandübersicht</h1>
      <div class="d-flex align-center justify-space-between">
        <p class="mr-8">
          Hier kann die Liste der TShirts und Armbänder pro Feuerwehr heruntergeladen werden.
          <br />
          <v-btn
            color="var(--lm-c-accent)"
            @click="downloadTShirtsPDF"
            small
            :loading="loadingTshirt"
          >
            Herunterladen
            <v-icon right dark> mdi-cloud-download </v-icon>
          </v-btn>
        </p>
      </div>
    </section>

    <section class="mb-12">
      <h1>Feuerwehr T-Shirt- und Armbandverteilliste</h1>
      <div class="d-flex align-center justify-space-between">
        <p class="mr-8">
          Hier kann eine Liste pro Feuerwehr zur Verteilung der TShirts und Armbänder
          heruntergeladen werden.
          <br />
          <v-btn
            color="var(--lm-c-accent)"
            @click="downloadDepartmentOverview"
            small
            :loading="loadingDepartmentOverview"
          >
            Herunterladen
            <v-icon right dark> mdi-cloud-download </v-icon>
          </v-btn>
        </p>
      </div>
    </section>

    <div class="tents-overview">
      <div v-if="initialLoading">
        <v-progress-circular indeterminate :size="24" />
      </div>
      <div v-else>
        <section class="mb-12">
          <h1>Zeltübersicht</h1>
          <div>
            <p class="mr-8">Hier stehen wie viele Zelte insgesamt angemeldet wurden.</p>
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
        </section>
      </div>
    </div>
  </LmContainer>

  <section class="mx-12 mb-12">
    <div class="d-flex align-baseline">
      <h1 class="mr-4 mb-2">Teilnehmer</h1>
      <div class="department-count">
        Gesamt: {{ totalAttendeeCount }} (Anwesend: {{ enteredAttendeesCount }})
      </div>
    </div>
    <v-row>
      <v-col cols="5">
        <v-text-field
          prepend-icon="mdi-magnify"
          v-model="filterInput"
          label="Filter nach Teilnehmer"
          variant="underlined"
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
        <h2 class="mr-4 my-8">Feuerwehr {{ registration.department.name }}</h2>
        <div class="department-count">
          Gesamt Teilnehmerzahl:
          {{ registration.youthAttendees.length + registration.youthLeader.length }}
        </div>
      </div>
    </div>
  </section>
</template>

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
