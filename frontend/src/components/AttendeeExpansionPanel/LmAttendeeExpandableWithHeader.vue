<script lang="ts" setup>
import { computed, ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  createAttendee,
  getAttendeeDefault,
  getAttendeeTypeByRole
} from '@/services/attendee'
import { type Department } from '@/services/department'
import LmAttendeeAddForm from '../AttendeeExpansionPanel/LmAttendeeAddForm.vue'
import LmAttendeeExpansionPanel from '../AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'
import { filterEnteredAttendees } from '@/helper/filterHelper'
import { getErrorMessage } from '@/services/errorConstants'
import { useToast } from 'vue-toastification'
import type { DepartmentSelect, TShirtSizeSelect } from '@/components/AttendeeExpansionPanel/helperTypes'
import type { EventDays } from '@/services/eventDays'

const props = defineProps<{
  headerLabel: string
  department: Department
  attendeeList: Attendee[]
  role: AttendeeRole
  attendeesCanBeEdited: boolean
  tShirtSizes: TShirtSizeSelect[]
  eventDays: EventDays[]
  departments: DepartmentSelect[]
}>()

const emit = defineEmits<{
  (e: 'update', attendee: Attendee, ownRef: InstanceType<typeof LmAttendeeExpansionPanel>): void
  (e: 'delete', attendee: Attendee): void
  (e: 'save-new', newAttendee: Attendee, type: keyof Attendees): void
}>()

const toast = useToast()
const isAddNewFormModalVisible = ref<boolean>(false)
const newAttendee = ref<Attendee | undefined>(undefined)
const expansionPanels = ref<InstanceType<typeof LmAttendeeExpansionPanel>[]>([])

const attendeeListWithAllAttributes = computed<Attendee[]>(() => {
  return props.attendeeList.map((attendee) => {
    let newAttendee = { ...attendee }
    if (!attendee.helperDays) {
      newAttendee.helperDays = []
    }
    if (!attendee.juleikaNumber) {
      newAttendee.juleikaNumber = ''
    }
    if (!attendee.juleikaExpireDate) {
      newAttendee.juleikaExpireDate = ''
    }
    return newAttendee
  })
})

const enteredAttendeesCount = computed<number>(() => {
  return attendeeListWithAllAttributes.value.filter(filterEnteredAttendees).length
})

const addNewAttendee = (role: AttendeeRole) => {
  if (!props.department || !props.department.id) {
    return
  }
  newAttendee.value = getAttendeeDefault(role, props.department.id)
  isAddNewFormModalVisible.value = true
}

const saveAttendee = (att: Attendee) => {
  if (newAttendee.value && att.id === newAttendee.value.id) {
    createAttendee(att)
      .then((newAtt) => {
        if (!newAttendee.value) return
        const attendeeType = getAttendeeTypeByRole(newAttendee.value.role)
        emit('save-new', newAtt, attendeeType)
        isAddNewFormModalVisible.value = false
      })
      .catch(async (err) => {
        const errorMessage = await getErrorMessage(err)
        if (errorMessage) {
          toast.error(errorMessage)
        }
      })
  }
}
</script>

<template>
  <div>
    <div class="d-flex justify-space-between align-center">
      <div class="d-flex align-center ga-3">
        <h3>{{ props.headerLabel }}</h3>
        <div class="additional-information d-none d-sm-inline-block">
          Gesamt: {{ attendeeListWithAllAttributes.length }}
          <span v-if="enteredAttendeesCount > 0">(Anwesend: {{ enteredAttendeesCount }})</span>
        </div>
      </div>

      <label v-if="attendeesCanBeEdited">
        <span class="mr-2 d-none d-sm-inline-block" style="cursor: pointer">{{ props.headerLabel }} hinzufügen</span>
        <v-btn @click="addNewAttendee(props.role)" color="primary" class="ma-0 mb-1" icon size="x-small">
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </label>
    </div>

    <v-expansion-panels class="mb-4" :key="attendeeListWithAllAttributes.length">
      <LmAttendeeExpansionPanel
        v-for="(attendee, index) in attendeeListWithAllAttributes"
        :key="attendee.id"
        :attendee="attendee"
        :role="props.role"
        :role-title="props.headerLabel"
        :departments="props.departments"
        :event-days="props.eventDays"
        :t-shirt-sizes="props.tShirtSizes"
        ref="expansionPanels"
        @update="emit('update', $event, expansionPanels[index])"
        @delete="emit('delete', $event)"
      ></LmAttendeeExpansionPanel>
    </v-expansion-panels>

    <v-dialog v-model="isAddNewFormModalVisible" max-width="900">
      <v-card class="px-6 py-6">
        <LmAttendeeAddForm
          v-if="newAttendee"
          :department="department"
          :attendee="newAttendee"
          :role-title="props.headerLabel"
          :role="props.role"
          :event-days="props.eventDays"
          :departments="props.departments"
          :t-shirt-sizes="props.tShirtSizes"
          :show-cancel="true"
          @save="saveAttendee"
          @cancel="isAddNewFormModalVisible = false"
        />
      </v-card>
    </v-dialog>
  </div>
</template>

<style scoped lang="scss">
.additional-information {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.6);
}
</style>
