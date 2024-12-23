<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDepartment } from '../services/department'
import { type Attendee, type Attendees, getAttendeesForDepartment } from '../services/attendee'
import { useToast } from 'vue-toastification'
import { useRoute } from 'vue-router'
import type { Department } from '@/services/department'

const toast = useToast()
const route = useRoute()
const departmentId = ref<number>(0)
const department = ref<Department | undefined>()
const attendees = ref<Attendees>({} as Attendees)
const youths = ref<AttendeeWithSelected[]>([])
const allSelected = ref<boolean>(false)
const selectedYouths = ref<string[]>([])

interface AttendeeWithSelected extends Attendee {
  selected: boolean
}

onMounted(async () => {
  departmentId.value = Array.isArray(route.params.departmentId)
    ? parseInt(route.params.departmentId[0])
    : parseInt(route.params.departmentId)

  department.value = await getDepartment(departmentId.value)
  attendees.value = await getAttendeesForDepartment(departmentId.value)
  youths.value = attendees.value.youths.map((youth) => ({ ...youth, selected: false }))
})

const selectAllYouths = () => {
  if (youths.value.some((youth) => youth.selected)) {
    for (let i = 0; i < youths.value.length; i++) {
      youths.value[i].selected = false
    }
    allSelected.value = false
  } else {
    for (let i = 0; i < youths.value.length; i++) {
      youths.value[i].selected = true
    }
    allSelected.value = true
  }
}
</script>

<template>
  <div>
    <v-container class="event-root">
      <h1>{{ department?.name }} beitreten</h1>
      <v-checkbox
        :value="allSelected"
        @change="selectAllYouths()"
        :label="`Alle Jugendliche auswählen`"
      />
      <form>
        <v-checkbox
          v-for="attendee in youths"
          :value="attendee.selected"
          :label="attendee.firstName + ' ' + attendee.lastName"
        />
      </form>
      {{ youths.map((y) => y.firstName + ' ' + y.lastName + ' ' + y.selected) }}
    </v-container>
  </div>
</template>

<style scoped lang="scss">
.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
