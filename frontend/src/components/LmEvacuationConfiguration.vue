<script setup lang="ts">
import { type Department, type TentMarkingRequest, updateTentMarkings } from '@/services/department'
import { ref } from 'vue'
import type { Distribution } from '@/services/event'
import type { EvacuationGroup } from '@/services/evacuationGroups'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'

const props = defineProps<{
  department: Department
  distribution: Distribution
  evacuationGroups: EvacuationGroup[]
}>()
const toast = useToast()

const department = ref<Department>(props.department)
const loading = ref<boolean>(false)
const edit = ref<boolean>(false)
const selectedTentMarkings = ref<string[]>(props.department.tentMarkings?.map((marking) => marking.name) || [])
const evacuationGroupsSelect = ref<{ title: string; value: EvacuationGroup }[]>([])

function selectableEvacuationGroups() {
  evacuationGroupsSelect.value = props.evacuationGroups.map((group) => ({
    title: group.name,
    value: group
  }))
}

const saveEvacuationGroups = () => {
  loading.value = true
  const tentMarkings = selectedTentMarkings.value.map((marking) => {
    return (
      department.value.tentMarkings?.find((tentMarking) => tentMarking.name === marking) ||
      ({
        name: marking
      } as TentMarkingRequest)
    )
  })
  if (!department.value.evacuationGroup) {
    toast.error(`Bitte wählen Sie eine Evakuierungsgruppe für ${department.value.name} aus.`)
    loading.value = false
    return
  }
  updateTentMarkings(department.value.id, department.value.evacuationGroup!.id, tentMarkings)
    .then((dep) => {
      loading.value = false
      edit.value = false
      department.value = dep
      toast.success(`Zelte für ${department.value.name} gespeichert.`)
    })
    .catch(async (err) => {
      loading.value = false
      await showErrorToast(toast, err, `Zelte für ${department.value.name} konnten nicht gespeichert werden.`)
    })
}

const startEdit = () => {
  selectedTentMarkings.value = department.value.tentMarkings?.map((marking) => marking.name) || []
  selectableEvacuationGroups()
  edit.value = true
}
</script>

<template>
  <div :style="'background: ' + department.evacuationGroup?.color + '33; padding: 9px;border-radius:3px'">
    <h4>Zelte</h4>
    <form v-if="edit" v-on:submit.prevent="saveEvacuationGroups()">
      <div>
        <v-select
          v-model="department.evacuationGroup"
          :items="evacuationGroupsSelect"
          label="Evakuierungsgruppen"
          required
        ></v-select>
      </div>
      <div>
        <v-combobox
          v-model="selectedTentMarkings"
          :items="[]"
          label="Zeltmarkierungen"
          hide-no-data
          closable-chips
          chips
          multiple
        ></v-combobox>
      </div>
      <div class="d-flex justify-end">
        <v-btn @click.prevent="edit = false" rounded class="mx-2">
          <v-icon medium class="mx-2"> mdi-cancel</v-icon>
        </v-btn>
        <v-btn type="submit" :loading="loading" rounded class="mx-2">
          <v-icon medium class="mx-2"> mdi-content-save</v-icon>
        </v-btn>
      </div>
    </form>
    <div v-if="!edit" class="d-flex flex-wrap justify-space-between align-baseline">
      <div class="d-flex flex-wrap flex-grow-1">
        <v-chip class="mx-2 my-2" v-for="tent in department.tentMarkings" size="x-large">
          {{ tent.name }}
        </v-chip>
        <p v-if="!department.tentMarkings || department.tentMarkings.length === 0">-</p>
      </div>
      <v-btn rounded @click.prevent="startEdit">
        <v-icon medium class="mr-2"> mdi-pencil</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<style scoped></style>
