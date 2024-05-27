<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { hasAdministrationRole } from '../../services/authentication'
import type { Department } from '../../services/department'
import { getDepartments, getMyDepartment } from '../../services/department'
import type { User } from '../../services/user'
import { getMe } from '../../services/user'
import EditDepartment from './LmEditDepartment.vue'
import AddDepartment from '../LmAddDepartment.vue'
import AttendeesShort from './LMAttendeesShort.vue'
import LmContainer from '../LmContainer.vue'
import TentsShort from './LMTentsShort.vue'

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
  <div v-if="hasAdministrationRole()">
    <div v-if="departments.length > 0">
      <LmContainer>
        <h1>Feuerwehren</h1>
        <div v-for="department in departments" :key="department.id">
          <h2>{{ department.name }}</h2>
          <EditDepartment :department="department" />
          <AttendeesShort :department-id="department.id" />
          <TentsShort :department-id="department.id" />
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
