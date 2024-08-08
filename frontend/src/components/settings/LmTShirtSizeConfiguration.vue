<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useToast } from 'vue-toastification'
import { showErrorToast } from '@/helper/fetch'
import { createTShirtSize, deleteTShirtSize, getTShirtSizes } from '@/services/tShirtSizes'

const toast = useToast()

const newTShirtSize = ref<string>('')
const tShirtSizes = ref<string[]>([])
const tShirtSizeToDelete = ref<string>('')
const tShirtSizeToReplace = ref<string>('')
const deleteModal = ref<boolean>(false)

const loadingTShirtSize = ref<string>('')
const deletingRequest = ref<boolean>(false)

onMounted(() => {
  getTShirtSizes().then((data) => (tShirtSizes.value = data))
})

const createTShirtSizeInternal = () => {
  loadingTShirtSize.value = newTShirtSize.value
  createTShirtSize({ size: newTShirtSize.value })
    .catch(async (err) => {
      await showErrorToast(toast, err, 'Neue TShirtgröße konnte nicht gespeichert werden.')
    })
    .then(() => {
      newTShirtSize.value = ''
      toast.success(`Neue TShirtgröße gespeichert.`)
      return getTShirtSizes()
    })
    .then((data) => (tShirtSizes.value = data))
}

const openDeleteDialog = (tShirtSize: string) => {
  tShirtSizeToDelete.value = tShirtSize
  deleteModal.value = true
}

const closeDeleteDialog = () => {
  tShirtSizeToDelete.value = ''
  deleteModal.value = false
}

const deleteEventInternal = () => {
  deletingRequest.value = true
  deleteTShirtSize({
    oldTShirtSize: tShirtSizeToDelete.value,
    newTShirtSize: tShirtSizeToReplace.value
  })
    .then(() => toast.success('TShirtgröße wurde gelöscht.'))
    .catch((err) => showErrorToast(toast, err, 'TShirtgröße konnte nicht gelöscht werden.'))
    .then(() => getTShirtSizes())
    .then((data) => (tShirtSizes.value = data))
    .then(() => {
      deletingRequest.value = false
      deleteModal.value = false
      tShirtSizeToDelete.value = ''
      tShirtSizeToReplace.value = ''
    })
}
</script>

<template>
  <v-card class="mb-16">
    <h2 class="pa-4 ml-md-4">TShirtgrößen</h2>
    <v-row justify="center">
      <v-col sm="12">
        <div class="pa-4">
          <h3>TShirtgröße erstellen</h3>
          <form v-on:submit.prevent="createTShirtSizeInternal()">
            <v-text-field
              v-model="newTShirtSize"
              label="TShirtgröße"
              required
              :variant="'underlined'"
            />
            <v-row class="v-row" justify="end">
              <v-btn
                color="primary"
                :loading="loadingTShirtSize !== '' && loadingTShirtSize === newTShirtSize"
                type="submit"
                rounded
              >
                <span>Erstellen</span>
              </v-btn>
            </v-row>
          </form>
        </div>

        <div class="px-4">
          <h3>TShirtgrößen verwalten</h3>
          <div class="flex-row flex-center">
            <v-card class="event-card mt-6 p-6">
              <p v-if="!tShirtSizes || tShirtSizes.length === 0" class="mb-0">
                ℹ️ Keine TShirtgrößen vorhanden.
              </p>
              <div class="flex-row event" v-for="tShirtSize in tShirtSizes" :key="tShirtSize">
                <div class="flex-row flex-grow">
                  <div class="flex-grow">
                    {{ tShirtSize }}
                  </div>

                  <div>
                    <v-icon medium @click.prevent="openDeleteDialog(tShirtSize)">
                      mdi-delete
                    </v-icon>
                  </div>
                </div>
              </div>
            </v-card>
          </div>
        </div>
      </v-col>
    </v-row>
  </v-card>
  <v-dialog v-model="deleteModal" persistent max-width="500">
    <v-card class="mb-0">
      <form v-on:submit.prevent="deleteEventInternal">
        <v-card-title class="headline">TShirtgröße ersetzen </v-card-title>
        <v-card-text>
          Durch welches TShirtgröße soll die Größe ({{ tShirtSizeToDelete }}) ersetzt werden?
          <v-select v-model="tShirtSizeToReplace" :items="tShirtSizes" label="TShirtgröße" required>
          </v-select>
        </v-card-text>
        <v-card-text v-if="tShirtSizeToDelete === tShirtSizeToReplace">
          <v-icon medium>mdi-warning</v-icon>
          Die Größe kann nicht durch die gleiche Größe ersetzt werden.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn rounded @click="closeDeleteDialog"> Schließen </v-btn>
          <v-btn
            color="primary"
            :loading="deletingRequest"
            type="submit"
            :disabled="tShirtSizeToReplace === '' || tShirtSizeToReplace === tShirtSizeToDelete"
          >
            Ersetzen
          </v-btn>
        </v-card-actions>
      </form>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.flex-row {
  display: flex;
}
.v-row {
  padding: 0 16px;
}
.flex-grow {
  flex: 1 1 auto;
}
.flex-center {
  justify-content: center;
}
.event {
  padding: 6px 16px;
}
.event-card {
  flex: 0 1 800px;
}
.link-button {
  text-decoration: none;
}
</style>
