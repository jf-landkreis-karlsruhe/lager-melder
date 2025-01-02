<script lang="ts" setup>
import { computed, ref } from 'vue'
import type { Attendee } from '@/services/attendee'
import { AttendeeRole, AttendeeStatus } from '@/services/attendee'
import { dateAsText, FOOD_ICON_MAP, foodText } from '../../helper/displayText'
import LmAttendeeAddForm from './LmAttendeeAddForm.vue'
import type { VExpansionPanelTitle } from 'vuetify/components'
import { type EventDays } from '@/services/eventDays'
import type { DepartmentSelect, TShirtSizeSelect } from '@/components/AttendeeExpansionPanel/helperTypes'

const props = defineProps<{
  attendee: Attendee
  role: AttendeeRole
  roleTitle: string
  departments: DepartmentSelect[]
  eventDays: EventDays[]
  tShirtSizes: TShirtSizeSelect[]
}>()

const emit = defineEmits<{
  (e: 'update', attendee: Attendee): void
  (e: 'delete', attendee: Attendee): void
}>()

const expansionPanel = ref<InstanceType<typeof VExpansionPanelTitle> | null>(null)
const departments = ref<{ title: string; value: number }[]>([])
const sortedHelperDays = computed<EventDays[]>(() => {
  return (
    (
      props.attendee.helperDays
        ?.map((dayId) => props.eventDays.find((day) => day.id === dayId))
        .filter((day) => day) as EventDays[]
    ).sort((a, b) => a.dayOfEvent - b.dayOfEvent) ?? []
  )
})

const handleFormSave = (editedAttendee: Attendee) => {
  emit('update', editedAttendee)
}
</script>

<template>
  <v-expansion-panel>
    <v-expansion-panel-title expand-icon="mdi-menu-down" ref="expansionPanel">
      <div class="d-flex justify-space-between align-center flex-1-1-100">
        <div class="d-flex align-center" style="flex: 2">
          <div class="user-icon position-relative mr-4">
            <v-icon size="x-large" class="pr-3">mdi-account-circle</v-icon>
            <div
              v-if="props.attendee.status === AttendeeStatus.ENTERED"
              class="position-absolute is-attending-icon"
              title="Zeltlager betreten"
            >
              ⛺
            </div>
          </div>
          <div class="d-flex flex-column ga-2">
            <span>{{ props.attendee.firstName }} {{ ' ' }} {{ props.attendee.lastName }}</span>
            <span v-if="props.attendee.birthday">*{{ dateAsText(props.attendee.birthday) }}</span>
          </div>
        </div>
        <div class="shirt-and-food d-flex justify-start" style="flex: 3">
          <div class="shirt d-flex flex-column justify-center align-center mr-sm-10">
            <v-icon class="mb-1">mdi-tshirt-crew-outline</v-icon>
            <div class="name d-none d-sm-inline-block">{{ props.attendee.tShirtSize }}</div>
          </div>

          <div class="food d-flex flex-column justify-center align-center mr-sm-10">
            <v-icon class="mb-1">{{ FOOD_ICON_MAP[props.attendee.food] }}</v-icon>
            <div class="name d-none d-sm-inline-block">{{ foodText(props.attendee.food) }}</div>
          </div>
        </div>

        <div
          v-if="[AttendeeRole.YOUTH_LEADER, AttendeeRole.CHILD_LEADER].includes(props.role)"
          class="d-none d-sm-flex align-center"
          style="flex: 3"
        >
          <v-icon class="mr-2">mdi-card-account-details-outline</v-icon>
          <div class="d-flex flex-column ga-2">
            <span>{{ props.attendee.juleikaNumber ?? '-' }}</span>
            <span>{{ dateAsText(props.attendee.juleikaExpireDate ?? '-') }}</span>
          </div>
        </div>

        <div
          v-if="props.role === AttendeeRole.Z_KID && props.attendee.partOfDepartmentId"
          class="d-none d-sm-flex align-center"
          style="flex: 3"
        >
          <v-icon class="mr-2">mdi-account-group-outline</v-icon>
          <span>{{ departments.find((d) => d.value == props.attendee.partOfDepartmentId)?.title || '-' }}</span>
        </div>

        <div
          v-if="props.role === AttendeeRole.HELPER && (props.attendee.helperDays?.length ?? 0) > 0"
          class="d-none d-sm-flex align-center"
          style="flex: 3"
        >
          <v-icon class="mr-2">mdi-handshake-outline</v-icon>
          <div class="d-flex flex-column ga-2">
            <span v-for="day in sortedHelperDays" :key="day.id">{{ day.name }}</span>
          </div>
        </div>

        <div
          :class="{ hidden: !props.attendee.additionalInformation }"
          class="description mr-2 d-none d-sm-inline-block"
          style="flex: 4"
        >
          <i>
            <v-icon class="mr-1">mdi-information-outline</v-icon>
            {{ props.attendee.additionalInformation }}
          </i>
        </div>
      </div>
    </v-expansion-panel-title>
    <v-expansion-panel-text>
      <LmAttendeeAddForm
        :attendee="props.attendee"
        :role="props.role"
        :role-title="props.roleTitle"
        :event-days="props.eventDays"
        :departments="props.departments"
        :t-shirt-sizes="props.tShirtSizes"
        @save="handleFormSave"
        @delete="emit('delete', props.attendee)"
      />
    </v-expansion-panel-text>
  </v-expansion-panel>
</template>

<style scoped lang="scss">
.hidden {
  visibility: hidden;
}

.is-attending-icon {
  font-size: 1.4rem;
  bottom: -0.25rem;
  right: -0.25rem;
}

.shirt,
.food {
  width: 40px;
}
@media screen and (min-width: 600px) {
  .food {
    width: 90px;
  }
}
.shirt,
.food {
  overflow: hidden;
  .name {
    text-overflow: ellipsis;
    max-width: 80px;
  }
}
</style>
