<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { AttendeeStatus, updateAttendee } from '../services/attendee'
import { foodText, birthdayText } from '../helper/displayText'
import { Food, AttendeeRole, createAttendee } from '../services/attendee'
import type { Attendee } from '../services/attendee'
import { deleteAttendee as deleteAttendeeService } from '../services/attendee'
import { filterEnteredAttendees } from '@/helper/filterHelper'
import { computed } from 'vue'
import { getTShirtSizes, type TShirtSize } from '@/services/tShirtSizes'

interface AttendeeWithValidation extends Attendee {
  tShirtSizeError: boolean
}

const props = withDefaults(
  defineProps<{
    attendees: Attendee[]
    departmentId: number
    headlineText: string
    role: AttendeeRole
    disabled: boolean
    attendeesChanged?: (change: number) => void
  }>(),
  {
    disabled: false
  }
)

const newAttendees = ref<Attendee[]>([])
const deletedAttendeeIds = ref<string[]>([])
const newAttendeeId = ref<string>('newAttendee')
const deletingAttendees = ref<string[]>([])
const editingAttendeeIds = ref<string[]>([newAttendeeId.value])
const headers = ref<{ title: string; value: string; sortable?: boolean }[]>([
  { title: '', value: 'status' },
  { title: 'Vorname', value: 'firstName' },
  { title: 'Nachname', value: 'lastName' },
  { title: 'Essen', value: 'food' },
  { title: 'TShirt Größe', value: 'tShirtSize' },
  { title: 'Geburtsdatum', value: 'birthday' },
  { title: 'Anmerkung', value: 'additionalInformation' },
  { title: '', value: 'actions', sortable: false }
])
const tShirtSizes = ref<TShirtSize[]>([])

onMounted(() => {
  getTShirtSizes().then((data) => (tShirtSizes.value = data))
})

const deleteAttendee = (att: Attendee) => {
  deletingAttendees.value.push(att.id)
  deleteAttendeeService(att.id).then(() => {
    removeAttendeeIdFromList(att.id, deletingAttendees.value)
    deletedAttendeeIds.value.push(att.id)
    props.attendeesChanged?.(-1)
  })
}

const editAttendee = (att: Attendee) => {
  editingAttendeeIds.value.push(att.id)
}

const saveAttendee = (att: AttendeeWithValidation) => {
  if (!att.tShirtSize) {
    att.tShirtSizeError = true
    return
  } else {
    att.tShirtSizeError = false
  }

  if (att.id === newAttendeeId.value) {
    createAttendee(att).then((newAtt) => {
      newAttendees.value.push(newAtt)
      props.attendeesChanged?.(1)
    })
    return
  }
  updateAttendee(att).then(() => {
    removeAttendeeIdFromList(att.id, editingAttendeeIds.value)
  })
}

const removeAttendeeIdFromList = (id: string, list: string[]) => {
  const indexOfAttendee = list.indexOf(id)
  list.splice(indexOfAttendee, 1)
}

const createFormName = (att: Attendee) => {
  return `form-${props.departmentId}-${props.headlineText}-${att.id}`
}

const statusClass = (item: any): string => {
  if (item.status === AttendeeStatus.ENTERED) {
    return 'icon-first entered'
  }
  if (item.status === AttendeeStatus.LEFT) {
    return 'icon-first left'
  }
  return 'icon-first'
}

const foods = computed<{ value: Food; title: string }[]>(() => {
  return Object.values(Food).map((value) => ({
    value,
    title: foodText(value)
  }))
})

const enteredAttendees = computed<number>(() => {
  return props.attendees.filter(filterEnteredAttendees).length
})

const attendeesWithNew = computed<AttendeeWithValidation[]>(() => {
  return props.attendees
    .concat(newAttendees.value)
    .filter((attendee) => !deletedAttendeeIds.value.includes(attendee.id))
    .concat([
      {
        status: null,
        id: newAttendeeId.value,
        firstName: '',
        lastName: '',
        birthday: '',
        food: Food.MEAT,
        tShirtSize: '' as TShirtSize,
        additionalInformation: '',
        role: props.role,
        departmentId: props.departmentId
      }
    ])
    .map((attendee) => ({ ...attendee, tShirtSizeError: false }))
})
</script>

