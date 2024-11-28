<script lang="ts" setup>
import type { AttendeeWithValidation } from '@/services/attendee'
import { ref } from 'vue'

const props = defineProps<{
  attendee: AttendeeWithValidation
  showCancel: boolean
}>()

const current = ref<AttendeeWithValidation>({ ...props.attendee })

const emit = defineEmits<{
  (e: 'save', attendee: AttendeeWithValidation): void
  (e: 'delete', attendee: AttendeeWithValidation): void
  (e: 'cancel', attendee: AttendeeWithValidation): void
}>()
</script>

<template>
  <v-form @submit.prevent="">
    <div class="d-flex flex-row ga-4 justify-space-between align-start mt-4">
      <!-- First column -->
      <div class="d-flex flex-column ga-3" style="flex: 6">
        <div class="d-flex align-center ga-4">
          <v-text-field
            label="Vorname"
            variant="outlined"
            density="comfortable"
            required
            :modelValue="props.attendee.firstName"
            @update:modelValue="current.firstName = $event"
          ></v-text-field>
          <v-text-field
            label="Nachname"
            variant="outlined"
            density="comfortable"
            required
            :modelValue="props.attendee.lastName"
            @update:modelValue="current.lastName = $event"
          ></v-text-field>
        </div>

        <v-select
          :items="[
            { name: 'S', props: { prependIcon: 'mdi-tshirt-crew-outline' } },
            { name: 'M', props: { prependIcon: 'mdi-tshirt-crew-outline' } },
            { name: 'TODO', props: { prependIcon: 'mdi-tshirt-crew-outline' } }
          ]"
          density="comfortable"
          variant="outlined"
          item-title="name"
          label="T-Shirt-Größe"
          required
          :error-messages="props.attendee.tShirtSizeError ? ['TShirt Größe auswählen'] : []"
          :modelValue="props.attendee.tShirtSize || 'S'"
          @update:modelValue="current.tShirtSize = $event"
        >
          <template v-slot:selection="{ item }">
            <v-icon class="mr-4">mdi-tshirt-crew-outline</v-icon>{{ item.title }}
          </template>
          <template v-slot:item="{ props }">
            <v-list-item v-bind="props"></v-list-item>
          </template>
        </v-select>

        <v-select
          :items="[
            { name: 'Fleisch', props: { prependIcon: 'mdi-food-drumstick-outline' } },
            { name: 'Vegetarisch', props: { prependIcon: 'mdi-cheese' } },
            { name: 'TODO', props: { prependIcon: 'mdi-food-drumstick-outline' } }
          ]"
          density="comfortable"
          variant="outlined"
          item-title="name"
          label="Essen"
          required
          :modelValue="props.attendee.food"
          @update:modelValue="current.food = $event"
        >
          <template v-slot:selection="{ item }">
            <v-icon class="mr-4">mdi-food-drumstick-outline</v-icon>{{ item.title }}
          </template>
          <template v-slot:item="{ props }">
            <v-list-item v-bind="props"></v-list-item>
          </template>
        </v-select>
      </div>

      <!-- Second column -->
      <div class="d-flex flex-column ga-3" style="flex: 6">
        <v-select
          :items="[
            { name: 'Samstag vorher' },
            { name: 'Montag' },
            { name: 'Dienstag' },
            { name: 'Mittwoch' },
            { name: 'Donnerstag' },
            { name: 'Freitag' },
            { name: 'Samstag' },
            { name: 'Sonntag' }
          ]"
          density="comfortable"
          variant="outlined"
          multiple
          chips
          item-title="name"
          label="Helfertage"
          :error-messages="props.attendee.helperDaysError ? ['Mindestens ein Helfertag muss ausgewählt werden'] : []"
          :modelValue="props.attendee.helperDays"
          @update:modelValue="current.helperDays = $event"
        >
        </v-select>

        <v-textarea
          label="Kommentar"
          variant="outlined"
          row-height="14"
          rows="4.1"
          auto-grow
          clearable
          :modelValue="props.attendee.additionalInformation"
          @update:modelValue="current.additionalInformation = $event"
        ></v-textarea>

        <div class="d-flex ga-4">
          <v-defaults-provider :defaults="{ VIcon: { color: 'error' } }">
            <v-btn v-if="props.showCancel" style="flex: 1" variant="text" @click="emit('cancel', props.attendee)">
              Abbrechen
            </v-btn>
            <v-btn
              v-else
              style="flex: 1"
              prepend-icon="mdi-trash-can-outline"
              variant="text"
              @click="emit('delete', props.attendee)"
            >
              Löschen
            </v-btn>
          </v-defaults-provider>
          <v-defaults-provider :defaults="{ VIcon: { color: '#fff' } }">
            <v-btn
              style="flex: 1"
              color="primary"
              prepend-icon="mdi-check"
              variant="flat"
              type="submit"
              @click="emit('save', props.attendee)"
            >
              Speichern
            </v-btn>
          </v-defaults-provider>
        </div>
      </div>
    </div>
  </v-form>
</template>

<style lang="scss">
// Remove default vuetify filter from v-icon
.v-icon {
  filter: none;
}
</style>
