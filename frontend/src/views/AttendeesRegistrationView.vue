<script setup lang="ts">
import AttendeesRegistration from '../components/LmAttendeesRegistration.vue'
import { onMounted, ref } from 'vue'
import { type Department, getMyDepartment } from '@/services/department'

const department = ref<Department>({} as Department)
onMounted(() => {
  getMyDepartment().then((myDepartment) => {
    department.value = myDepartment
  })
})
</script>

<template>
  <div v-if="department.id">
    <AttendeesRegistration :department="department" />
  </div>
</template>
