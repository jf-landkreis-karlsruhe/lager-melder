<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { DepartmentFeatures, getDepartment } from '../services/department'
import { type Attendees, getAttendeesPartOfDepartment } from '../services/attendee'
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
  attendees.value = await getAttendeesPartOfDepartment(departmentId.value)
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
      <SubsidyOverview v-if="departmentId" :department-id="departmentId" />
      <h1>{{ department?.name }} beitreten</h1>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.YOUTH_GROUPS)"
        headline="Jugendgruppe"
        :attendee-groups="[
          { headline: 'Jugendliche', attendees: attendees.youths || [] },
          { headline: 'Betreuer', attendees: attendees.youthLeaders || [] },
          { headline: 'Z Kids', attendees: attendees.zKids || [] }
        ]"
        :enter-code="eventCode"
        :leave-code="leaveCode"
      ></AttendeeBatchEvent>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.CHILD_GROUPS)"
        headline="Kindergruppen"
        :attendee-groups="[
          { headline: 'Kindergruppe', attendees: attendees.children || [] },
          { headline: 'Kindergruppenleiter', attendees: attendees.childLeaders || [] }
        ]"
        :enter-code="eventCode"
        :leave-code="leaveCode"
      ></AttendeeBatchEvent>
      <AttendeeBatchEvent
        v-if="hasFeature(DepartmentFeatures.HELPER)"
        headline="Helfer"
        :attendee-groups="[{ headline: 'Helfer', attendees: attendees.helpers || [] }]"
        :enter-code="eventCode"
        :leave-code="leaveCode"
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
