<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDepartment } from '../services/department'
import { type Attendees, getAttendeesForDepartment } from '../services/attendee'
import { useToast } from 'vue-toastification'
import { useRoute } from 'vue-router'
import type { Department } from '@/services/department'

const toast = useToast()
const route = useRoute()
const departmentId = ref<number>(0)
const department = ref<Department | undefined>()
const attendees = ref<Attendees>([])
const selectedYouths = ref<string[]>([])

onMounted(async () => {
  departmentId.value = Array.isArray(route.params.departmentId)
    ? parseInt(route.params.departmentId[0])
    : parseInt(route.params.departmentId)

  // load member of department
  department.value = await getDepartment(departmentId.value)
  attendees.value = await getAttendeesForDepartment(departmentId.value)
})
const toggleSelection = (memberId) => {
  const index = selectedYouths.value.indexOf(memberId)
  if (index === -1) {
    selectedYouths.value.push(memberId)
  } else {
    selectedYouths.value.splice(index, 1)
  }
}
const selectAllYouths = () => {
  if (selectedYouths.value.length === attendees.value.youths.length) {
    selectedYouths.value = []
  } else {
    selectedYouths.value = attendees.value.youths.map((youth) => youth.id)
  }
}
</script>

<template>
  <div>
    <v-container class="event-root">
      <h1>{{ department?.name }} beitreten</h1>
      <v-checkbox @change="selectAllYouths()" :label="`Alle Jugendliche auswählen`" />
      <form>
        <v-checkbox
          v-for="attendee in attendees.youths"
          :id="attendee.id"
          :value="attendee.id"
          :selected="selectedYouths.includes(attendee.id)"
          @click="toggleSelection(attendee.id)"
          :label="attendee.firstName + ' ' + attendee.lastName"
        />
      </form>
      {{ selectedYouths }}
    </v-container>
  </div>
</template>

<style scoped lang="scss">
.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
