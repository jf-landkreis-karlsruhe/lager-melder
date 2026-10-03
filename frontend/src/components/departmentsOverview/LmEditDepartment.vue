<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Roles, rolesText as rolesTitle } from '@/services/authentication'
import { type Department, DepartmentFeatures, updateDepartment } from '../../services/department'
import type { User } from '../../services/user'
import { updateRole, userForDepartment } from '../../services/user'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'

const toast = useToast()

const props = defineProps<{
  department: Department
}>()

const error = ref<boolean>(false)
const loading = ref<boolean>(false)
const roleLoading = ref<boolean>(false)
const user = ref<User>({} as User)
const leaderName = ref<string>(props.department.leaderName)
const leaderEmail = ref<string>(props.department.leaderEMail)
const shortName = ref<string>(props.department.shortName)
const headDepartmentName = ref<string>(props.department.headDepartmentName)
const phoneNumber = ref<string>(props.department.phoneNumber)
const features = ref<DepartmentFeatures[]>([...props.department.features])
const rolesList = ref<{ value: Roles; title: string }[]>([
  { value: Roles.USER, title: rolesTitle(Roles.USER) },
  {
    value: Roles.LK_KARLSRUHE,
    title: rolesTitle(Roles.LK_KARLSRUHE)
  },
  {
    value: Roles.SPECIALIZED_FIELD_DIRECTOR,
    title: rolesTitle(Roles.SPECIALIZED_FIELD_DIRECTOR)
  }
])

const onUpdateDepartment = () => {
  loading.value = true
  const updatedDepartment = {
    ...props.department,
    leaderName: leaderName.value,
    leaderEMail: leaderEmail.value,
    shortName: shortName.value,
    phoneNumber: phoneNumber.value,
    headDepartmentName: headDepartmentName.value,
    features: features.value
  }
  updateDepartment(updatedDepartment)
    .then(() => {
      loading.value = false
      toast.success(`${updatedDepartment.name} gespeichert.`)
    })
    .catch(async (err) => {
      loading.value = false
      await showErrorToast(toast, err, `${updatedDepartment.name} konnte nicht gepeichert werden.`)
    })
}

const onUpdateRole = () => {
  if (user.value.role) {
    roleLoading.value = true
    updateRole(user.value.id, user.value.role)
      .then((updatedUser) => {
        user.value = updatedUser
        roleLoading.value = false
        toast.success(`Rolle ${rolesTitle(updatedUser.role)} für ${updatedUser.username} gepeichert.`)
      })
      .catch(async (err) => {
        roleLoading.value = false
        await showErrorToast(
          toast,
          err,
          `Rolle ${rolesTitle(user.value.role)} konnte für ${user.value.username} nicht gepeichert werden.`
        )
      })
  }
}

onMounted(async () => {
  const newUser = await userForDepartment(props.department.id).catch(() => {
    error.value = true
    return undefined
  })
  if (newUser) {
    user.value = newUser
  }
})
</script>

<template>
  <div v-if="!error">
    <form @submit.prevent="onUpdateDepartment">
      <v-container>
        <v-row align="baseline" justify="space-between">
          <div>
            <h4>Stammdaten</h4>
          </div>
        </v-row>
        <v-row align="center" justify="center" wrap="wrap" style="gap: 20px">
          <div class="flex-grow-1" style="min-width: 200px">
            <v-text-field v-model="shortName" variant="underlined" label="Name kurz" />
          </div>
          <div class="flex-grow-1" style="min-width: 300px">
            <v-text-field v-model="headDepartmentName" variant="underlined" label="Gemeinde" />
          </div>
        </v-row>
        <v-row align="center" justify="end" wrap="wrap" style="gap: 20px">
          <div class="fixed-width">
            <v-text-field v-model="leaderName" variant="underlined" label="Jugendwart" required />
          </div>
          <div class="flex-grow">
            <v-text-field v-model="leaderEmail" variant="underlined" type="email" label="Jugendwart Email" required />
          </div>
        </v-row>
        <v-row align="center" justify="center" wrap="wrap" style="gap: 20px">
          <div class="flex-grow">
            <v-text-field v-model="phoneNumber" variant="underlined" label="Kontaktnummer" />
          </div>
        </v-row>

        <v-row>
          <div>
            <h5>Feature</h5>
          </div>
          <div class="d-flex space-between flex-wrap" style="gap: 20px">
            <v-switch
              v-model="features"
              color="primary"
              label="Teilnehmer"
              :value="DepartmentFeatures.YOUTH_GROUPS"
            ></v-switch>
            <v-switch
              v-model="features"
              color="primary"
              label="Kindergruppentag"
              :value="DepartmentFeatures.CHILD_GROUPS"
            ></v-switch>
            <v-switch v-model="features" color="primary" label="Z Kids" :value="DepartmentFeatures.ZKIDS"></v-switch>
            <v-switch v-model="features" color="primary" label="Helfer" :value="DepartmentFeatures.HELPER"></v-switch>
          </div>
        </v-row>
        <v-row justify="end">
          <v-btn type="submit" :loading="loading"> Stammdaten speichern</v-btn>
        </v-row>
      </v-container>
    </form>
    <form @submit.prevent="onUpdateRole">
      <v-container v-if="user.role !== 'ADMIN'">
        <v-row>
          <h4>Login</h4>
        </v-row>
        <v-row align="center" justify="space-between" wrap="wrap" style="gap: 20px">
          <div class="flex-grow">
            <v-text-field
              v-model="user.username"
              type="text"
              label="Benutzername"
              variant="underlined"
              hint="Read only"
              readonly
              required
            />
          </div>
          <div class="fixed-width">
            <v-select
              v-model="user.role"
              :items="rolesList"
              item-text="text"
              variant="underlined"
              item-value="value"
              label="Role"
            ></v-select>
          </div>
        </v-row>
        <v-row justify="end">
          <v-btn type="submit" :loading="roleLoading"> Login speichern</v-btn>
        </v-row>
      </v-container>
    </form>
  </div>
</template>

<style scoped>
.fixed-width {
  flex: 1 1 300px;
}

.flex-grow {
  flex: 2 1 300px;
}
</style>
