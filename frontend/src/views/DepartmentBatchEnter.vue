<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { DepartmentFeatures, getDepartment } from '../services/department'
import { type Attendees, getAttendeesForDepartment } from '../services/attendee'
import { useToast } from 'vue-toastification'
import { useRoute } from 'vue-router'
import type { Department } from '@/services/department'
import AttendeeBatchEvent from '@/components/batch/AttendeeBatchEvent.vue'
import SubsidyOverview from '@/components/batch/SubsidyOverview.vue'

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

const hasFeature = (feature: DepartmentFeatures) => {
  if (!department.value) {
    return false
  }
  return department.value.features.includes(feature)
}
</script>

<template>
  <div>
    <v-container class="event-root">
      <SubsidyOverview v-if="departmentId" :departmentId="departmentId" />
      <h1>{{ department?.name }} beitreten</h1>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.YOUTH_GROUPS)"
        headline="Jugendgruppe"
        :attendeeGroups="[
          { headline: 'Jugendliche', attendees: attendees.youths || [] },
          { headline: 'Betreuer', attendees: attendees.youthLeaders || [] }
        ]"
        :enterCode="eventCode"
        :leaveCode="leaveCode"
      ></AttendeeBatchEvent>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.CHILD_GROUPS)"
        headline="Kindergruppen"
        :attendeeGroups="[
          { headline: 'Kindergruppe', attendees: attendees.children || [] },
          { headline: 'Kindergruppenleiter', attendees: attendees.childLeaders || [] }
        ]"
        :enterCode="eventCode"
        :leaveCode="leaveCode"
      ></AttendeeBatchEvent>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.ZKIDS)"
        headline="ZKids"
        :attendeeGroups="[{ headline: 'Z Kids', attendees: attendees.zKids || [] }]"
        :enterCode="eventCode"
        :leaveCode="leaveCode"
      ></AttendeeBatchEvent>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.HELPER)"
        headline="Helfer"
        :attendeeGroups="[{ headline: 'Helfer', attendees: attendees.helpers || [] }]"
        :enterCode="eventCode"
        :leaveCode="leaveCode"
      ></AttendeeBatchEvent>
    </v-container>
  </div>
</template>

<style scoped lang="scss">
.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
