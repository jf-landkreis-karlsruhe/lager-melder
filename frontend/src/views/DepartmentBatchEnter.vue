<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getDepartment } from '../services/department'
import { type Attendees, getAttendeesForDepartment } from '../services/attendee'
import { useToast } from 'vue-toastification'
import { useRoute } from 'vue-router'
import type { Department } from '@/services/department'
import YouthAndAttendeeBatch from '@/components/batch/AttendeeBatchEvent.vue'

const toast = useToast()
const route = useRoute()
const departmentId = ref<number>(0)
const department = ref<Department | undefined>()
const attendees = ref<Attendees>({} as Attendees)
const eventCode = 'zeltin01'
const leaveCode = 'zeltout2'

onMounted(async () => {
  departmentId.value = Array.isArray(route.params.departmentId)
    ? parseInt(route.params.departmentId[0])
    : parseInt(route.params.departmentId)

  department.value = await getDepartment(departmentId.value)
  attendees.value = await getAttendeesForDepartment(departmentId.value)
})
</script>

<template>
  <div>
    <v-container class="event-root">
      <h1>{{ department?.name }} beitreten</h1>
      <YouthAndAttendeeBatch
        v-if="attendees.youths || attendees.youthLeaders"
        headline="Übernachtungsgäste"
        :attendeeGroups="[
          { headline: 'Jugendliche', attendees: attendees.youths },
          { headline: 'Betreuer', attendees: attendees.youthLeaders }
        ]"
        :departmentId="departmentId"
        :enterCode="eventCode"
        :leaveCode="leaveCode"
      ></YouthAndAttendeeBatch>
    </v-container>
  </div>
</template>

<style scoped lang="scss">
.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
