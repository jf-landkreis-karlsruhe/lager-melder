<template>
  <v-container>
    <div>
      <h2>{{ departmentDistribution.name }}</h2>
      <div class="d-flex wrap">
        <div class="d-flex column">
          <div class="number-container checked-in">
            <h3>{{ departmentDistribution.checkedInYouth }} ⛺</h3>
            <h5>anwesende Jugendliche</h5>
          </div>
          <div class="number-container checked-in">
            <h3>{{ departmentDistribution.checkedInYouthLeader }} ⛺</h3>
            <h5>anwesende Jugendleiter</h5>
          </div>
        </div>

        <div class="d-flex">
          <div class="number-container checked-out">
            <h3>{{ departmentDistribution.checkedOutYouth }} 🏠</h3>
            <h5>abwesende Jugendliche</h5>
          </div>

          <div class="number-container checked-out">
            <h3>{{ departmentDistribution.checkedOutYouthLeader }} 🏠</h3>
            <h5>abwesende Jugendliche</h5>
          </div>
        </div>
      </div>
    </div>
  </v-container>
</template>

<script lang="ts">
import { Distribution } from "@/services/event";
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

@Component({
  components: {},
})
export default class CheckedInSummary extends Vue {
  @Prop() departmentDistribution!: Distribution;
}
</script>
<style scoped>
.number-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
}

.checked-in {
  background-color: #00ff0030;
}
.checked-out {
  background-color: #ff000030;
}
</style>
