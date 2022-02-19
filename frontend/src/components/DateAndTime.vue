<template>
  <v-row align="center" justify="center">
    <v-text-field
      type="date"
      :value="date"
      @input="dateChanged"
      :label="label"
      :hint="hint"
      class="mx-4"
    />
    <span class="mr-4">{{ time }} Uhr</span>
    <v-dialog v-model="showTime" width="450">
      <template v-slot:activator="{ on, attrs }">
        <v-btn v-bind="attrs" v-on="on" small> Uhrzeit setzen </v-btn>
      </template>

      <v-card>
        <v-card-title class="text-h5 grey lighten-2">
          Uhrzeit am {{ formattedDate }}
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
  </v-row>
</template>

<script lang="ts">
import { Vue, Prop, Component, Emit } from "vue-property-decorator";

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

  private get formattedDate() {
    return new Date(this.date).toLocaleString("de-DE", {
      weekday: "long",
      day: "numeric",
      month: "numeric",
      year: "numeric",
    });
  }
}
</script>

<style lang="scss">
</style>