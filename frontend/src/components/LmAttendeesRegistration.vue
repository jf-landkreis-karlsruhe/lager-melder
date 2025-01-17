<script setup lang="ts">
import { computed, nextTick, onBeforeMount, onMounted, ref, type Ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  createAttendee,
  defaultAttendees,
  deleteAttendee as deleteAttendeeService,
  getAttendeeDefault,
  getAttendeesForDepartment,
  getAttendeeTypeByRole,
  updateAttendee as updateAttendeeService
} from '../services/attendee'
import { type Department, DepartmentFeatures, getDepartmentsForSelecting } from '../services/department'
import { filterByDepartmentAndSearch, filterEnteredAttendees } from '../helper/filterHelper'
import RegistrationInformation from './LmRegistrationInformation.vue'
import { getRegistrationEnd } from '@/services/settings'
import LmRegistrationEndBanner from '@/components/LmRegistrationEndBanner.vue'
import LmAttendeeExpandableWithHeader from './AttendeeExpansionPanel/LmAttendeeExpandableWithHeader.vue'
import type LmAttendeeExpansionPanel from './AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'
import { getTShirtSizes } from '@/services/tShirtSizes'
import type { DepartmentSelect, TShirtSizeSelect } from '@/components/AttendeeExpansionPanel/helperTypes'
import { type EventDays, getEventDays } from '@/services/eventDays'
import { getErrorMessage } from '@/services/errorConstants'
import { useToast } from 'vue-toastification'

const toast = useToast()

const props = defineProps<{
  department: Department
}>()

// Variables
const attendeeRoleYouth: AttendeeRole = AttendeeRole.YOUTH
const attendeeRoleYouthLeader: AttendeeRole = AttendeeRole.YOUTH_LEADER
const attendeeRoleChild: AttendeeRole = AttendeeRole.CHILD
const attendeeRoleChildLeader: AttendeeRole = AttendeeRole.CHILD_LEADER
const attendeeRoleZKid: AttendeeRole = AttendeeRole.Z_KID
const attendeeRoleHelper: AttendeeRole = AttendeeRole.HELPER

let attendeesRegistrationEnd: Date | null = null
let attendeesCanBeEdited: boolean = false
let childGroupRegistrationEnd: Date | null = null
let childGroupCanBeEdited: boolean = true

// REFS
const attendees = ref<Attendees>(defaultAttendees)
const filterInput = ref<string>('')
const tShirtSizes = ref<TShirtSizeSelect[]>([])
const eventDays = ref<EventDays[]>([])
const departments = ref<DepartmentSelect[]>([])
const loading = ref<boolean>(false)

// COMPUTED

const youthAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.youths, props.department.id, filterInput.value).map(
    (attendee) => ({ ...attendee, tShirtSizeError: false, helperDaysError: false })
  )
})

const youthLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.youthLeaders, props.department.id, filterInput.value)
})

const childAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.children, props.department.id, filterInput.value)
})

const zKidsAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.zKids, props.department.id, filterInput.value)
})

const helpersAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.helpers, props.department.id, filterInput.value)
})

const childLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.childLeaders, props.department.id, filterInput.value)
})

const enteredAttendeesCount = computed<number>(() => {
  return (
    attendees.value.youths.filter(filterEnteredAttendees).length +
    attendees.value.youthLeaders.filter(filterEnteredAttendees).length
  )
})

const totalAttendeeCount = computed<number>(() => {
  return attendees.value.youths.length + attendees.value.youthLeaders.length
})

const saveNewAttendee = async (
  newAttendee: Attendee,
  role: AttendeeRole,
  handleCloseAddNewForm: () => void,
  expansionPanelsRef: Ref<InstanceType<typeof LmAttendeeExpansionPanel>[]>
) => {
  if (!newAttendee) return

  loading.value = true

  const newAtt = await createAttendee(newAttendee).catch(async (err) => {
    loading.value = false
    handleCloseAddNewForm()
    const errorMessage = await getErrorMessage(err)
    if (errorMessage) {
      toast.error(errorMessage)
    }
  })
  if (!newAtt) return

  const type = getAttendeeTypeByRole(role)
  attendees.value = { ...attendees.value, [type]: [...attendees.value[type], newAttendee] }
  handleCloseAddNewForm()
  await nextTick()

  const lastExpansionPanel: HTMLDivElement = expansionPanelsRef.value[expansionPanelsRef.value.length - 1]?.$el
  scrollTo(lastExpansionPanel.offsetTop + 200, () => {
    lastExpansionPanel.classList.add('highlight-bump')
  })
  loading.value = false
}

const handleUpdateAttendee = async (att: Attendee, ownRef: InstanceType<typeof LmAttendeeExpansionPanel>) => {
  const attendeeWithAllProps: Attendee = {
    ...getAttendeeDefault(att.role, att.departmentId),
    ...att
  }
  // replace udpated attendee in local list
  attendees.value = {
    ...attendees.value,
    [getAttendeeTypeByRole(att.role)]: attendees.value[getAttendeeTypeByRole(att.role)].map((a) =>
      a.id === att.id ? attendeeWithAllProps : a
    )
  }
  loading.value = true
  // update attendee in database
  await updateAttendeeService(attendeeWithAllProps).catch((e) => {
    toast.error('Fehler beim Speichern des Teilnehmers')
    console.error(e)
    loading.value = false
  })
  // close expansion panel with manual click as other ways didn't work
  ownRef.$el.querySelector('button')?.click()
  loading.value = false
}

