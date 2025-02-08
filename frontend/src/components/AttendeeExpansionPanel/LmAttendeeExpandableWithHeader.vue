<script lang="ts" setup>
import { computed, ref, type Ref } from 'vue'
import { type Attendee, AttendeeRole, getAttendeeDefault } from '@/services/attendee'
import { type Department } from '@/services/department'
import LmAttendeeAddForm from '../AttendeeExpansionPanel/LmAttendeeAddForm.vue'
import LmAttendeeExpansionPanel from '../AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'
import { filterEnteredAttendees } from '@/helper/filterHelper'
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
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'update', attendee: Attendee, ownRef: InstanceType<typeof LmAttendeeExpansionPanel>): void
  (e: 'delete', attendee: Attendee): void
  (
    e: 'save-new',
    newAttendee: Attendee,
    type: AttendeeRole,
    closeHandler: () => void,
    expansionPanelsRef: Ref<InstanceType<typeof LmAttendeeExpansionPanel>[]>
  ): void
}>()

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

const openAddNewAttendeeForm = (role: AttendeeRole) => {
  if (!props.department || !props.department.id) {
    return
  }
  newAttendee.value = getAttendeeDefault(role, props.department.id)
  isAddNewFormModalVisible.value = true
}

const closeAddNewAttendeeForm = (): void => {
  isAddNewFormModalVisible.value = false
}

const handleSaveNewAttendee = (newAttendee: Attendee) => {
  emit('save-new', newAttendee, props.role, closeAddNewAttendeeForm, expansionPanels)
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
        <v-btn @click="openAddNewAttendeeForm(props.role)" color="primary" class="ma-0 mb-1" icon size="x-small">
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
        :loading="loading"
        :attendeesCanBeEdited="props.attendeesCanBeEdited"
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
          :loading="loading"
          @save="handleSaveNewAttendee($event)"
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
