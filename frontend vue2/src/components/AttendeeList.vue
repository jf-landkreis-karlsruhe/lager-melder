<template>
  <v-row>
    <v-row justify="center" v-if="attendees.length > 0">
      <v-list subheader two-line class="attendee-list">
        <v-subheader inset class="attendee-subheader">Teilnehmer</v-subheader>

        <v-list-item v-for="attendee in attendees" :key="attendee.attendeeCode">
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
</template>

<script lang="ts">
import { Vue, Component, Emit, Prop } from "vue-property-decorator";
import { PcrAttendee, removeAttendeeFromPool } from "../services/pcrTest";

@Component({ name: "AttendeeList" })
export default class AttendeeList extends Vue {
  private pcrPoolId: string = "";
  private trashIndex = "";

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

      this.refetch();
    }
  }

  @Emit("refetch")
  public refetch(): string {
    return "refetch please";
  }

  @Prop({ required: true })
  public readonly attendees!: PcrAttendee[];

  public created(): void {
    this.pcrPoolId = this.$route.params.poolId;
  }
}
</script>

<style lang="scss">
.attendee-subheader {
  justify-content: center;
  margin-left: 0;
}
</style>