<template>
  <section>
    <v-card>
      <v-card-title>
        <div class="d-flex align-baseline">
          <h3 class="mr-4">{{ headlineText }}</h3>
          <div class="additional-information">
            Anzahl {{ headlineText }}: {{ attendeesWithNew.length - 1 }} (Anwesend:
            {{ enteredAttendees }})
          </div>
        </div>
      </v-card-title>
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="disabled ? (attendees as unknown as AttendeeWithValidation[]) : attendeesWithNew"
          :item-class="statusClass"
          :items-per-page="0"
        >
          <template v-slot:[`item.status`]="{ item }">
            <div v-if="item.status === AttendeeStatus.LEFT" title="Zeltlager verlassen">🏠</div>
            <div v-if="item.status === AttendeeStatus.ENTERED" title="Zeltlager betreten">⛺</div>
          </template>
          <template v-slot:[`item.firstName`]="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ item.firstName }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-text-field
                type="text"
                v-model="item.firstName"
                label="Vorname"
                variant="underlined"
                required
                :form="createFormName(item)"
              />
            </div>
          </template>
          <template v-slot:[`item.lastName`]="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ item.lastName }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-text-field
                type="text"
                v-model="item.lastName"
                label="Nachname"
                variant="underlined"
                required
                :form="createFormName(item)"
              />
            </div>
          </template>
          <template v-slot:[`item.tShirtSize`]="{ item }">
            <div style="max-width: 190px">
              <div v-if="!editingAttendeeIds.includes(item.id)">
                {{ item.tShirtSize }}
              </div>
              <div v-if="editingAttendeeIds.includes(item.id)">
                <v-select
                  v-model="item.tShirtSize"
                  :items="tShirtSizes"
                  label="TShirt Größe"
                  variant="underlined"
                  :required="true"
                  single-line
                  :error-messages="item.tShirtSizeError ? ['TShirt Größe auswählen'] : []"
                  :form="createFormName(item)"
                ></v-select>
              </div>
            </div>
          </template>
          <template v-slot:[`item.food`]="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ foodText(item.food) }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-select
                v-model="item.food"
                :items="foods"
                variant="underlined"
                label="Essen"
                single-line
                required
                :form="createFormName(item)"
              ></v-select>
            </div>
          </template>
          <template v-slot:[`item.birthday`]="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ birthdayText(item.birthday) }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-text-field
                type="date"
                v-model="item.birthday"
                label="Geburtsdatum"
                required
                variant="underlined"
                :form="createFormName(item)"
              />
            </div>
          </template>
          <template v-slot:[`item.additionalInformation`]="{ item }">
            <div v-if="!editingAttendeeIds.includes(item.id)">
              {{ item.additionalInformation }}
            </div>
            <div v-if="editingAttendeeIds.includes(item.id)">
              <v-textarea
                v-model="item.additionalInformation"
                :form="createFormName(item)"
                variant="underlined"
                rows="1"
              />
            </div>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-row class="actions">
              <div v-if="!disabled && !editingAttendeeIds.includes(item.id)">
                <v-icon medium class="mr-2" @click.prevent="editAttendee(item)">
                  mdi-pencil
                </v-icon>
              </div>
              <div v-if="!disabled && editingAttendeeIds.includes(item.id)">
                <button type="submit" :form="createFormName(item)">
                  <v-icon medium class="mr-2"> mdi-content-save </v-icon>
                </button>
              </div>
              <button
                v-if="
                  !disabled && !deletingAttendees.includes(item.id) && item.id !== newAttendeeId
                "
              >
                <v-icon medium @click.prevent="deleteAttendee(item)"> mdi-delete </v-icon>
              </button>
              <span v-if="!disabled && deletingAttendees.includes(item.id)">
                <v-progress-circular indeterminate :size="24" color="green" />
              </span>
            </v-row>
          </template>
          <template #bottom></template>
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
