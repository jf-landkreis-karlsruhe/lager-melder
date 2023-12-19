<template>
  <v-container>
    <h1>Anwesende</h1>
    <div v-if="eventSummary !== null">
      <CheckedInSummary :departmentDistribution="eventSummary.total" />
      <div
        v-for="departmentSummary in eventSummary.departments"
        :key="departmentSummary.name"
      >
        <CheckedInSummary :departmentDistribution="departmentSummary" />
      </div>
    </div>
  </v-container>
</template>

<script lang="ts">
import CheckedInSummary from "@/components/CheckedInSummary.vue";
import { GlobalEventSummary, globalEventSummary } from "@/services/event";
import Vue from "vue";
import { Component } from "vue-property-decorator";

@Component({
  components: { CheckedInSummary },
})
export default class EventSummaryView extends Vue {
  eventSummary: GlobalEventSummary | null = null;

  mounted() {
    globalEventSummary().then((summary) => {
      this.eventSummary = summary;
    });
  }
}
</script>
