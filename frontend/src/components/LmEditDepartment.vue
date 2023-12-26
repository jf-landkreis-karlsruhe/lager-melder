<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { hasAdministrationRole, Roles, rolesText } from '@/services/authentication'
import { updateDepartment } from '../services/department'
import type { Department } from '../services/department'
import { sendRegistrationMail, userForDepartment, updateRole } from '../services/user'
import type { User } from '../services/user'
import { useToast } from 'vue-toastification'

const toast = useToast()

const props = defineProps<{
  department: Department
}>()

const error = ref<boolean>(false)
const loading = ref<boolean>(false)
const saved = ref<boolean>(false)
const roleLoading = ref<boolean>(false)
const sendingEmail = ref<boolean>(false)
const emailSent = ref<boolean>(false)
const dialogOpen = ref<boolean>(false)
const user = ref<User>({} as User)
const leaderName = ref<string>(props.department.leaderName)
const leaderEmail = ref<string>(props.department.leaderEMail)
const rolesList = ref<{ value: Roles; text: string }[]>([
  { value: Roles.USER, text: rolesText(Roles.USER) },
  {
    value: Roles.SPECIALIZED_FIELD_DIRECTOR,
    text: rolesText(Roles.SPECIALIZED_FIELD_DIRECTOR)
  }
])

const closeModal = () => {
  dialogOpen.value = false
  sendingEmail.value = false
  emailSent.value = false
}

const sendRegistrationEmail = () => {
  sendingEmail.value = true
  sendRegistrationMail(user.value.id).then(() => {
    emailSent.value = true
    sendingEmail.value = false
  })
}

const onUpdateDepartment = () => {
  loading.value = true
  updateDepartment(props.department)
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
          `Rolle ${rolesText(updatedUser.role)} für ${updatedUser.username} gepeichert.`
        )
      })
      .catch(() => {
        roleLoading.value = false
        toast.error(
          `Rolle ${rolesText(user.value.role)} konnte für ${
            user.value.username
          } nicht gepeichert werden.`
        )
      })
  }
}

onMounted(() => {
  userForDepartment(props.department.id)
    .then((newUser) => (user.value = newUser))
    .catch(() => (error.value = true))
})
</script>

<template>
  <div v-if="!error">
    <form v-on:submit.prevent="onUpdateDepartment">
      <v-text-field variant="underlined" v-model="leaderName" label="Jugendwart" required />
      <v-text-field
        variant="underlined"
        type="email"
        v-model="leaderEmail"
        label="Jugendwart Email"
        required
      />
      <v-container>
        <v-row justify="end">
          <v-dialog v-model="dialogOpen" persistent max-width="500">
            <template v-slot:activator="{ props }" v-if="hasAdministrationRole()">
              <v-btn rounded color="primary" dark v-bind="props" class="mb-2">
                Registriernugsmail versenden
              </v-btn>
            </template>
            <v-card>
              <template v-slot:title>Registriernugsmail versenden</template>
              <form v-on:submit.prevent="sendRegistrationEmail" class="mx-2">
                <v-card-text v-if="!emailSent">
                  Willst du wirklich?
                  <ol class="ml-6">
                    <li>Ein neues Passwort für {{ department.name }} setzen</li>
                    <li>Das neue Passwort an {{ department.leaderEMail }} schicken</li>
                  </ol>
                </v-card-text>
                <v-card-text v-if="emailSent">
                  <v-icon medium>mdi-check</v-icon> Registrierungsmail erfolgreich versendet
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn rounded @click="closeModal"> Schließen </v-btn>
                  <v-btn color="primary" v-if="!emailSent" :loading="sendingEmail" type="submit">
                    Senden
                  </v-btn>
                </v-card-actions>
              </form>
            </v-card>
          </v-dialog>
          <v-btn rounded color="primary" :loading="loading" type="submit" class="ml-3">
            <span v-if="saved"> <v-icon medium>mdi-check</v-icon> Gespeichert </span>
            <span v-if="!saved"> Speichern </span>
          </v-btn>
        </v-row>
      </v-container>
    </form>
    <form v-on:submit.prevent="onUpdateRole">
      <v-container v-if="user.role !== 'ADMIN'">
        <v-row align="center" justify="space-between">
          <div>
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
          <div>
            <v-select
              v-model="user.role"
              :items="rolesList"
              item-text="text"
              item-value="value"
              label="Role"
            ></v-select>
          </div>
          <div>
            <button type="submit" :loading="roleLoading">
              <v-icon medium class="mr-2"> mdi-content-save </v-icon>
            </button>
          </div>
        </v-row>
        <hr class="mt-16 mb-8" />
      </v-container>
    </form>
  </div>
</template>

<style scoped></style>
