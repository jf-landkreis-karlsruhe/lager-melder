<script setup lang="ts">
import AttendeesRegistration from '../components/LmAttendeesRegistration.vue'
import { onMounted, ref } from 'vue'
import { type Department, getDepartment } from '@/services/department'
import { useRoute } from 'vue-router'

const route = useRoute()
const department = ref<Department>({} as Department)

onMounted(() => {
  const departmentIdString = Array.isArray(route.params.eventCode)
    ? route.params.id[0]
    : (route.params.id as string)
  console.log(departmentIdString)
  const departmentId = parseInt(departmentIdString, 10)
  // departmentId to number
  getDepartment(departmentId).then((dep) => {
    department.value = dep
  })
})
</script>

<template>
  <div v-if="department.id">
    <AttendeesRegistration :department="department" />
  </div>
</template>