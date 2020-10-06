<template>
  <div>
    <div v-for="food in foodDistribution" :key="food.food">
      {{ food.name }}: {{ food.count }}
    </div>
    <div v-for="tShirt in tShirtDistribution" :key="tShirt.tShirt">
      {{ tShirt.name }}: {{ tShirt.count }}
    </div>
    <div v-if="tShirtDistribution.length > 0">
      <BarChart chartLabel="TShirt" :rawData="tShirtDistribution" />
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
  TShirtSize
} from "../services/attendee";

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
      .map(attendee => attendee.tShirtSize)
      .reduce((acc: TShirtDestribution[], value: TShirtSize) => {
        const tShirtInDistrubution = acc.find(tShirt => tShirt.name === value);
        if (tShirtInDistrubution) {
          tShirtInDistrubution.count += 1;
          return acc;
        }
        return [...acc, { name: value, count: 1 }];
      }, [] as TShirtDestribution[]);
  }

  get foodDistribution() {
    return this.attendees
      .map(attendee => attendee.food)
      .reduce((acc: FoodDestribution[], value: Food) => {
        const foodInDistrubution = acc.find(food => food.name === value);
        if (foodInDistrubution) {
          foodInDistrubution.count += 1;
          return acc;
        }
        return [...acc, { name: value, count: 1 }];
      }, [] as FoodDestribution[]);
  }
}
</script>
