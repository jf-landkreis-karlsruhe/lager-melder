<script setup lang="ts">
import { ref } from 'vue'
import {type Department, DepartmentFeatures} from '../services/department'
import { registerNewDepartmentAndUser } from '../services/user'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'

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
    username: username.value,
    features: [DepartmentFeatures.CHILD_GROUPS, DepartmentFeatures.YOUTH_GROUPS]
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
        leaderEMail: newDepartmentWithUser.leaderEMail,
        shortName: '',
        phoneNumber: '',
        features: newDepartmentWithUser.features,
        headDepartmentName: ''
      })
      departmentName.value = ''
      leaderName.value = ''
      leaderMail.value = ''
      username.value = ''
    })
    .catch(async (err) => {
      loading.value = false
      await showErrorToast(toast, err, 'Feuerwehr konnte nicht angelegt werden.')
    })
}
</script>

<template>
  <div class="mb-10">
    <form v-on:submit.prevent="addDepartmentAndUser()">
      <v-text-field
        variant="underlined"
        v-model="departmentName"
        label="Name der Feuerwehr"
        required
      />
      <v-text-field variant="underlined" v-model="leaderName" label="Jugendwart" required />
      <v-text-field
        variant="underlined"
        v-model="leaderMail"
        type="email"
        label="Jugendwart Email"
        required
      />
      <v-text-field variant="underlined" v-model="username" label="Benutzername" required />
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
