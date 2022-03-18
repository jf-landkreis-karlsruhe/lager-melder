<template>
  <section>
    <v-card>
      <v-card-title>
        <div class="d-flex align-baseline">
          <h3 class="mr-4">{{ headlineText }}</h3>
          <div class="additional-information">
            Anzahl {{ headlineText }}:
            {{ attendeesWithNew.length - 1 }} (Anwesend: {{ enteredAttendees }})
          </div>
        </div>
      </v-card-title>
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="attendeesWithNew"
          :disable-pagination="true"
          :disable-items-per-page="true"
          :hide-default-footer="true"
          :item-class="statusClass"
        >
          <template v-slot:item.status="{ item }">
            <div
              v-if="item.status === attendeeStatusLeft"
              title="Zeltlager verlassen"
            >
              🏠
            </div>
            <div
              v-if="item.status === attendeeStatusEntered"
              title="Zeltlager betreten"
            >
              ⛺
            </div>
          </template>
          <template v-slot:item.firstName="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ item.firstName }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-text-field
                type="text"
                v-model="item.firstName"
                label="Vorname"
                required
                :form="createFormName(item)"
              />
            </div>
          </template>
          <template v-slot:item.lastName="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ item.lastName }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-text-field
                type="text"
                v-model="item.lastName"
                label="Nachname"
                required
                :form="createFormName(item)"
              />
            </div>
          </template>
          <template v-slot:item.tShirtSize="{ item }">
            <div style="max-width: 190px">
              <div v-if="!editingAttendeeIds.includes(item.id)">
                {{ tShirtSizeText(item.tShirtSize) }}
              </div>
              <div v-if="editingAttendeeIds.includes(item.id)">
                <v-select
                  v-model="item.tShirtSize"
                  :items="tShirtSizes"
                  item-text="text"
                  item-value="value"
                  label="TShirt Größe"
                  single-line
                  :error-messages="
                    item.tShirtSizeError ? ['TShirt Größe auswählen'] : []
                  "
                  :form="createFormName(item)"
                ></v-select>
              </div>
            </div>
          </template>
          <template v-slot:item.food="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ foodText(item.food) }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-select
                v-model="item.food"
                :items="foods"
                item-text="text"
                item-value="value"
                label="Essen"
                single-line
                required
                :form="createFormName(item)"
              ></v-select>
            </div>
          </template>
          <template v-slot:item.birthday="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ birthdayText(item.birthday) }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-text-field
                type="date"
                v-model="item.birthday"
                label="Geburtsdatum"
                required
                :form="createFormName(item)"
              />
            </div>
          </template>
          <template v-slot:item.additionalInformation="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ item.additionalInformation }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-textarea
                v-model="item.additionalInformation"
                :form="createFormName(item)"
                rows="1"
              />
            </div>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-row class="actions">
              <div v-if="!editingAttendeeIds.includes(item.id)">
                <v-icon medium class="mr-2" @click.prevent="editAttendee(item)">
                  mdi-pencil
                </v-icon>
              </div>
              <div v-if="editingAttendeeIds.includes(item.id)">
                <button type="sumbit" :form="createFormName(item)">
                  <v-icon medium class="mr-2"> mdi-content-save </v-icon>
                </button>
              </div>
              <span
                v-if="
                  !deletingAttendees.includes(item.id) &&
                  item.id !== newAttendeeId
                "
              >
                <v-icon medium @click.prevent="deleteAttendee(item)">
                  mdi-delete
                </v-icon>
              </span>
              <span v-if="deletingAttendees.includes(item.id)">
                <v-progress-circular indeterminate :size="24" color="green" />
              </span>
            </v-row>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
    <form
      v-for="attendee in attendeesWithNew"
      :key="attendee.id"
      :id="createFormName(attendee)"
      v-on:submit.prevent="saveAttendee(attendee)"
    ></form>
  </section>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";
import { AttendeeStatus, updateAttendee } from "../services/attendee";
import { tShirtSizeText, foodText, birthdayText } from "../helper/displayText";

import {
  // eslint-disable-next-line no-unused-vars
  Attendee,
  Food,
  TShirtSize,
  // eslint-disable-next-line no-unused-vars
  AttendeeRole,
  createAttendee,
} from "../services/attendee";

import { deleteAttendee } from "../services/attendee";
import { filterEnteredAttendees } from "@/helper/filterHelper";

