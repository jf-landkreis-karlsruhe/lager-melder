<template>
  <v-row align="center" justify="center">
    <v-col sm="6">
      <v-text-field
        type="date"
        :value="date"
        @input="dateChanged"
        :label="label"
        :hint="hint"
        class="mx-4"
      />
    </v-col>
    <v-col sm="6">
      <span class="mr-4">{{ time }} Uhr</span>
      <v-dialog v-model="showTime" width="450">
        <template v-slot:activator="{ on, attrs }">
          <v-icon v-bind="attrs" v-on="on" medium color="black">
            mdi-clock-time-ten-outline
          </v-icon>
        </template>

        <v-card>
          <v-card-title class="text-h5 grey lighten-2">
            Uhrzeit am {{ dateLocalized }}
          </v-card-title>

          <v-card-text class="d-flex justify-center align-center">
            <v-time-picker
              :value="time"
              @input="timeChanged"
              format="24hr"
            ></v-time-picker>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="showTime = false">
              Schließen
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

@Component({ name: "DateAndTime" })
export default class DateAndTime extends Vue {
  private showTime: boolean = false;

  @Prop({ required: true })
  private readonly date!: string;

  @Prop({ required: true })
  private readonly time!: string;

  @Prop({ required: false })
  private readonly label: string | undefined;

  @Prop({ required: false })
  private readonly hint: string | undefined;

  @Emit("dateChanged")
  protected dateChanged(newDate: string) {
    return newDate;
  }

  @Emit("timeChanged")
  protected timeChanged(newTime: string) {
    return newTime;
  }

  private get dateLocalized() {
    return dateLocalized(this.date);
  }
}
</script>

<style lang="scss"></style>
