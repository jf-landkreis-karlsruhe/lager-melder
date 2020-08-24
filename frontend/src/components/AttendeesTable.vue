<template>
  <section>
    <v-card class="card">
      <v-card-title>
        <v-row justify="space-between">
          <h1 class="headline">{{headlineText}}</h1>
          <div class="additional-information">Anzahl {{headlineText}}: {{attendees.length}}</div>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="attendees"
          :disable-pagination="true"
          :disable-items-per-page="true"
          :hide-default-footer="true"
        >
          <template v-slot:item.tshirtSize="{ item }">{{tshirtSizeText(item.tshirtSize)}}</template>
          <template v-slot:item.food="{ item }">{{foodText(item.food)}}</template>
          <template v-slot:item.actions="{ item }">
            <v-row>
              <v-icon small class="mr-2" @click="editItem(item)">mdi-pencil</v-icon>
              <v-icon small @click="deleteItem(item)">mdi-delete</v-icon>
            </v-row>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </section>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

import {
  // getAttendeesForMyDepartment,
  // eslint-disable-next-line no-unused-vars
  Attendee,
  Food,
  TShirtSize
} from "../services/attendee";

@Component({})
export default class AttendeesTable extends Vue {
  @Prop() attendees!: Attendee[];
  @Prop() headlineText!: string;

  headers = [
    { text: "Vorname", value: "firstName" },
    { text: "Nachname", value: "lastName" },
    { text: "Essen", value: "food" },
    { text: "TShirt Größe", value: "tshirtSize" },
    { text: "Geburtsdatum", value: "birthday" },
    { text: "Anmerkung", value: "additionalInformation" },
    { text: "", value: "actions", sortable: false }
  ];

  tshirtSizeText = (tshirtSize: TShirtSize) => {
    switch (tshirtSize) {
      case TShirtSize.ONE_HUNDRED_TWENTY_EIGHT:
        return "128";
      case TShirtSize.ONE_HUNDRED_FORTY:
        return "140";
      case TShirtSize.ONE_HUNDRED_FIFTY_TWO:
        return "152";
      case TShirtSize.ONE_HUNDRED_FIFTY_EIGHT:
        return "158";
      case TShirtSize.ONE_HUNDRED_SIXTY_FOUR:
        return "164";
      case TShirtSize.S:
        return "S";
      case TShirtSize.M:
        return "M";
      case TShirtSize.L:
        return "L";
      case TShirtSize.XL:
        return "XL";
      case TShirtSize.XXL:
        return "XXL";
      case TShirtSize.XXXL:
        return "XXXL";
      case TShirtSize.XXXXL:
        return "XXXXL";
      case TShirtSize.XXXXXL:
        return "XXXXXL";
      default:
        return tshirtSize;
    }
  };

  foodText = (food: Food) => {
    switch (food) {
      case Food.MEAT:
        return "Fleisch";
      case Food.NONE:
        return "Nichts";
      case Food.ALLERGY:
        return "Allergie";
      case Food.VEGETARIAN:
        return "Vegetarisch";
      case Food.VEGAN:
        return "Vegan";
      case Food.MUSLIM:
        return "Muslimisch";
      default:
        return food;
    }
  };
}
</script>

<style scoped>
.card {
  padding: 14px;
  margin-bottom: 30px;
}
.additional-information {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.6);
}
.headline {
  margin: 14px 0;
}
</style>
