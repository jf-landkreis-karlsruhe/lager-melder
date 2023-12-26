<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { hasAdministrationRole } from '../services/authentication'
import { getDepartments, getMyDepartment } from '../services/department'
import type { Department } from '../services/department'
import { getMe } from '../services/user'
import type { User } from '../services/user'
import EditDepartment from './LmEditDepartment.vue'
import AddDepartment from './LmAddDepartment.vue'

const myDepartment = ref<Department>({} as Department)
const departments = ref<Department[]>([])
const password = ref<string>('')
const repeatPassword = ref<string>('')
const user = ref<User>({} as User)
const showPasswordError = ref<boolean>(false)

const onDepartmentCreated = (newDepartment: Department) => {
  if (departments.value.find((dep) => dep.id === newDepartment.id)) {
    return
  }
  departments.value.push(newDepartment)
}

watch([password, repeatPassword], () => {
  showPasswordError.value = false
})

onMounted(async () => {
  const newUser = await getMe()
  user.value = newUser
  const newDepartment = await getMyDepartment()
  myDepartment.value = newDepartment
  if (hasAdministrationRole()) {
    const newDepartments = await getDepartments()
    departments.value = (newDepartments || []).filter(
      (newDepartment) => newDepartment.id !== myDepartment.value.id
    )
  }
})
</script>

<template>
  <div>
    <v-container>
      <div v-if="hasAdministrationRole() && departments.length > 0">
        <v-row justify="center">
          <v-col sm="12" md="8" lg="6" xl="6">
            <h1>Feuerwehren</h1>
            <div v-for="department in departments" :key="department.id">
              <h2>{{ department.name }}</h2>
              <EditDepartment :department="department" class="mb-8" />
            </div>
          </v-col>
        </v-row>
      </div>
    </v-container>

    <div v-if="hasAdministrationRole()" class="add-new-department">
      <v-container>
        <v-row justify="center">
          <v-col sm="12" md="8" lg="6" xl="6">
            <h1>Feuerwehr hinzufügen</h1>
            <AddDepartment :onDepartmentCreated="onDepartmentCreated" class="mb-8" />
          </v-col>
        </v-row>
      </v-container>
    </div>
  </div>
</template>

<style scoped lang="scss">
.add-new-department {
  background: #ddf2ff;
}
</style>
