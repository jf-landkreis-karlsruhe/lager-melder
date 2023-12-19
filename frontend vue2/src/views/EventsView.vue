<template>
  <div>
    <v-container class="event-root">
      <h1>🏕 Event: {{ eventName || "Anstehendes Event" }}</h1>
      <v-row justify="center">
        <Scanner
          manualCodeHint="8 Zeichen benötigt"
          :manualCodeInputRules="manualCodeInputRules"
          @submitCode="submitEvent($event)"
        />
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Vue, Component } from "vue-property-decorator";
import { getEventByCode, loginToEvent } from "../services/event";
import { isValidTestCode } from "../assets/config";
import Scanner from "../components/Scanner.vue";

@Component({ name: "EventsView", components: { Scanner } })
export default class EventsView extends Vue {
  eventCode: string = "";
  eventName: string = "";

  private get manualCodeInputRules() {
    return [
      (value: string) => !!value || "Required.",
      (value: string) => isValidTestCode(value) || "8 Zeichen benötigt",
    ];
  }

  protected async submitEvent(attendeeCode: string) {
    if (!isValidTestCode(attendeeCode)) {
      return;
    }
    const attendeeRes = await loginToEvent(this.eventCode, attendeeCode);
    if (attendeeRes) {
      this.$toast.success(
        `${this.eventName} von "${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName}".`
      );
    }
  }

  async mounted() {
    this.eventCode = this.$route.params.eventCode;
    const event = await getEventByCode(this.eventCode);
    this.eventName = event.name;
  }
}
</script>

<style scoped lang="scss">
* {
  box-sizing: border-box;
}

.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
