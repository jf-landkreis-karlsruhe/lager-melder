<script lang="ts" setup>
import type { Attendee } from '@/services/attendee'
import { foodText, dateAsText, FOOD_ICON_MAP } from '../../helper/displayText'
import LmAttendeeAddForm from './LmAttendeeAddForm.vue'
import { AttendeeRole } from '@/services/attendee'

const props = defineProps<{
  attendee: Attendee
}>()

const emit = defineEmits<{
  (e: 'update', attendee: Attendee): void
  (e: 'delete', attendee: Attendee): void
}>()
</script>

<template>
  <v-expansion-panel>
    <v-expansion-panel-title expand-icon="mdi-menu-down">
      <div class="d-flex justify-space-between align-center flex-1-1-100">
        <div class="d-flex align-center" style="flex: 2">
          <v-icon size="large" class="mr-4">mdi-account-circle</v-icon>
          <div class="d-flex flex-column ga-2">
            <span>{{ props.attendee.firstName }} {{ ' ' }} {{ props.attendee.lastName }}</span>
            <span>*{{ dateAsText(props.attendee.birthday) }}</span>
          </div>
        </div>
        <div class="shirt-and-food d-flex justify-start" style="flex: 3">
          <div class="shirt d-flex flex-column justify-center align-center mr-10">
            <v-icon class="mb-1">mdi-tshirt-crew-outline</v-icon>
            <div class="name">{{ props.attendee.tShirtSize }}</div>
          </div>

          <div class="food d-flex flex-column justify-center align-center mr-10">
            <v-icon class="mb-1">{{ FOOD_ICON_MAP[props.attendee.food] }}</v-icon>
            <div class="name">{{ foodText(props.attendee.food) }}</div>
          </div>
        </div>

        <div v-if="props.attendee.juleikaNumber" class="d-flex align-center" style="flex: 3">
          <v-icon class="mr-1">mdi-card-account-details-outline</v-icon>
          <div class="d-flex flex-column ga-2">
            <span>{{ props.attendee.juleikaNumber }}</span>
            <span>{{ dateAsText(props.attendee.juleikaExpireDate) }}</span>
          </div>
        </div>
        <div
          v-else-if="props.attendee.role === AttendeeRole.Z_KID && props.attendee.departmentId"
          class="d-flex align-center"
          style="flex: 3"
        >
          <v-icon class="mr-1">mdi-account-group-outline</v-icon>
          <span>{{ props.attendee.departmentId }}</span>
        </div>
        <div v-else-if="(props.attendee.helperDays?.length ?? 0) > 0" class="d-flex align-center" style="flex: 3">
          <v-icon class="mr-1">mdi-handshake-outline</v-icon>
          <div class="d-flex flex-column ga-2">
            <span v-for="day in props.attendee.helperDays" :key="day">{{ day }}</span>
          </div>
        </div>

        <div :class="{ hidden: !props.attendee.additionalInformation }" class="description mr-2" style="flex: 4">
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
        @save="emit('update', props.attendee)"
        @delete="emit('delete', props.attendee)"
      />
    </v-expansion-panel-text>
  </v-expansion-panel>
</template>

<style scoped lang="scss">
.hidden {
  visibility: hidden;
}

.shirt {
  width: 40px;
}
.food {
  width: 80px;
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
