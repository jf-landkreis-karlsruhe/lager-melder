<template>
  <div class="mt-16 mb-5">
    <h1>Verteilung der Auswahl für Tshirts und Essen</h1>
    <div v-if="tShirtDistribution.length > 0">
      <BarChart chartLabel="TShirt" :rawData="tShirtDistribution" />
    </div>
    <div v-if="foodDistribution.length > 0">
      <BarChart chartLabel="Essen" :rawData="foodDistribution" />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";
import BarChart from "./BarChart.vue";

import {
  // eslint-disable-next-line no-unused-vars
  Attendee,
  // eslint-disable-next-line no-unused-vars
  Food,
  // eslint-disable-next-line no-unused-vars
  TShirtSize,
} from "../services/attendee";

import { foodText, tShirtSizeText } from "../helper/displayText";
import { FoodSortOrder, TShirtSizeSortOrder } from "@/helper/filterHelper";

type FoodDestribution = {
  name: Food;
  count: number;
};
type TShirtDestribution = {
  name: TShirtSize;
  count: number;
};

@Component({ components: { BarChart } })
export default class Header extends Vue {
  @Prop() attendees!: Attendee[];

  get tShirtDistribution() {
    return this.attendees
      .map((attendee) => attendee.tShirtSize)
      .reduce((acc: TShirtDestribution[], value: TShirtSize) => {
        const tShirtInDistrubution = acc.find(
          (tShirt) => tShirt.name === value
        );
        if (tShirtInDistrubution) {
          tShirtInDistrubution.count += 1;
          return acc;
        }
        return [...acc, { name: value, count: 1 }];
      }, [] as TShirtDestribution[])
      .sort(
        (distributionA, distributionB) =>
          TShirtSizeSortOrder.indexOf(distributionA.name) -
          TShirtSizeSortOrder.indexOf(distributionB.name)
      )
      .map((distribution) => ({
        name: tShirtSizeText(distribution.name),
        count: distribution.count,
      }));
  }

  get foodDistribution() {
    return this.attendees
      .map((attendee) => attendee.food)
      .reduce((acc: FoodDestribution[], value: Food) => {
        const foodInDistrubution = acc.find((food) => food.name === value);
        if (foodInDistrubution) {
          foodInDistrubution.count += 1;
          return acc;
        }
        return [...acc, { name: value, count: 1 }];
      }, [] as FoodDestribution[])
      .sort(
        (distributionA, distributionB) =>
          FoodSortOrder.indexOf(distributionA.name) -
          FoodSortOrder.indexOf(distributionB.name)
      )
      .map((distribution) => ({
        name: foodText(distribution.name),
        count: distribution.count,
      }));
  }
}
</script>
