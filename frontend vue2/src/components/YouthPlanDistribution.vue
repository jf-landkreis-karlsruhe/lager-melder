<template>
  <v-dialog v-model="dialogOpen" persistent max-width="500">
    <template v-slot:activator="{ on, attrs }" v-if="hasAdministrationRole()">
      <v-btn
        rounded
        color="primary"
        dark
        v-bind="attrs"
        v-on="on"
        @click="openModal"
        class="mb-2"
      >
        Verteilung Pädagogischer Betreuer anzeigen
      </v-btn>
    </template>
    <v-card>
      <v-card-title class="headline">
        Verteilung Pädagogischer Betreuer
      </v-card-title>
      <v-card-text v-if="!loading">
        <dl>
          <dt>Teilnehmer</dt>
          <dd>{{ youthPlanDistribution.youthCount }}</dd>
          <dt>Betreuer</dt>
          <dd>{{ youthPlanDistribution.leaderCount }}</dd>
        </dl>
      </v-card-text>
      <v-card-text v-if="loading">
        <v-progress-circular indeterminate :size="24" />
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn rounded text @click="closeModal"> Schließen </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  getYouthPlanDistribution,
  YouthPlanDistribution,
} from "@/services/youthPlanAttendees";

import { hasAdministrationRole } from "../services/authentication";

@Component({})
export default class YouthDistributionPlan extends Vue {
  error = false;
  loading: boolean = false;
  dialogOpen: boolean = false;
  youthPlanDistribution: YouthPlanDistribution = {
    youthCount: 0,
    leaderCount: 0,
  };

  hasAdministrationRole = hasAdministrationRole;

  closeModal() {
    this.dialogOpen = false;
  }

  openModal() {
    this.loading = true;
    getYouthPlanDistribution()
      .then((youthPlanDistribution) => {
        this.youthPlanDistribution = youthPlanDistribution;
        this.loading = false;
      })
      .catch(() => (this.error = true));
  }
}
</script>

<style scoped></style>
