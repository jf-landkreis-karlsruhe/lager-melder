<template>
  <div>
    <v-container class="pcr-test-root">
      <!-- PCR TEST ID DOES NOT EXIST  -->
      <!-- image from: https://pixabay.com/de/illustrations/elefant-karikatur-charakter-zoo-2375697/ -->
      <Sorry
        v-if="!loading && isNotFoundError"
        title="Sorry, die angegebene PCR-Pool-Nummer exisitert nicht."
        image-url="https://cdn.pixabay.com/photo/2017/06/05/23/36/elephant-2375697_1280.png"
        description="Möglicherweise hat auch der Code nicht die richtige Länge."
        cta-url="/pcr-tests"
        cta-label="Zurück"
      />

      <!-- PCR TEST IS OUT OF RANGE  -->
      <!-- image from: https://www.maxpixel.net/Gray-Mammal-Elephant-Worried-Cartoon-Trunk-311860 -->
      <Sorry
        v-if="
          !loading && !isNotFoundError && (isNotValidError || !isInDateRange)
        "
        title="PCR Test Gültigkeit abgelaufen."
        image-url="https://www.maxpixel.net/static/photo/1x/Gray-Mammal-Elephant-Worried-Cartoon-Trunk-311860.png"
        description="Das Datum des Tests liegt außerhalb der Gültigkeit.<br />Leider kannst du keine Änderungen mehr vornehmen."
        cta-url="/pcr-tests"
        cta-label="Zurück"
      />

      <!-- PCR TEST ID DOES EXIST  -->
      <v-row v-if="!error && isInDateRange">
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

          <AttendeeList
            ref="attendeeListRef"
            :attendees="attendees"
            @refetch="refetchData"
          />
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Vue, Component } from "vue-property-decorator";
import Scanner from "../components/Scanner.vue";
import Sorry from "../components/Sorry.vue";
import AttendeeList from "../components/AttendeeList.vue";
import {
  getPcrPool,
  addAttendeeToPcrPool,
  PcrAttendee,
  PcrTest,
} from "../services/pcrTest";
import {
  ErrorConstants,
  ErrorResponse,
  isErrorOfType,
} from "../services/errorConstants";

@Component({
  name: "PcrTestView",
  components: { Scanner, Sorry, AttendeeList },
})
export default class PcrTestView extends Vue {
  private pcrPoolId: string = "";
  private pcrTest: PcrTest | null = null;
  private error: ErrorResponse | null = null;
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
      this.attendees.push(attendeeRes);
      // scroll to new element
      this.$vuetify.goTo(this.$refs.attendeeListRef, {
        easing: "easeInOutCubic",
      });
      await this.refetchData();
    }
  }

  private get isNotFoundError(): boolean {
    return isErrorOfType(ErrorConstants.NOT_FOUND_ERROR, this.error);
  }

  private get isNotValidError(): boolean {
    return isErrorOfType(ErrorConstants.VALIDATION_ERROR, this.error);
  }

  public async created(): Promise<void> {
    this.pcrPoolId = this.$route.params.poolId;
    this.pcrTest = await this.refetchData();
  }

  private async refetchData(): Promise<PcrTest | null> {
    this.loading = true;
    const pcrTestData = await getPcrPool(this.pcrPoolId).catch(
      (e) => (this.error = e)
    );
    this.loading = false;
    if (!pcrTestData) return null;

    return pcrTestData;
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
