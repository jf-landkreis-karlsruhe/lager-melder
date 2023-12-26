<script setup lang="ts">
import { ref } from 'vue'
import type { Department } from '../services/department'
import { registerNewDepartmentAndUser } from '../services/user'
import { useToast } from 'vue-toastification'

const toast = useToast()

const props = defineProps<{
  onDepartmentCreated: (department: Department) => void
}>()

const departmentName = ref<string>('')
const leaderName = ref<string>('')
const leaderMail = ref<string>('')
const username = ref<string>('')
const loading = ref<boolean>(false)
const created = ref<boolean>(false)

const addDepartmentAndUser = () => {
  loading.value = true

  const departmentWithUserReguest = {
    departmentName: departmentName.value,
    leaderName: leaderName.value,
    leaderEMail: leaderMail.value,
    username: username.value
  }
  registerNewDepartmentAndUser(departmentWithUserReguest)
    .then((newDepartmentWithUser) => {
      loading.value = false
      created.value = true
      toast.success(`Feuerwehr ${newDepartmentWithUser.departmentName} erfolgreich angelegt.`)
      props.onDepartmentCreated({
        id: newDepartmentWithUser.departmentId,
        name: newDepartmentWithUser.departmentName,
        leaderName: newDepartmentWithUser.leaderName,
        leaderEMail: newDepartmentWithUser.leaderEMail
      })
      departmentName.value = ''
      leaderName.value = ''
      leaderMail.value = ''
      username.value = ''
    })
    .catch((e: any) => {
      loading.value = false
      toast.error(`Feuerwehr konnte nicht angelegt werden. (${e})`)
    })
}
</script>

<template>
  <div class="mb-10">
    <form v-on:submit.prevent="addDepartmentAndUser()">
      <v-text-field v-model="departmentName" label="Name der Feuerwehr" required />
      <v-text-field v-model="leaderName" label="Jugendwart" required />
      <v-text-field v-model="leaderMail" type="email" label="Jugendwart Email" required />
      <v-text-field v-model="username" label="Benutzername" required />
      <v-row justify="end">
        <v-btn color="primary" :loading="loading" type="submit" rounded>
          <span v-if="created"> <v-icon medium>mdi-check</v-icon> Erstellt </span>
          <span v-if="!created"> Erstellen </span>
        </v-btn>
      </v-row>
    </form>
  </div>
</template>

<style scoped></style>
