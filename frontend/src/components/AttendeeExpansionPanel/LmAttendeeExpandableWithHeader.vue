<script lang="ts" setup>
import { ref } from 'vue'
import {
  AttendeeRole,
  createAttendee,
  getAttendeeDefault,
  getAttendeeTypeByRole,
  type Attendee,
  type Attendees
} from '@/services/attendee'
import type { Department } from '@/services/department'
import LmAttendeeAddForm from '../AttendeeExpansionPanel/LmAttendeeAddForm.vue'
import LmAttendeeExpansionPanel from '../AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'

const props = defineProps<{
  headerLabel: string
  department: Department
  attendeeList: Attendee[]
  role: AttendeeRole
  attendeesCanBeEdited: boolean
}>()

const emit = defineEmits<{
  (e: 'update', attendee: Attendee): void
  (e: 'delete', attendee: Attendee): void
  (e: 'save-new', newAttendee: Attendee, type: keyof Attendees): void
}>()

const isAddNewFormModalVisible = ref<boolean>(false)
const newAttendee = ref<Attendee | undefined>(undefined)

const addNewAttendee = (role: AttendeeRole) => {
  if (!props.department || !props.department.id) {
    return
  }
  newAttendee.value = getAttendeeDefault(role, props.department.id)
  isAddNewFormModalVisible.value = true
}

const saveAttendee = (att: Attendee) => {
  if (newAttendee.value && att.id === newAttendee.value.id) {
    createAttendee(att).then((newAtt) => {
      if (!newAttendee.value) return
      const attendeeType = getAttendeeTypeByRole(newAttendee.value.role)
      emit('save-new', newAtt, attendeeType)
    })
    isAddNewFormModalVisible.value = false
  }
}
</script>

<template>
  <div>
    <div class="d-flex justify-space-between align-center">
      <h2>{{ props.headerLabel }}</h2>

      <label v-if="attendeesCanBeEdited">
        <span class="mr-2" style="cursor: pointer">{{ props.headerLabel }} hinzufügen</span>
        <v-btn @click="addNewAttendee(props.role)" color="primary" class="ma-0 mb-1" icon size="x-small">
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </label>
    </div>

    <v-expansion-panels class="mb-4" :key="props.attendeeList.length">
      <LmAttendeeExpansionPanel
        v-for="attendee in props.attendeeList"
        :key="attendee.id"
        :attendee="attendee"
        :role="props.role"
        @update="emit('update', $event)"
        @delete="emit('delete', $event)"
      ></LmAttendeeExpansionPanel>
    </v-expansion-panels>

    <v-dialog v-model="isAddNewFormModalVisible" max-width="900">
      <v-card class="px-6 py-6">
        <LmAttendeeAddForm
          v-if="newAttendee"
          :department="department"
          :attendee="newAttendee"
          :role="props.role"
          :show-cancel="true"
          @save="saveAttendee"
          @cancel="isAddNewFormModalVisible = false"
        />
      </v-card>
    </v-dialog>
  </div>
</template>

<style scoped lang="scss"></style>
