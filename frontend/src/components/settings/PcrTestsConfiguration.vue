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
      <v-textarea
        v-model="newPcrTestCodes"
        label="PCR Test Serie Testcodes als Kommaseparierte Liste"
        required
      />
      <v-row justify="center" align="center" class="d-flex flex-wrap mt-2">
        <v-col>
          <DateAndTime
            :dateTime="newStart"
            @changed="newStart = $event"
            label="Startdatum"
          />
        </v-col>
        <v-col>
          <DateAndTime
            :dateTime="newEnd"
            @changed="newEnd = $event"
            label="Enddatum"
          />
        </v-col>
      </v-row>

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
          class="flex-row bordered"
          v-for="pcrTestSeries in pcrTests"
          :key="pcrTestSeries.id"
        >
          <div class="flex-row flex-grow mb-4">
            <div
              v-if="!editingPcrTestIds.includes(pcrTestSeries.id)"
              class="edit-tests"
            >
              <div class="mr-4 edit-name">
                Name: <b>"{{ pcrTestSeries.name }}"</b>
              </div>
              <div class="edit-test-tags">
                Testcodes:
                <span
                  class="edit-test-tag"
                  v-for="code in pcrTestSeries.testCodes"
                  :key="code"
                >
                  {{ code }}
                </span>
              </div>
              <div class="edit-date-range">
                Von {{ dateLocalized(pcrTestSeries.start) }} Uhr → bis
                {{ dateLocalized(pcrTestSeries.end) }} Uhr
              </div>
            </div>
            <div v-if="isOpenForEditing(pcrTestSeries.id)">
              <v-text-field
                type="text"
                v-model="pcrTestSeries.name"
                label="Titel der PCR Test Serie"
                required
                :form="createFormName(pcrTestSeries)"
              />
              <v-textarea
                v-model="pcrTestSeries.testCodes"
                label="PCR Test Serie Testcodes als Kommaseparierte Liste"
                required
              />
              <v-row
                justify="center"
                align="center"
                class="d-flex flex-wrap mt-2"
              >
                <v-col>
                  <DateAndTime
                    :dateTime="pcrTestSeries.start"
                    @changed="pcrTestSeries.start = $event"
                    label="Startdatum"
                  />
                </v-col>
                <v-col>
                  <DateAndTime
                    :dateTime="pcrTestSeries.end"
                    @changed="pcrTestSeries.end = $event"
                    label="Enddatum"
                  />
                </v-col>
              </v-row>
            </div>
          </div>

          <div>
            <div class="flex-row">
              <div v-if="!isOpenForEditing(pcrTestSeries.id)">
                <v-icon
                  medium
                  class="mr-2"
                  @click.prevent="addToEditing(pcrTestSeries.id)"
                >
                  mdi-pencil
                </v-icon>
              </div>
              <div v-if="isOpenForEditing(pcrTestSeries.id)">
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
import DateAndTime from "../DateAndTime.vue";
import { dateTimeLocalized } from "../../helper/displayDate";

@Component({ name: "PcrTestsConfiguration", components: { DateAndTime } })
export default class PcrTestsConfiguration extends Vue {
  private newPcrTestName: string = "";
  private newPcrTestCodes: string = "";
  private newStart: Date = new Date();
  private newEnd: Date = new Date();

  private pcrTests: PcrTestSeries[] = [];
  private editingPcrTestIds: string[] = [];
  private loadingPcrTestId: string = "";

  async mounted() {
    const data = await getAllPcrPoolSeries();
    this.pcrTests = data;
  }

  private get newPcrTestCodesArray(): string[] {
    return this.newPcrTestCodes.replaceAll(" ", "").split(/[\n,]+/);
  }

  addToEditing(pcrTestSeriesId: string) {
    this.editingPcrTestIds.push(pcrTestSeriesId);
  }

  private isOpenForEditing(pcrTestSeriesId: string): boolean {
    return this.editingPcrTestIds.includes(pcrTestSeriesId);
  }

  async createPcrTestSeriesInternal() {
    this.loadingPcrTestId = "0";
    await createPcrPoolSeries({
      name: this.newPcrTestName,
      start: this.newStart,
      end: this.newEnd,
      testCodes: this.newPcrTestCodesArray,
    }).catch(() => {
      this.setToDefaultFormValues();
    });
    this.setToDefaultFormValues();

    const poolData = await getAllPcrPoolSeries();
    this.pcrTests = poolData;
  }

  protected setToDefaultFormValues() {
    this.newPcrTestName = "";
    this.newStart = new Date();
    this.newEnd = new Date();
    this.newPcrTestCodes = "";
    this.loadingPcrTestId = "";
  }

  async savePcrTestSeries(pcrTestSeries: PcrTestSeries) {
    this.loadingPcrTestId = pcrTestSeries.id;
    // pcrTestSeries.testCodes are converted into string in textarea element
    if (typeof pcrTestSeries.testCodes === "string") {
      pcrTestSeries.testCodes = (pcrTestSeries.testCodes as unknown as string)
        .replaceAll(" ", "")
        .split(/[\n,]+/);
    }

    const data = await updatePcrPoolSeries(pcrTestSeries).catch(() => {
      this.loadingPcrTestId = "";
    });
    if (!data) return;
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

  private dateLocalized(date: Date) {
    return dateTimeLocalized(date);
  }
}
</script>

<style scoped lang="scss">
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

.edit-tests {
  .edit-test-tags {
    font-size: 0.9rem;
  }
  .edit-test-tag {
    background-color: #ddd;
    padding: 0px 16px;
    margin: 0 4px;
    border-radius: 16px;
  }
  .edit-date-range {
    font-size: 0.75rem;
  }
}
.bordered {
  padding-top: 12px;
  border-bottom: 1px solid lightgray;
}
</style>
