<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { hasSpecializedFieldDirectorRole } from '../services/authentication'
import type { Department } from '../services/department'
import { getDepartments, getMyDepartment } from '../services/department'
import type { StartDownloadRegistrationFiles } from '../services/settings'
import { getStartDownloadRegistrationFiles } from '../services/settings'
import { dateLocalized } from '@/helper/displayDate'
import FileList from './LmFileList.vue'
import YouthPlanDistribution from './LmYouthPlanDistribution.vue'
import LmContainer from './LmContainer.vue'
import { Group } from '@/services/registrationFiles'

const departmentId = ref<number | undefined>()
const departmentName = ref<string>('')
const departments = ref<Department[]>([])
const startDownloadRegistrationFiles = ref<StartDownloadRegistrationFiles | null>(null)
const localizedStartDate = ref<string | null>(null)

onMounted(() => {
  getMyDepartment()
    .then((myDepartment) => {
      departmentName.value = myDepartment.name
      departmentId.value = myDepartment.id
    })
    .then(() => {
      if (hasSpecializedFieldDirectorRole()) {
        return getDepartments({ onlyWithAttendees: true })
      }
    })
    .then((allDepartments) => {
      departments.value = allDepartments?.filter((department) => department.id !== departmentId.value) ?? []
    })

  getStartDownloadRegistrationFiles().then((newStartDownloadRegistrationFiles) => {
    startDownloadRegistrationFiles.value = newStartDownloadRegistrationFiles
    localizedStartDate.value = dateLocalized(newStartDownloadRegistrationFiles.startDownloadRegistrationFiles)
  })
})
</script>

<template>
  <LmContainer>
    <h1>Anmeldeunterlagen</h1>
    <div v-if="startDownloadRegistrationFiles && startDownloadRegistrationFiles.registrationFilesCanBeDownloaded">
      <p>
        Die hier aufgelisteten Dateien sind für die Anmeldung am Kreiszeltlager. Die Dateien sind mit den angemeldeten
        Teilnehmerlisten vorbefüllt und sollen so bei der unterschrieben Anmeldung abgegeben werden, damit eine
        reibungslose Anmeldung gewährleistet werden kann.
      </p>
      <FileList
        v-if="departmentId"
        :departmentId="departmentId"
        :departmentName="departmentName"
        :group="Group.PARTICIPANT"
      />
      <FileList
        v-if="departmentId"
        :departmentId="departmentId"
        :departmentName="departmentName"
        :group="Group.CHILD_GROUP"
      />
      <p>
        ⚠️ In einigen PDF Viewern kommt es zu Probleme mit der Anzeige, es funktionieren mit Google Chrome und Adobe
        Acrobat Reader. ⚠️
      </p>
      <div>
        <YouthPlanDistribution />
      </div>
      <div v-if="hasSpecializedFieldDirectorRole() && departments">
        <h2>Anmeldeunterlagen aller Feuerwehren</h2>
        <div v-for="department in departments" :key="department.id">
          <h3>Feuerwehr {{ department.name }}</h3>
          <FileList :departmentId="department.id" :departmentName="department.name" :group="Group.PARTICIPANT" />
          <FileList :departmentId="department.id" :departmentName="department.name" :group="Group.CHILD_GROUP" />
          <br />
        </div>
      </div>
    </div>
    <div v-if="startDownloadRegistrationFiles && !startDownloadRegistrationFiles.registrationFilesCanBeDownloaded">
      Die Anmeldeunterlagen sind noch nicht bereit zum Herunterladen. Sie stehen ab
      {{ localizedStartDate && localizedStartDate }} zur Verfügung.
    </div>
  </LmContainer>
</template>

<style scoped></style>
