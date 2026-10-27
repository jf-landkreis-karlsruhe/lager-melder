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
// a stored number with expire date was checked when it was saved, so editing other fields needs no new check
const juleikaValidated = ref<boolean>(!!props.attendee.juleikaNumber && !!props.attendee.juleikaExpireDate)
const juleikaValidating = ref<boolean>(false)
const juleikaValidationFailed = ref<boolean>(false)
// the running check, saving waits for it so a valid Juleika is not lost when the save button is clicked right away
let juleikaCheck: Promise<void> = Promise.resolve()
// only the response of the latest check counts, number or last name may have changed while a request was running
let juleikaRequestId = 0

const resetJuleikaState = () => {
  juleikaRequestId++
  juleikaValidated.value = false
  juleikaValidating.value = false
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

const handleJuleikaBlur = () => {
  if (!isLeaderRole.value) return
  if (!current.value.juleikaNumber || !current.value.lastName) return
  juleikaCheck = checkJuleika()
}

const checkJuleika = async () => {
  const requestId = ++juleikaRequestId
  juleikaValidating.value = true
  juleikaValidationFailed.value = false
  juleikaValidated.value = false
  try {
    const result = await validateJuleika(current.value.juleikaNumber, current.value.lastName)
    if (requestId !== juleikaRequestId) return
    // without an expire date the leader does not count as having a valid Juleika
    const valid = result.valid && !!result.expireDate
    juleikaValidated.value = valid
    juleikaValidationFailed.value = !valid
    current.value.juleikaExpireDate = valid ? result.expireDate : undefined
  } catch {
    if (requestId !== juleikaRequestId) return
    juleikaValidationFailed.value = true
    current.value.juleikaExpireDate = undefined
  } finally {
    if (requestId === juleikaRequestId) juleikaValidating.value = false
  }
}

// shown as hint, not as error: an error would make the form invalid, but leaders without valid Juleika may be saved
const juleikaHint = computed(() => {
  if (juleikaValidated.value) return 'Juleika gültig'
  if (juleikaValidationFailed.value) return 'Juleika ungültig oder nicht gefunden.'
  return 'Validierung erfolgt nach Eingabe von Nummer und Nachname'
})

const foodList = computed<{ value: Food; title: string }[]>(() => {
  return Object.values(Food).map((value: Food) => {
    return { value, title: foodText(value), props: { prependIcon: FOOD_ICON_MAP[value] } }
  })
})
const helperDays = computed<HelperDaySelect[]>(() => getHelperDaySelect(props.eventDays))

// leaders without a valid Juleika may be saved, they just do not count as leaders with Juleika
const handleSubmit = async () => {
  if (!isFormValid.value) return
  await juleikaCheck
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
  <v-form v-model="isFormValid" @submit.prevent="handleSubmit">
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
        :model-value="current.firstName"
        @update:model-value="current.firstName = $event"
      ></v-text-field>
      <v-text-field
        label="Nachname"
        variant="outlined"
        density="comfortable"
        class="form-field"
        required
        :rules="requiredRule"
        :model-value="current.lastName"
        @update:model-value="onLastNameChange($event)"
        @blur="handleJuleikaBlur"
      ></v-text-field>
      <v-text-field
        v-if="props.role !== AttendeeRole.HELPER"
        v-model="current.birthday"
        type="date"
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
        :model-value="current.juleikaNumber"
        :class="{ 'juleika-invalid': juleikaValidationFailed }"
        :hint="juleikaHint"
        persistent-hint
        @update:model-value="onJuleikaNumberChange($event)"
        @blur="handleJuleikaBlur"
      ></v-text-field>
      <v-text-field
        label="Juleika-Ablaufdatum"
        variant="outlined"
        density="comfortable"
        class="form-field"
        disabled
        :model-value="current.juleikaExpireDate"
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
          :model-value="current.tShirtSize"
          @update:model-value="current.tShirtSize = $event"
        >
          <template #selection="{ item }">
            <v-icon class="mr-4">mdi-tshirt-crew-outline</v-icon>{{ item.title }}
          </template>
          <template #item="{ props: itemProps }">
            <v-list-item v-bind="itemProps"></v-list-item>
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
          :model-value="current.food"
          @update:model-value="current.food = $event"
        >
          <template #selection="{ item }">
            <v-icon class="mr-4">{{ item.props.prependIcon }}</v-icon>
            {{ item.title }}
          </template>
          <template #item="{ props: itemProps }">
            <v-list-item v-bind="itemProps"></v-list-item>
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
          :model-value="current.partOfDepartmentId"
          @update:model-value="current.partOfDepartmentId = $event"
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
          :model-value="current.helperDays"
          @update:model-value="current.helperDays = $event"
        ></v-select>
      </div>

      <v-textarea
        label="Kommentar"
        variant="outlined"
        auto-grow
        clearable
        rows="4"
        class="form-field comment-field"
        :model-value="current.additionalInformation"
        @update:model-value="current.additionalInformation = $event"
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
        <v-btn style="flex: 1" color="primary" :loading="loading" prepend-icon="mdi-check" variant="flat" type="submit">
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

.juleika-invalid .v-messages {
  color: rgb(var(--v-theme-warning));
  opacity: 1;
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
