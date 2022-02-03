<template>
  <div>
    <v-container class="pcr-test-root">
      <h1>Scanne einen Code um einen Teilnehmer zum PCR Pool hinzuzufügen.</h1>
      <v-row justify="center">
        <Scanner
          class="scanner"
          manualCodeHint="Mindestens Y Zeichen"
          @submitCode="addAttendeeToPcrPool"
        />
      </v-row>
      <v-row justify="center">
        <transition name="slide-fade" mode="out-in">
          <v-alert
            class="attandee-code-success"
            :key="successMessage"
            v-if="!!successMessage"
            v-model="successMessage"
            type="success"
            dismissible
          >
            {{ successMessage }}
          </v-alert>
        </transition>
      </v-row>

      <div ref="attendeeListRef">
        <v-row justify="center" v-if="attendees.length > 0">
          <v-list subheader two-line class="attendee-list">
            <v-subheader inset>Teilnehmer</v-subheader>

            <v-list-item
              v-for="attendee in attendees"
              :key="attendee.attendeeFirstName"
            >
              <v-list-item-avatar>
                <v-icon class="grey lighten-1" dark> mdi-account </v-icon>
              </v-list-item-avatar>

              <v-list-item-content>
                <v-list-item-title
                  v-text="
                    `${attendee.attendeeFirstName} ${attendee.attendeeLastName}`
                  "
                ></v-list-item-title>

                <v-list-item-subtitle
                  v-text="attendee.time"
                ></v-list-item-subtitle>
              </v-list-item-content>

              <v-list-item-action>
                <v-btn icon>
                  <v-icon
                    color="grey lighten-1"
                    @click="removeAttendeeFromPcrPool"
                    @mouseover="trashIndex = attendee.id"
                    v-show="trashIndex !== attendee.id"
                  >
                    mdi-delete
                  </v-icon>
                  <v-icon
                    color="grey lighten-1"
                    @click="removeAttendeeFromPcrPool"
                    @mouseleave="trashIndex = -1"
                    v-show="trashIndex === attendee.id"
                  >
                    mdi-delete-empty
                  </v-icon>
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-row>
      </div>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Vue, Component } from "vue-property-decorator";
import Scanner from "../components/Scanner.vue";
import {
  addAttendeeToPcrPool,
  removeAttendeeFromPool,
  PcrAttendee,
} from "../services/pcrTest";

@Component({ name: "PcrTestView", components: { Scanner } })
export default class PcrTestView extends Vue {
  private attendees: PcrAttendee[] = [];
  private successMessage: string = "";
  private pcrPoolId: string = "";
  private networkError: string = "";
  private trashIndex = -1;
  $refs!: {
    attendeeListRef: HTMLDivElement;
  };

  protected async addAttendeeToPcrPool(attendeeCode: string): Promise<void> {
    /// TODO: verify validation of scanned code
    // if (!isValidTestCode(attendeeCode)) {
    //   return;
    // }
    const attendeeRes = await addAttendeeToPcrPool(
      this.pcrPoolId,
      attendeeCode
    ).catch((reason) => {
      this.networkError = JSON.stringify(reason);
    });
    console.log(attendeeRes);
    if (attendeeRes) {
      this.successMessage = `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich hinzugefügt.`;
      this.attendees.push(attendeeRes);
      // scroll to new element
      this.$vuetify.goTo(this.$refs.attendeeListRef, {
        easing: "easeInOutCubic",
      });
    }
  }

  protected async removeAttendeeFromPcrPool(
    attendeeCode: string
  ): Promise<void> {
    removeAttendeeFromPool(this.pcrPoolId, attendeeCode)
      .then(() => {
        const deletedAttendees = this.attendees.splice(
          this.attendees.findIndex((v) => v.attendeeCode === attendeeCode),
          1
        );
        this.successMessage = `${deletedAttendees[0].attendeeFirstName} ${deletedAttendees[0].attendeeLastName} wurde erfolgreich vom Pool '${this.pcrPoolId}' entfernt.`;
      })
      .catch((reason) => {
        this.networkError = JSON.stringify(reason);
      });
  }

  async mounted() {
    this.pcrPoolId = this.$route.params.poolId;
  }
}
</script>

<style scoped lang="scss">
.pcr-test-root {
  position: relative;

  .scanner {
    margin-bottom: 2rem;
  }

  .attandee-code-success {
    position: absolute;
    right: 1rem;
    top: 4.75rem;
  }

  .attendee-list {
    min-width: 400px;
  }
}
</style>
