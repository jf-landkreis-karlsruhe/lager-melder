<template>
  <v-card>
    <h2>PCR Test Serien</h2>
    <h3>PCR Test Serie erstellen</h3>
    <form v-on:submit.prevent="createPcrTestSeriesInternal()">
      <v-text-field
        v-model="newPcrTestName"
        label="Titel der PCR Test Serie"
        required
      />
      <v-text-field
        v-model="newPcrTestCodes"
        label="PCR Test Serie Testcodes als Kommaseparierte Liste"
        required
      />
      <div justify="space-around" class="px-md-12 d-flex flex-wrap">
        <v-col class="d-flex flex-column px-lg-12">
          <label for="pcr-start-date">Startdatum</label>
          <v-date-picker
            id="pcr-start-date"
            v-model="newStartDate"
          ></v-date-picker>
        </v-col>
        <v-col class="d-flex flex-column px-lg-12">
          <label for="pcr-end-date">Endedatum</label>
          <v-date-picker id="pcr-end-date" v-model="newEndDate"></v-date-picker>
        </v-col>
      </div>

      <v-row class="v-row mb-8" justify="end">
        <v-btn
          color="primary"
          :loading="loadingPcrTestId === '0'"
          type="submit"
        >
          <span>Erstellen</span>
        </v-btn>
      </v-row>
    </form>

    <h3>PCR Test Serien verwalten</h3>
    <div class="flex-row flex-center">
      <v-card class="card pcr-test-card mt-6">
        <p v-if="!pcrTests || pcrTests.length === 0" class="mb-0">
          ℹ️ Keine PCR Test Serien vorhanden.
        </p>
        <div
          class="flex-row"
          v-for="pcrTestSeries in pcrTests"
          :key="pcrTestSeries.id"
        >
          <div class="flex-row flex-grow mb-4">
            <div v-if="!editingPcrTestIds.includes(pcrTestSeries.id)">
              <span class="mr-4">Name: "{{ pcrTestSeries.name }}"</span>
              <span class="mr-4"
                >Testcodes: {{ pcrTestSeries.testCodes.join(", ") }}</span
              >
              <span class="date-range">
                Von {{ startHumanReadable }} bis {{ endHumanReadable }}
              </span>
            </div>
            <div v-if="editingPcrTestIds.includes(pcrTestSeries.id)">
              <v-text-field
                type="text"
                v-model="pcrTestSeries.name"
                label="Titel der PCR Test Serie"
                required
                :form="createFormName(pcrTestSeries)"
              />
            </div>
          </div>

          <div>
            <div class="flex-row">
              <div v-if="!editingPcrTestIds.includes(pcrTestSeries.id)">
                <v-icon
                  medium
                  class="mr-2"
                  @click.prevent="addToEditing(pcrTestSeries)"
                >
                  mdi-pencil
                </v-icon>
              </div>
              <div v-if="editingPcrTestIds.includes(pcrTestSeries.id)">
                <v-btn
                  type="sumbit"
                  :loading="loadingPcrTestId === pcrTestSeries.id"
                  :form="createFormName(pcrTestSeries)"
                >
                  <v-icon medium class="mr-2"> mdi-content-save </v-icon>
                </v-btn>
              </div>
              <v-icon
                medium
                @click.prevent="deletePcrTestSeriesInteral(pcrTestSeries.id)"
              >
                mdi-delete
              </v-icon>
            </div>
          </div>
        </div>

        <form
          v-for="pcrTestSeries in pcrTests"
          :key="'form-' + pcrTestSeries.id"
          :id="createFormName(pcrTestSeries)"
          v-on:submit.prevent="savePcrTestSeries(pcrTestSeries)"
        />
      </v-card>
    </div>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  getAllPcrPoolSeries,
  createPcrPoolSeries,
  deletePcrPoolSeries,
  updatePcrPoolSeries,
  PcrTestSeries,
} from "../../services/pcrTestSeries";

const getTodayIsoString = (): string => {
  return new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
    .toISOString()
    .substr(0, 10);
};

@Component({ name: "PcrTestsConfiguration" })
export default class PcrTestsConfiguration extends Vue {
  newPcrTestName: string = "";
  newPcrTestCodes: string = "";
  newStartDate: string = getTodayIsoString();
  newEndDate: string = getTodayIsoString();

  pcrTests: PcrTestSeries[] = [];
  editingPcrTestIds: string[] = [];
  loadingPcrTestId: string = "";

  async mounted() {
    const data = await getAllPcrPoolSeries();
    this.pcrTests = data;
  }

  addToEditing(pcrTestSeries: PcrTestSeries) {
    this.editingPcrTestIds.push(pcrTestSeries.id);
  }

  private get newPcrTestCodesArray(): string[] {
    return this.newPcrTestCodes.replaceAll(" ", "").split(",");
  }

  private get startHumanReadable(): string {
    return new Date(this.newStartDate).toLocaleDateString();
  }

  private get endHumanReadable(): string {
    return new Date(this.newEndDate).toLocaleDateString();
  }

  async createPcrTestSeriesInternal() {
    this.loadingPcrTestId = "0";
    await createPcrPoolSeries({
      name: this.newPcrTestName,
      start: new Date(this.newStartDate).toDateString(),
      end: new Date(this.newEndDate).toDateString(),
      testCodes: this.newPcrTestCodesArray,
    });
    this.newPcrTestName = "";
    this.newStartDate = getTodayIsoString();
    this.newEndDate = getTodayIsoString();
    (this.newPcrTestCodes = ""), (this.loadingPcrTestId = "");

    const poolData = await getAllPcrPoolSeries();
    this.pcrTests = poolData;
  }

  async savePcrTestSeries(pcrTestSeries: PcrTestSeries) {
    this.loadingPcrTestId = pcrTestSeries.id;
    const data = await updatePcrPoolSeries(pcrTestSeries);
    const index = this.editingPcrTestIds.indexOf(data.id);
    this.editingPcrTestIds.splice(index, 1);
    this.loadingPcrTestId = "";
  }

  async deletePcrTestSeriesInteral(id: string) {
    await deletePcrPoolSeries(id);
    const data = await getAllPcrPoolSeries();
    this.pcrTests = data;
  }

  createFormName(pcrTestSeries: PcrTestSeries) {
    return `form-pcr-test-${pcrTestSeries.id}`;
  }
}
</script>

<style scoped>
.flex-row {
  display: flex;
}
.v-row {
  padding: 0 14px;
}
.flex-grow {
  flex: 1 1 auto;
}
.flex-center {
  justify-content: center;
}
.pcr-test-card {
  flex: 0 1 800px;
}

.date-range {
  font-size: 0.7rem;
}
</style>
