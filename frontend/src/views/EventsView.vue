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

      <!-- Notifications. TODO: extract as general service -->
      <transition name="slide-fade" mode="out-in">
        <v-alert
          class="attandee-code-success"
          :key="attendeeAddedSentence"
          v-if="attendeeAddedSentence"
          type="success"
          dismissible
        >
          {{ attendeeAddedSentence }}
        </v-alert>
      </transition>

      <v-row justify="center">
        <div class="errors">
          <v-alert
            class="network-error"
            :value="!!networkError"
            type="error"
            transition="slide-y-transition"
            dismissible
          >
            {{ networkError }}
          </v-alert>
        </div>
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

  attendeeAddedSentence: string = "";
  networkError: string = "";

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
    const attendeeRes = await loginToEvent(this.eventCode, attendeeCode).catch(
      (reason) => {
        this.networkError = JSON.stringify(reason);
      }
    );
    console.log(attendeeRes);
    if (attendeeRes) {
      this.attendeeAddedSentence = `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich hinzugefügt.`;
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

.attandee-code-success,
.network-error {
  max-width: 100%;
  min-width: 300px;
}
.attandee-code-success {
  position: absolute;
  bottom: 0;
  right: 12px;
  transform: translateY(100%);
}
</style>
