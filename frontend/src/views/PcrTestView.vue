<template>
  <div>
    <v-container class="pcr-test-root">
      <!-- PCR TEST ID DOES NOT EXIST  -->
      <v-row v-if="!isValidPoolId(pcrPoolId)" justify="center">
        <v-col justify="center" class="sorry">
          <v-row justify="center">
            <h1 class="sorry-title">
              Sorry, die angegebene PCR-Pool-Nummer exisitert nicht.
            </h1>
            <img
              src="https://images.unsplash.com/photo-1504667290505-eee11f23905a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2340&q=80"
              width="65%"
              height="auto"
            />
          </v-row>
          <v-row justify="center">
            <div class="back-button">
              <router-link to="/pcr-tests" tag="a">Zurück</router-link>
            </div>
          </v-row>
        </v-col>
      </v-row>

      <!-- PCR TEST IS OUT OF RANGE  -->
      <v-row justify="center" v-if="isValidPoolId(pcrPoolId) && !isInDateRange">
        <v-col class="my-8" align="center">
          <h1>PCR Test Gültigkeit abgelaufen.</h1>
          <img
            src="https://images.unsplash.com/photo-1502907997294-84206b78f31b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2338&q=80"
            width="45%"
            height="auto"
            class="mb-4"
          />
          <p class="desc">
            Das Datum des Tests liegt außerhalb der Gültigkeit. <br />
            Leider kannst du keine Änderungen mehr vornehmen.
          </p>
        </v-col>
      </v-row>

      <!-- PCR TEST ID DOES EXIST  -->
      <v-row v-if="isValidPoolId(pcrPoolId) && isInDateRange">
        <v-row justify="center">
          <div class="my-8 d-flex flex-column align-center">
            <h1>
              Scanne einen Code um einen Teilnehmer zum PCR Pool hinzuzufügen.
            </h1>
            <img
              src="https://i.pinimg.com/originals/f3/c1/cd/f3c1cdcc492cfc5d31be66093adcd33f.jpg"
              width="45%"
              height="auto"
              class="mb-4"
            />
            <p>Scanne hier den Code jedes Teilnehmers ab.</p>
          </div>

          <Scanner
            class="scanner"
            manualCodeHint="Mindestens Y Zeichen"
            @submitCode="addAttendeeToPcrPool"
          />
        </v-row>

        <!-- SUCCESS MESSAGE -->
        <transition name="slide-fade" mode="out-in">
          <v-alert
            transition="slide-y-reverse-transition"
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

        <v-row ref="attendeeListRef">
          <v-row justify="center" v-if="attendees.length > 0">
            <v-list subheader two-line class="attendee-list">
              <v-subheader inset>Teilnehmer</v-subheader>

              <v-list-item
                v-for="attendee in attendees"
                :key="attendee.attendeeCode"
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
                    v-text="attendee.departmentName"
                  ></v-list-item-subtitle>
                </v-list-item-content>

                <v-list-item-action>
                  <v-btn icon v-if="isInDateRange">
                    <v-icon
                      color="grey lighten-1"
                      @click="removeAttendeeFromPcrPool"
                      @mouseover="trashIndex = attendee.attendeeCode"
                      v-show="trashIndex !== attendee.attendeeCode"
                    >
                      mdi-delete
                    </v-icon>
                    <v-icon
                      color="grey lighten-1"
                      @click="removeAttendeeFromPcrPool"
                      @mouseleave="trashIndex = ''"
                      v-show="trashIndex === attendee.attendeeCode"
                    >
                      mdi-delete-empty
                    </v-icon>
                  </v-btn>
                </v-list-item-action>
              </v-list-item>
            </v-list>
          </v-row>
        </v-row>
      </v-row>

      <!-- NETWORK ERROR -->
      <v-row justify="center">
        <transition name="slide-fade" mode="out-in">
          <v-alert
            transition="slide-y-reverse-transition"
            class="attandee-code-error"
            :key="networkError"
            v-if="showNetworkError"
            v-model="showNetworkError"
            type="error"
            dismissible
          >
            {{ networkError }}
          </v-alert>
        </transition>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { isValidTestCode } from "@/assets/config";
import { Vue, Component } from "vue-property-decorator";
import Scanner from "../components/Scanner.vue";
import {
  getPcrPool,
  addAttendeeToPcrPool,
  removeAttendeeFromPool,
  PcrAttendee,
  PcrTest,
} from "../services/pcrTest";

@Component({ name: "PcrTestView", components: { Scanner } })
export default class PcrTestView extends Vue {
  private pcrPoolId: string = "";
  private pcrTest: PcrTest | null = null;
  private successMessage: string = "";
  private networkError: string = "";
  private trashIndex = "";
  $refs!: {
    attendeeListRef: HTMLDivElement;
  };

  protected get attendees(): PcrAttendee[] {
    return this.pcrTest?.testedAttendees ?? [];
  }

  protected get isInDateRange(): boolean {
    if (!this.pcrTest) return false;

    const currentDate = new Date();
    const { start, end } = this.pcrTest;
    if (currentDate > start && currentDate < end) {
      return true;
    }
    return false;
  }

  protected get showNetworkError(): boolean {
    return this.networkError.length > 0;
  }

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

    if (attendeeRes) {
      this.successMessage = `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich hinzugefügt.`;
      this.attendees.push(attendeeRes);
      // scroll to new element
      this.$vuetify.goTo(this.$refs.attendeeListRef, {
        easing: "easeInOutCubic",
      });
      await this.refetchData();
    }
  }

  protected async removeAttendeeFromPcrPool(
    attendeeCode: string
  ): Promise<void> {
    await removeAttendeeFromPool(this.pcrPoolId, attendeeCode).catch(
      (reason) => {
        this.networkError = JSON.stringify(reason);
      }
    );

    const deletedAttendees = this.attendees.splice(
      this.attendees.findIndex((v) => v.attendeeCode === attendeeCode),
      1
    );
    this.successMessage = `${deletedAttendees[0].attendeeFirstName} ${deletedAttendees[0].attendeeLastName} wurde erfolgreich vom Pool '${this.pcrPoolId}' entfernt.`;

    await this.refetchData();
  }

  public async created(): Promise<void> {
    this.pcrPoolId = this.$route.params.poolId;
    this.pcrTest = await this.refetchData();
  }

  private async refetchData(): Promise<PcrTest | null> {
    const pcrTestData = await getPcrPool(this.pcrPoolId).catch(
      (e: Response) => {
        const urlObj = new URL(e.url);
        this.networkError = `URL: ${urlObj.pathname}, Status: ${e.status}, Reason: ${e.type}`;
      }
    );
    if (!pcrTestData) return null;

    return pcrTestData;
  }

  protected isValidPoolId(poolId: string): boolean {
    return isValidTestCode(poolId);
  }
}
</script>

<style scoped lang="scss">
.pcr-test-root {
  position: relative;
  margin-bottom: 5rem;

  .sorry {
    .sorry-title {
      font-size: 2rem;
    }
    .back-button {
      margin-top: 1rem;
      padding: 0.5rem;
      color: #1976d2;
    }
  }

  .desc {
    text-align: center;
  }

  .scanner {
    margin-bottom: 2rem;
  }

  .attandee-code-success,
  .attandee-code-error {
    position: fixed;
    bottom: 1.5rem;
    right: 1.5rem;
    max-width: 100%;
    z-index: 99;
  }

  .attendee-list {
    min-width: 400px;
  }
}
</style>
