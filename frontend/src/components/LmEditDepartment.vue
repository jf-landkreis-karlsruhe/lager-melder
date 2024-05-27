<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Roles, rolesText as rolesTitle } from '@/services/authentication'
import type { Department } from '../services/department'
import { updateDepartment } from '../services/department'
import type { User } from '../services/user'
import { updateRole, userForDepartment } from '../services/user'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'

const toast = useToast()

const props = defineProps<{
  department: Department
}>()

const error = ref<boolean>(false)
const loading = ref<boolean>(false)
const saved = ref<boolean>(false)
const roleLoading = ref<boolean>(false)
const user = ref<User>({} as User)
const leaderName = ref<string>(props.department.leaderName)
const leaderEmail = ref<string>(props.department.leaderEMail)
const rolesList = ref<{ value: Roles; title: string }[]>([
  { value: Roles.USER, title: rolesTitle(Roles.USER) },
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
    leaderEMail: leaderEmail.value
  }
  updateDepartment(updatedDepartment)
    .then(() => {
      loading.value = false
      saved.value = true
      setTimeout(() => (saved.value = false), 2000)
    })
    .catch(() => {
      loading.value = false
    })
}

const onUpdateRole = () => {
  if (user.value.role) {
    roleLoading.value = true
    updateRole(user.value.id, user.value.role)
      .then((updatedUser) => {
        user.value = updatedUser
        roleLoading.value = false
        toast.success(
          `Rolle ${rolesTitle(updatedUser.role)} für ${updatedUser.username} gepeichert.`
        )
      })
      .catch(async (err) => {
        roleLoading.value = false
        await showErrorToast(
          toast,
          err,
          `Rolle ${rolesTitle(user.value.role)} konnte für ${
            user.value.username
          } nicht gepeichert werden.`
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
    <form v-on:submit.prevent="onUpdateDepartment">
      <v-container>
        <v-row>
          <h4>Kontakt</h4>
        </v-row>
        <v-row align="center" justify="end" wrap="wrap" style="gap: 20px">
          <div class="fixed-width">
            <v-text-field variant="underlined" v-model="leaderName" label="Jugendwart" required />
          </div>
          <div class="flex-grow">
            <v-text-field
              variant="underlined"
              type="email"
              v-model="leaderEmail"
              label="Jugendwart Email"
              required
            />
          </div>
          <div>
            <v-btn type="submit" :loading="loading" variant="text">
              <v-icon medium class="mr-2"> mdi-content-save</v-icon>
            </v-btn>
          </div>
        </v-row>
      </v-container>
    </form>
    <form v-on:submit.prevent="onUpdateRole">
      <v-container v-if="user.role !== 'ADMIN'">
        <v-row>
          <h4>Login</h4>
        </v-row>
        <v-row align="center" justify="space-between" wrap="wrap" style="gap: 20px">
          <div class="flex-grow">
            <v-text-field
              type="text"
              v-model="user.username"
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
          <div>
            <v-btn type="submit" :loading="roleLoading" variant="text">
              <v-icon medium class="mr-2"> mdi-content-save </v-icon>
            </v-btn>
          </div>
        </v-row>
        <v-divider class="mt-16 mb-8 border-opacity-15"></v-divider>
      </v-container>
    </form>
  </div>
</template>

<style scoped>
.fixed-width {
  flex: 1 1 200px;
}

.flex-grow {
  flex: 2 1 200px;
}
</style>