const deleteAttendee = async (att: Attendee) => {
  await deleteAttendeeService(att.id)
  const attendeeType = getAttendeeTypeByRole(att.role)
  attendees.value = {
    ...attendees.value,
    [attendeeType]: attendees.value[attendeeType].filter((a) => a.id !== att.id)
  }
}

onBeforeMount(async () => {
  const response = await getRegistrationEnd()
  attendeesRegistrationEnd = response.registrationEnd
  attendeesCanBeEdited = response.attendeesCanBeEdited
  childGroupRegistrationEnd = response.childGroupRegistrationEnd
  childGroupCanBeEdited = response.childGroupsCanBeEdited
})

onMounted(async () => {
  getAttendeesForDepartment(props.department.id).then((attendeeList) => {
    attendees.value = attendeeList
  })
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

// HELPER
/**
 * Native scrollTo with callback
 * @param offset - offset to scroll to
 * @param callback - callback function
 */
function scrollTo(offset: number, callback: () => void) {
  const fixedOffset = offset.toFixed()
  const onScroll = function () {
    if (window.pageYOffset.toFixed() === fixedOffset) {
      window.removeEventListener('scroll', onScroll)
      callback()
    }
  }

  window.addEventListener('scroll', onScroll)
  onScroll()
  window.scrollTo({
    top: offset,
    behavior: 'smooth'
  })
}
</script>

<template>
  <v-container>
    <div v-if="department && department.features.includes(DepartmentFeatures.YOUTH_GROUPS)">
      <div>
        <h1>Teilnehmer {{ department.name }}</h1>
        <div class="align-baseline">
          <h2 class="mb-0">Zeltlager</h2>
          <div>Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend: {{ enteredAttendeesCount }})</div>
        </div>
        <LmRegistrationEndBanner v-if="attendeesRegistrationEnd" :registrationEnd="attendeesRegistrationEnd" />

        <LmAttendeeExpandableWithHeader
          :department="props.department"
          header-label="Jugendliche"
          :attendee-list="youthAttendeeList"
          :role="attendeeRoleYouth"
          :attendeesCanBeEdited="attendeesCanBeEdited"
          :t-shirt-sizes="tShirtSizes"
          :departments="departments"
          :event-days="eventDays"
          :loading="loading"
          @save-new="saveNewAttendee"
          @update="handleUpdateAttendee"
          @delete="deleteAttendee"
        />
      </div>

      <LmAttendeeExpandableWithHeader
        header-label="Jugendleiter"
        :department="props.department"
        :attendee-list="youthLeaderAttendeeList"
        :role="attendeeRoleYouthLeader"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        :loading="loading"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />

      <div v-if="department && department.id">
        <RegistrationInformation :departmentId="department.id" :department-phone-number="department.phoneNumber" />
      </div>
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.CHILD_GROUPS)" class="mt-12 child-groups">
      <h2>Kindergruppentag</h2>
      <LmRegistrationEndBanner :registrationEnd="childGroupRegistrationEnd" />

      <LmAttendeeExpandableWithHeader
        header-label="Kinder"
        :department="props.department"
        :attendee-list="childAttendeeList"
        :role="attendeeRoleChild"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        :loading="loading"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />

      <LmAttendeeExpandableWithHeader
        header-label="Kindergruppenleiter"
        :department="props.department"
        :attendee-list="childLeaderAttendeeList"
        :role="attendeeRoleChildLeader"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        :loading="loading"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.ZKIDS)" class="mt-12">
      <h2>Z Kids Gruppe</h2>
      <LmRegistrationEndBanner :registrationEnd="attendeesRegistrationEnd" />

      <LmAttendeeExpandableWithHeader
        header-label="zKids"
        :department="props.department"
        :attendee-list="zKidsAttendeeList"
        :role="attendeeRoleZKid"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        :loading="loading"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.HELPER)" class="mt-12 helpers-group">
      <h2>Helfer Gruppe</h2>
      <LmRegistrationEndBanner :registrationEnd="childGroupRegistrationEnd" />

      <LmAttendeeExpandableWithHeader
        header-label="Helfer"
        :department="props.department"
        :attendee-list="helpersAttendeeList"
        :role="attendeeRoleHelper"
        :attendeesCanBeEdited="childGroupCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        :loading="loading"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />
    </div>
  </v-container>
</template>

<style lang="scss">
// Remove default vuetify filter from v-icon
.v-icon {
  filter: none;
}
</style>

<style scoped lang="scss">
.shirt-and-food {
  .shirt {
    gap: 0.1rem;

    .shirt__size {
      font-size: 0.8rem !important;
    }
  }
}

.child-groups,
.helpers-group {
  --margin-to-full-width: 200px;
  background-color: #f5f5f5;
  padding: 12px var(--margin-to-full-width);
  margin: 0 calc(-1 * var(--margin-to-full-width));
}

:deep(.highlight-bump) {
  animation: highlight-bump-anim 0.5s ease-in-out;
  transition: transform 0.5s ease-in-out;

  @keyframes highlight-bump-anim {
    0% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.1);
    }
    100% {
      transform: scale(1);
    }
  }
}
</style>
