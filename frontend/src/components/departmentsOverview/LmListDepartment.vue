<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { hasAdministrationRole } from '../../services/authentication'
import { type Department, updatePauseDepartment } from '../../services/department'
import { getDepartments } from '../../services/department'
import EditDepartment from './LmEditDepartment.vue'
import AddDepartment from '../LmAddDepartment.vue'
import AttendeesShort from './LMAttendeesShort.vue'
import LmContainer from '../LmContainer.vue'
import TentsShort from './LMTentsShort.vue'
import { checkinDepartmentToEvent, type Event, EventType, getEventByType } from '@/services/event'
import { useToast } from 'vue-toastification'

const toast = useToast()
const departments = ref<Department[]>([])
const enterEvent = ref<Event>({} as Event)

const onDepartmentCreated = (newDepartment: Department) => {
  if (departments.value.find((dep) => dep.id === newDepartment.id)) {
    return
  }
  departments.value.push(newDepartment)
}

onMounted(async () => {
  departments.value = await getDepartments().then((deps) =>
    deps.sort((a, b) =>
      `${a.headDepartmentName} ${a.name}`.localeCompare(`${b.headDepartmentName} ${b.name}`)
    )
  )
  getEventByType(EventType.GLOBAL_ENTER).then((event: Event) => (enterEvent.value = event))
})

const checkinDepartment = (department: Department) => {
  checkinDepartmentToEvent(enterEvent.value, department.id).then(() =>
    toast.success(` ${department.name} erfolgreich eingecheckt`)
  )
}

const updatePauseDepartmentInternal = (department: Department) => {
  updatePauseDepartment(department.id, !department.paused).then(() => {
    department.paused = !department.paused
    toast.success(
      ` ${department.name} erfolgreich ${department.paused ? 'abgemeldet' : 'zurückgemeldet'}`
    )
  })
}
</script>

<template>
  <div v-if="hasAdministrationRole()">
    <div v-if="departments.length > 0">
      <LmContainer>
        <h1>Feuerwehren</h1>
        <div v-for="department in departments" :key="department.id">
          <h2><span v-if="department.paused">⏸️ </span>{{ department.name }}</h2>
          <EditDepartment :department="department" />
          <AttendeesShort :department-id="department.id" />
          <TentsShort :department-id="department.id" />
          <router-link :to="'/feuerwehr/' + department.id">Details</router-link>
          <div class="d-flex justify-space-between align-center flex-grow-1 flex-wrap mt-4">
            <v-btn @click="updatePauseDepartmentInternal(department)" class="checkin" rounded>
              <span v-if="department.paused">Zurückmelden</span>
              <span v-if="!department.paused">Abmelden</span>
            </v-btn>
            <v-btn @click="checkinDepartment(department)" class="checkin" rounded>
              ⛺ Teilnehmer einchecken
            </v-btn>
          </div>
          <v-divider class="mt-8 mb-16 border-opacity-15"></v-divider>
        </div>
      </LmContainer>
    </div>

    <div class="add-new-department">
      <LmContainer>
        <h1>Feuerwehr hinzufügen</h1>
        <AddDepartment :onDepartmentCreated="onDepartmentCreated" class="mb-8" />
      </LmContainer>
    </div>
  </div>
</template>

<style scoped lang="scss">
.add-new-department {
  background: #ddf2ff;
}
</style>
