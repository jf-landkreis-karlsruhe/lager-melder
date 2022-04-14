<template>
  <v-row align="center" justify="center">
    <v-col sm="6" md="4" lg="4" offset-sm="1">
      <v-text-field
        type="date"
        :value="dateOnly"
        @input="dateChangedInteral"
        :label="label"
        :hint="hint"
        class="mx-4"
      />
    </v-col>
    <v-col sm="4" md="4" lg="4">
      <span class="mr-4">{{ timeOnly }} Uhr</span>
      <v-dialog v-model="showTime" width="450">
        <template v-slot:activator="{ on, attrs }">
          <v-icon v-bind="attrs" v-on="on" medium color="black">
            mdi-clock-time-ten-outline
          </v-icon>
        </template>

        <v-card class="mb-0">
          <v-card-title class="text-h5 grey lighten-2">
            Uhrzeit am {{ dateLocalized }}
          </v-card-title>

          <v-card-text class="d-flex justify-center align-center">
            <v-time-picker
              :value="timeOnly"
              @input="timeChangedInteral"
              format="24hr"
            ></v-time-picker>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="showTime = false">
              Übernehmen
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Vue, Prop, Component, Emit } from "vue-property-decorator";
import { dateLocalized } from "../helper/displayDate";
import {
  dateAndTimeAsIsoString,
  dateIsoString,
  timeIsoString,
} from "../helper/date";

@Component({ name: "DateAndTime" })
export default class DateAndTime extends Vue {
  private showTime: boolean = false;

  @Prop({ required: true })
  private readonly dateTime!: Date;

  @Prop({ required: false })
  private readonly label: string | undefined;

  @Prop({ required: false })
  private readonly hint: string | undefined;

  @Emit("changed")
  protected changed(newDate: Date) {
    return newDate;
  }

  get dateOnly(): string {
    return dateIsoString(this.dateTime);
  }

  get timeOnly(): string {
    return timeIsoString(this.dateTime);
  }

  private dateChangedInteral(newDate: string) {
    this.changed(new Date(dateAndTimeAsIsoString(newDate, this.timeOnly)));
  }

  private timeChangedInteral(newTime: string) {
    this.changed(new Date(dateAndTimeAsIsoString(this.dateOnly, newTime)));
  }

  private get dateLocalized() {
    return dateLocalized(this.dateTime);
  }
}
</script>

<style lang="scss"></style>