interface AttendeeWithValidation extends Attendee {
  tShirtSizeError: boolean;
}

@Component({})
export default class AttendeesTable extends Vue {
  @Prop() attendees!: Attendee[];
  @Prop() headlineText!: string;
  @Prop() role!: AttendeeRole;
  @Prop() departmentId!: string;
  // eslint-disable-next-line no-unused-vars
  @Prop() attendeesChanged!: (change: number) => void;

  newAttendees: Attendee[] = [];
  deletedAttendeeIds: string[] = [];
  newAttendeeId = "newAttendee";
  deletingAttendees: string[] = [];
  editingAttendeeIds: string[] = [this.newAttendeeId];
  headers = [
    { text: "", value: "status" },
    { text: "Vorname", value: "firstName" },
    { text: "Nachname", value: "lastName" },
    { text: "Essen", value: "food" },
    { text: "TShirt Größe", value: "tShirtSize" },
    { text: "Geburtsdatum", value: "birthday" },
    { text: "Anmerkung", value: "additionalInformation" },
    { text: "", value: "actions", sortable: false },
  ];
  attendeeStatusLeft = AttendeeStatus.LEFT;
  attendeeStatusEntered = AttendeeStatus.ENTERED;

  deleteAttendee = (attendee: Attendee) => {
    this.deletingAttendees.push(attendee.id);
    deleteAttendee(attendee.id).then(() => {
      this.removeAttendeeIdFromList(attendee.id, this.deletingAttendees);
      this.deletedAttendeeIds.push(attendee.id);
      this.attendeesChanged(-1);
    });
  };
  editAttendee = (attendee: Attendee) =>
    this.editingAttendeeIds.push(attendee.id);

  saveAttendee = (attendee: AttendeeWithValidation) => {
    if (!attendee.tShirtSize) {
      attendee.tShirtSizeError = true;
      return;
    } else {
      attendee.tShirtSizeError = false;
    }

    if (attendee.id === this.newAttendeeId) {
      createAttendee(attendee).then((attendee) => {
        this.newAttendees.push(attendee);
        this.attendeesChanged(1);
      });
      return;
    }
    updateAttendee(attendee).then(() => {
      this.removeAttendeeIdFromList(attendee.id, this.editingAttendeeIds);
    });
  };
  removeAttendeeIdFromList = (id: string, list: string[]) => {
    const indexOfAttendee = list.indexOf(id);
    list.splice(indexOfAttendee, 1);
  };

  createFormName = (attendee: Attendee) =>
    `form-${this.departmentId}-${this.headlineText}-${attendee.id}`;

  get tShirtSizes(): { value: TShirtSize; text: string }[] {
    return Object.values(TShirtSize).map((value) => ({
      value,
      text: this.tShirtSizeText(value),
    }));
  }

  get foods(): { value: Food; text: string }[] {
    return Object.values(Food).map((value) => ({
      value,
      text: this.foodText(value),
    }));
  }

  get enteredAttendees(): number {
    return this.attendees.filter(filterEnteredAttendees).length;
  }

  birthdayText = birthdayText;
  foodText = foodText;
  tShirtSizeText = tShirtSizeText;

  statusClass(item: any): string {
    if (item.status === AttendeeStatus.ENTERED) {
      return "icon-first entered";
    }
    if (item.status === AttendeeStatus.LEFT) {
      return "icon-first left";
    }
    return "icon-first";
  }

  get attendeesWithNew(): AttendeeWithValidation[] {
    return this.attendees
      .concat(this.newAttendees)
      .filter((attendee) => !this.deletedAttendeeIds.includes(attendee.id))
      .concat([
        {
          status: null,
          id: this.newAttendeeId,
          firstName: "",
          lastName: "",
          birthday: "",
          food: Food.MEAT,
          tShirtSize: "" as TShirtSize,
          additionalInformation: "",
          role: this.role,
          departmentId: this.departmentId,
        },
      ])
      .map((attendee) => ({ ...attendee, tShirtSizeError: false }));
  }
}
</script>

<style scoped>
.additional-information {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.6);
}
.actions {
  width: 56px;
}
</style>

<style>
tr.left {
  background-color: #ff000030;
}
tr.entered {
  background-color: #00ff0030;
}
tr.icon-first > td:first-child,
thead.v-data-table-header > tr > th {
  padding: 0 8px !important;
}
</style>
