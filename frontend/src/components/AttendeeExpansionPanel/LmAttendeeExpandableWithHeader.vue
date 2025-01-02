<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  createAttendee,
  getAttendeeDefault,
  getAttendeeTypeByRole
} from '@/services/attendee'
import { type Department, getDepartmentsForSelecting } from '@/services/department'
import LmAttendeeAddForm from '../AttendeeExpansionPanel/LmAttendeeAddForm.vue'
import LmAttendeeExpansionPanel from '../AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'
import { filterEnteredAttendees } from '@/helper/filterHelper'
import { getErrorMessage } from '@/services/errorConstants'
import { useToast } from 'vue-toastification'
import { getTShirtSizes } from '@/services/tShirtSizes'
import { type EventDays, getEventDays } from '@/services/eventDays'
import type { DepartmentSelect, TShirtSizeSelect } from '@/components/AttendeeExpansionPanel/helperTypes'

const props = defineProps<{
  headerLabel: string
  department: Department
  attendeeList: Attendee[]
  role: AttendeeRole
  attendeesCanBeEdited: boolean
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
const tShirtSizes = ref<TShirtSizeSelect[]>([])
const eventDays = ref<EventDays[]>([])
const departments = ref<DepartmentSelect[]>([])

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

const enteredAttendees = computed<number>(() => {
  return attendeeListWithAllAttributes.value.filter(filterEnteredAttendees).length
})

onMounted(async () => {
  tShirtSizes.value = (await getTShirtSizes()).map(
    (shirtSize) =>
      ({
        title: shirtSize,
        props: { prependIcon: 'mdi-tshirt-crew-outline' }
      }) as TShirtSizeSelect
  )
  eventDays.value = await getEventDays()

  getDepartmentsForSelecting().then((data) => {
    departments.value = [
      ...departments.value,
      ...data.map((department) => ({ value: department.id, title: department.name }))
    ]
  })
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
        <h2>{{ props.headerLabel }}</h2>
        <div class="additional-information d-none d-sm-inline-block">
          Gesamt: {{ attendeeListWithAllAttributes.length }} (Anwesend: {{ enteredAttendees }})
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
        :departments="departments"
        :event-days="eventDays"
        :t-shirt-sizes="tShirtSizes"
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
          :event-days="eventDays"
          :departments="departments"
          :t-shirt-sizes="tShirtSizes"
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
