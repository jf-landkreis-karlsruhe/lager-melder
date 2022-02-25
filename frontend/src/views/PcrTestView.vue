<template>
  <div>
    <v-container class="pcr-test-root">
      <!-- PCR TEST ID DOES NOT EXIST  -->
      <!-- image from: https://pixabay.com/de/illustrations/elefant-karikatur-charakter-zoo-2375697/ -->
      <Sorry
        v-if="!loading && !isValidPoolId(pcrPoolId)"
        title="Sorry, die angegebene PCR-Pool-Nummer exisitert nicht."
        image-url="https://cdn.pixabay.com/photo/2017/06/05/23/36/elephant-2375697_1280.png"
        description="Möglicherweise hat auch der Code nicht die richtige Länge."
        cta-url="/pcr-tests"
        cta-label="Zurück"
      />

      <!-- PCR TEST IS OUT OF RANGE  -->
      <!-- image from: https://www.maxpixel.net/Gray-Mammal-Elephant-Worried-Cartoon-Trunk-311860 -->
      <Sorry
        v-if="!loading && isValidPoolId(pcrPoolId) && !isInDateRange"
        title="PCR Test Gültigkeit abgelaufen."
        image-url="https://www.maxpixel.net/static/photo/1x/Gray-Mammal-Elephant-Worried-Cartoon-Trunk-311860.png"
        description="Das Datum des Tests liegt außerhalb der Gültigkeit.<br />Leider kannst du keine Änderungen mehr vornehmen."
        cta-url="/pcr-tests"
        cta-label="Zurück"
      />

      <!-- PCR TEST ID DOES EXIST  -->
      <v-row v-if="!loading && isValidPoolId(pcrPoolId) && isInDateRange">
        <h1 class="header mb-8">Teilnehmer zum PCR Pool hinzuzufügen.</h1>
        <v-col justify="center" align="center">
          <div class="mb-8">
            <img
              src="../assets/Zeltlager-Ausweis-Beispiel.png"
              alt="Beispiel eines Teilnehmer-Ausweises"
              title="Beispiel eines Teilnehmer-Ausweises"
              width="40%"
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
                    <v-btn
                      icon
                      v-if="isInDateRange"
                      @click="removeAttendeeFromPcrPool(attendee.attendeeCode)"
                    >
                      <v-icon
                        color="grey lighten-1"
                        @mouseover="trashIndex = attendee.attendeeCode"
                        v-show="trashIndex !== attendee.attendeeCode"
                      >
                        mdi-delete
                      </v-icon>
                      <v-icon
                        color="grey lighten-1"
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
        </v-col>
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
import Sorry from "../components/Sorry.vue";

@Component({ name: "PcrTestView", components: { Scanner, Sorry } })
export default class PcrTestView extends Vue {
  private pcrPoolId: string = "";
  private pcrTest: PcrTest | null = null;
  private trashIndex = "";

  private loading: boolean = false;
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

  private isAttendeeCodeInList(attendeeCode: string): boolean {
    return (
      this.pcrTest?.testedAttendees
        .map((att) => att.attendeeCode)
        .includes(attendeeCode) ?? false
    );
  }

  protected async addAttendeeToPcrPool(attendeeCode: string): Promise<void> {
    if (this.isAttendeeCodeInList(attendeeCode)) {
      return;
    }

    const attendeeRes = await addAttendeeToPcrPool(
      this.pcrPoolId,
      attendeeCode
    );

    if (attendeeRes) {
      this.$toast.success(
        `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich hinzugefügt.`
      );
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
    const res = await removeAttendeeFromPool(this.pcrPoolId, attendeeCode);

    if (res?.ok) {
      const deletedAttendees = this.attendees.splice(
        this.attendees.findIndex((v) => v.attendeeCode === attendeeCode),
        1
      );
      this.$toast.info(
        `${deletedAttendees[0].attendeeFirstName} ${deletedAttendees[0].attendeeLastName} wurde entfernt.`
      );

      await this.refetchData();
    }
  }

  public async created(): Promise<void> {
    this.pcrPoolId = this.$route.params.poolId;
    this.pcrTest = await this.refetchData();
  }

  private async refetchData(): Promise<PcrTest | null> {
    this.loading = true;
    const pcrTestData = await getPcrPool(this.pcrPoolId);
    this.loading = false;
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

  .header {
    text-align: left;
  }

  .scanner {
    margin-bottom: 2rem;
  }

  .attendee-list {
    min-width: 400px;
  }
}
</style>
