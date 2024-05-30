<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { hasAdministrationRole } from '../../services/authentication'
import type { Department } from '../../services/department'
import { getDepartments } from '../../services/department'
import EditDepartment from './LmEditDepartment.vue'
import AddDepartment from '../LmAddDepartment.vue'
import AttendeesShort from './LMAttendeesShort.vue'
import LmContainer from '../LmContainer.vue'
import TentsShort from './LMTentsShort.vue'

const departments = ref<Department[]>([])

const onDepartmentCreated = (newDepartment: Department) => {
  if (departments.value.find((dep) => dep.id === newDepartment.id)) {
    return
  }
  departments.value.push(newDepartment)
}

onMounted(async () => {
  departments.value = await getDepartments()
})
</script>

<template>
  <div v-if="hasAdministrationRole()">
    <div v-if="departments.length > 0">
      <LmContainer>
        <h1>Feuerwehren</h1>
        <div v-for="department in departments" :key="department.id">
          <h2>{{ department.name }}</h2>
          <EditDepartment :department="department" />
          <AttendeesShort :department-id="department.id" />
          <TentsShort :department-id="department.id" />
          <router-link :to="'/feuerwehr/' + department.id">Details</router-link>
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
