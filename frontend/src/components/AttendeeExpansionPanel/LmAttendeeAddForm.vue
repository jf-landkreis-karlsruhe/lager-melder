<script lang="ts" setup>
import { computed, ref } from 'vue'
import { FOOD_ICON_MAP, foodText } from '@/helper/displayText'
import { type Attendee, AttendeeRole, Food } from '@/services/attendee'
import { type EventDays } from '@/services/eventDays'
import {
  type DepartmentSelect,
  getHelperDaySelect,
  type HelperDaySelect,
  type TShirtSizeSelect
} from '@/components/AttendeeExpansionPanel/helperTypes'

const props = defineProps<{
  attendee: Attendee
  role: AttendeeRole
  roleTitle: string
  tShirtSizes: TShirtSizeSelect[]
  eventDays: EventDays[]
  departments: DepartmentSelect[]
  showCancel?: boolean
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'save', attendee: Attendee): void
  (e: 'delete', attendee: Attendee): void
  (e: 'cancel', attendee: Attendee): void
}>()

const current = ref<Attendee>({ ...props.attendee })
const isFormValid = ref<boolean>(false)

const foodList = computed<{ value: Food; title: string }[]>(() => {
  return Object.values(Food).map((value: Food) => {
    return { value, title: foodText(value), props: { prependIcon: FOOD_ICON_MAP[value] } }
  })
})
const helperDays = computed<HelperDaySelect[]>(() => getHelperDaySelect(props.eventDays))

const handleSubmit = () => {
  if (isFormValid.value) {
    // Children and child leaders don't get a tShirt, but it is required currently, therefore this is a hack
    if (props.role === AttendeeRole.CHILD || props.role === AttendeeRole.CHILD_LEADER) {
      current.value.tShirtSize = props.tShirtSizes[0].title
    }
    emit('save', current.value)
  }
}

const tshirtRules = [
  (value: string) => {
    // Children and child leaders don't get a tShirt
    if (value || AttendeeRole.CHILD === props.role || AttendeeRole.CHILD_LEADER === props.role) return true

    return 'TShirt Größe auswählen'
  }
]

const helperDaysRules = [
  (value: string[]) => {
    if (value && value.length > 0) return true

    return 'Mindestens ein Helfertag muss ausgewählt werden'
  }
]

const requiredRule = [
  (value: string) => {
    if (value) return true

    return 'Pflichtfeld, bitte ausfüllen.'
  }
]
</script>

<template>
  <v-form @submit.prevent="handleSubmit" v-model="isFormValid">
    <h3>{{ props.roleTitle }} hinzufügen</h3>
    <div class="d-flex flex-row align-start flex-wrap ga-4 mt-4">
      <!-- First column -->
      <div class="d-flex flex-column form-column ga-2">
        <div class="d-flex align-center ga-4">
          <v-text-field
            label="Vorname"
            variant="outlined"
            density="comfortable"
            style="flex: 1"
            required
            :rules="requiredRule"
            :modelValue="current.firstName"
            @update:modelValue="current.firstName = $event"
          ></v-text-field>
          <v-text-field
            label="Nachname"
            variant="outlined"
            density="comfortable"
            style="flex: 1"
            required
            :rules="requiredRule"
            :modelValue="current.lastName"
            @update:modelValue="current.lastName = $event"
          ></v-text-field>
        </div>

        <v-text-field
          v-if="props.role !== AttendeeRole.HELPER"
          type="date"
          v-model="current.birthday"
          label="Geburtsdatum"
          required
          variant="outlined"
          density="comfortable"
          :rules="requiredRule"
        />

        <div class="d-flex align-center ga-4">
          <v-select
            v-if="props.role !== AttendeeRole.CHILD && props.role != AttendeeRole.CHILD_LEADER"
            :items="tShirtSizes"
            density="comfortable"
            variant="outlined"
            item-title="title"
            label="T-Shirt-Größe"
            required
            :rules="tshirtRules"
            :modelValue="current.tShirtSize"
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
            :items="foodList"
            density="comfortable"
            variant="outlined"
            item-title="title"
            label="Essen"
            required
            :rules="requiredRule"
            :modelValue="current.food"
            @update:modelValue="current.food = $event"
          >
            <template v-slot:selection="{ item }">
              <v-icon class="mr-4">{{ item.props.prependIcon }}</v-icon>
              {{ item.title }}
            </template>
            <template v-slot:item="{ props }">
              <v-list-item v-bind="props"></v-list-item>
            </template>
          </v-select>
        </div>
      </div>

      <!-- Second column -->
      <div class="d-flex flex-column justify-space-between form-column ga-2">
        <div
          v-if="[AttendeeRole.YOUTH_LEADER, AttendeeRole.CHILD_LEADER].includes(props.role)"
          class="d-flex flex-column ga-2"
        >
          <v-text-field
            label="Juleika-Nummer"
            variant="outlined"
            density="comfortable"
            :modelValue="current.juleikaNumber"
            @update:modelValue="current.juleikaNumber = $event"
          ></v-text-field>

          <v-text-field
            type="date"
            v-model="current.juleikaExpireDate"
            label="Juleika-Ablaufdatum"
            variant="outlined"
            density="comfortable"
          />
        </div>

        <v-select
          v-if="props.role === AttendeeRole.Z_KID && departments.length > 0"
          :items="departments"
          density="comfortable"
          variant="outlined"
          item-title="title"
          label="Teil von"
          required
          :rules="requiredRule"
          :modelValue="current.partOfDepartmentId"
          @update:modelValue="current.partOfDepartmentId = $event"
        >
        </v-select>

        <v-select
          v-if="props.role === AttendeeRole.HELPER"
          :items="helperDays"
          density="comfortable"
          variant="outlined"
          multiple
          chips
          item-title="title"
          label="Helfertage"
          :rules="helperDaysRules"
          :modelValue="current.helperDays"
          @update:modelValue="current.helperDays = $event"
        >
        </v-select>

        <v-textarea
          label="Kommentar"
          variant="outlined"
          row-height="12"
          :rows="[AttendeeRole.YOUTH, AttendeeRole.CHILD].includes(props.role) ? 7 : 2"
          auto-grow
          clearable
          :modelValue="current.additionalInformation"
          @update:modelValue="current.additionalInformation = $event"
        ></v-textarea>
      </div>
    </div>

    <!-- Second Row -->
    <div class="d-flex flex-row justify-space-between align-center ga-4 w-sm-50 w-xs-100 ml-auto">
      <v-defaults-provider :defaults="{ VIcon: { color: 'error' } }">
        <v-btn v-if="props.showCancel" style="flex: 1" variant="text" @click="emit('cancel', current)">
          Abbrechen
        </v-btn>
        <v-btn
          v-else
          style="flex: 1"
          prepend-icon="mdi-trash-can-outline"
          variant="text"
          @click="emit('delete', current)"
        >
          Löschen
        </v-btn>
      </v-defaults-provider>
      <v-defaults-provider :defaults="{ VIcon: { color: '#fff' } }">
        <v-btn style="flex: 1" color="primary" :loading="loading" prepend-icon="mdi-check" variant="flat" type="submit">
          Speichern
        </v-btn>
      </v-defaults-provider>
    </div>
  </v-form>
</template>

<style lang="scss">
.form-column {
  flex-basis: 100%;
}

@media screen and (min-width: 600px) {
  .form-column {
    flex: 6;
  }
}

// Remove default vuetify filter from v-icon
.v-icon {
  filter: none;
}
</style>
