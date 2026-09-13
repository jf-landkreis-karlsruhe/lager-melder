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
import { validateJuleika } from '@/services/juleika'

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

const isLeaderRole = computed(
  () => props.role === AttendeeRole.YOUTH_LEADER || props.role === AttendeeRole.CHILD_LEADER
)

// Juleika validation state
const juleikaValidated = ref<boolean>(false)
const juleikaValidating = ref<boolean>(false)
const juleikaValidationFailed = ref<boolean>(false)

const resetJuleikaState = () => {
  juleikaValidated.value = false
  juleikaValidationFailed.value = false
  current.value.juleikaExpireDate = undefined
}

const onJuleikaNumberChange = (value: string) => {
  current.value.juleikaNumber = value
  resetJuleikaState()
}

const onLastNameChange = (value: string) => {
  current.value.lastName = value
  resetJuleikaState()
}

const handleJuleikaBlur = async () => {
  if (!isLeaderRole.value) return
  if (!current.value.juleikaNumber || !current.value.lastName) return

  juleikaValidating.value = true
  juleikaValidationFailed.value = false
  juleikaValidated.value = false
  try {
    const result = await validateJuleika(current.value.juleikaNumber, current.value.lastName)
    juleikaValidated.value = result.valid
    juleikaValidationFailed.value = !result.valid
    if (result.valid) {
      current.value.juleikaExpireDate = result.expireDate
    } else {
      current.value.juleikaExpireDate = undefined
    }
  } catch {
    juleikaValidationFailed.value = true
    current.value.juleikaExpireDate = undefined
  } finally {
    juleikaValidating.value = false
  }
}

const foodList = computed<{ value: Food; title: string }[]>(() => {
  return Object.values(Food).map((value: Food) => {
    return { value, title: foodText(value), props: { prependIcon: FOOD_ICON_MAP[value] } }
  })
})
const helperDays = computed<HelperDaySelect[]>(() => getHelperDaySelect(props.eventDays))

const handleSubmit = () => {
  if (!isFormValid.value) return
  if (isLeaderRole.value && !juleikaValidated.value) return
  // Children and child leaders don't get a tShirt, but it is required currently, therefore this is a hack
  if (props.role === AttendeeRole.CHILD || props.role === AttendeeRole.CHILD_LEADER) {
    current.value.tShirtSize = props.tShirtSizes[0].title
  }
  emit('save', current.value)
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

    <!-- Row 1: Vorname, Nachname, Geburtsdatum -->
    <div class="d-flex flex-row align-start flex-wrap ga-4 mt-4">
      <v-text-field
        label="Vorname"
        variant="outlined"
        density="comfortable"
        class="form-field"
        required
        :rules="requiredRule"
        :modelValue="current.firstName"
        @update:modelValue="current.firstName = $event"
      ></v-text-field>
      <v-text-field
        label="Nachname"
        variant="outlined"
        density="comfortable"
        class="form-field"
        required
        :rules="requiredRule"
        :modelValue="current.lastName"
        @update:modelValue="onLastNameChange($event)"
        @blur="handleJuleikaBlur"
      ></v-text-field>
      <v-text-field
        v-if="props.role !== AttendeeRole.HELPER"
        type="date"
        v-model="current.birthday"
        label="Geburtsdatum"
        required
        variant="outlined"
        density="comfortable"
        class="form-field"
        :rules="requiredRule"
      />
    </div>

    <!-- Row 2: Juleika (only for leader roles) -->
    <div v-if="isLeaderRole" class="d-flex flex-row align-start flex-wrap ga-4 mt-2">
      <v-text-field
        label="Juleika-Nummer"
        variant="outlined"
        density="comfortable"
        class="form-field"
        :loading="juleikaValidating"
        :modelValue="current.juleikaNumber"
        @update:modelValue="onJuleikaNumberChange($event)"
        @blur="handleJuleikaBlur"
        :hint="juleikaValidated ? 'Juleika gültig' : 'Validierung erfolgt nach Eingabe von Nummer und Nachname'"
        persistent-hint
        :error="juleikaValidationFailed"
        :error-messages="juleikaValidationFailed ? 'Juleika ungültig oder nicht gefunden' : []"
      ></v-text-field>
      <v-text-field
        label="Juleika-Ablaufdatum"
        variant="outlined"
        density="comfortable"
        class="form-field"
        disabled
        :modelValue="current.juleikaExpireDate"
      ></v-text-field>
    </div>

    <!-- Row 3: T-Shirt + Essen links untereinander, Kommentar rechts gleiche Höhe -->
    <div class="d-flex flex-row align-stretch flex-wrap ga-4 mt-2">
      <div class="d-flex flex-column ga-4 form-field" style="min-width: 160px">
        <v-select
          v-if="props.role !== AttendeeRole.CHILD && props.role !== AttendeeRole.CHILD_LEADER"
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
        ></v-select>

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
        ></v-select>
      </div>

      <v-textarea
        label="Kommentar"
        variant="outlined"
        auto-grow
        clearable
        rows="4"
        class="form-field comment-field"
        :modelValue="current.additionalInformation"
        @update:modelValue="current.additionalInformation = $event"
      ></v-textarea>
    </div>

    <!-- Actions -->
    <div class="d-flex flex-row justify-space-between align-center ga-4 w-sm-50 w-xs-100 ml-auto mt-2">
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
        <v-btn
          style="flex: 1"
          color="primary"
          :loading="loading"
          prepend-icon="mdi-check"
          variant="flat"
          type="submit"
          :disabled="isLeaderRole && !juleikaValidated"
        >
          Speichern
        </v-btn>
      </v-defaults-provider>
    </div>
  </v-form>
</template>

<style lang="scss">
.form-field {
  flex: 1;
  min-width: 180px;
}

.comment-field {
  min-width: 320px;
  flex-grow: 2;
  display: flex;
  flex-direction: column;

  :deep(.v-field) {
    flex: 1;
    height: 100%;
  }

  :deep(.v-field__input) {
    height: 100%;
  }
}

// Remove default vuetify filter from v-icon
.v-icon {
  filter: none;
}
</style>
