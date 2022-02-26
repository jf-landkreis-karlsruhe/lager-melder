<template>
  <DateAndTime
    :date="dateOnly"
    @dateChanged="dateChangedInteral"
    label="Enddatum"
    :time="timeOnly"
    @timeChanged="pcrTestSeries.end = $event"
  />
</template>

<script lang="ts">
import { Vue, Prop, Component, Emit } from "vue-property-decorator";
import { dateLocalized } from "../helper/displayDate";
import { dateAndTimeAsIsoString, dateIsoString, timeIsoString } from "../helper/date";


@Component({ name: "DateAndTime" })
export default class DateAndTime extends Vue {
  private showTime: boolean = false;

  @Prop({ required: true })
  private readonly date!: Date;

  @Prop({ required: false })
  private readonly label: string | undefined;

  @Prop({ required: false })
  private readonly hint: string | undefined;

  @Emit("changed")
  protected dateChanged(newDate: Date) {
    return newDate;
  }

  get dateOnly(): string {
    return dateIsoString(this.date);
  }

  get timeOnly(): string {
    return timeIsoString(this.date);
  }

  private dateChangedInteral(newDate: string) {
    dateAndTimeAsIsoString(newDate, this.timeOnly)
    this.dateChanged(new Date(newDate))
  }

  private get dateLocalized() {
    return dateLocalized(this.date);
  }
}
</script>

<style lang="scss">
</style>
