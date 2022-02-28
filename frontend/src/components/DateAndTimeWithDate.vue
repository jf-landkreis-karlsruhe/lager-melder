<template>
  <DateAndTime
    :showTime="showTime"
    :label="label"
    :hint="hint"
    :date="dateOnly"
    @dateChanged="dateChangedInteral"
    :time="timeOnly"
    @timeChanged="timeChangedInteral"
  />
</template>

<script lang="ts">
import { Vue, Prop, Component, Emit } from "vue-property-decorator";
import { dateAndTimeAsIsoString, dateIsoString, timeIsoString } from "../helper/date";
import DateAndTime from './DateAndTime.vue'


@Component({ name: "DateAndTimeWithDate", components: { DateAndTime }})
export default class DateAndTimeWithDate extends Vue {
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
    this.changed(new Date(dateAndTimeAsIsoString(newDate, this.timeOnly)))
  }

  private timeChangedInteral(newTime: string) {
    this.changed(new Date(dateAndTimeAsIsoString(this.dateOnly, newTime)))
  }

}
</script>

<style lang="scss">
</style>
