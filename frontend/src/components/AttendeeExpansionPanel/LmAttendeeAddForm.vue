<script lang="ts" setup>
import { foodText } from '@/helper/displayText'
import { Food, type Attendee } from '@/services/attendee'
import { getEventDays } from '@/services/eventDays'
import { getTShirtSizes } from '@/services/tShirtSizes'
import { computed, onMounted, ref } from 'vue'
import { useDate } from 'vuetify'

const props = defineProps<{
  attendee: Attendee
  showCancel?: boolean
}>()

const emit = defineEmits<{
  (e: 'save', attendee: Attendee): void
  (e: 'delete', attendee: Attendee): void
  (e: 'cancel', attendee: Attendee): void
}>()

const current = ref<Attendee>({ ...props.attendee })
const tShirtSizes = ref<{ title: string; props: Object }[]>([])
const helperDays = ref<{ title: string; value: string }[]>([])

const adapter = useDate()

const birthdayAsDate = computed<unknown>(() => {
  return adapter.parseISO(current.value.birthday)
})

const foodList = computed<{ value: Food; title: string }[]>(() => {
  return Object.values(Food).map((value: Food) => {
    const foodIconMap = {
      [Food.MEAT]: 'mdi-food-drumstick-outline',
      [Food.VEGETARIAN]: 'mdi-cheese',
      [Food.MUSLIM]: 'mdi-food-halal',
      [Food.SPECIAL]: 'mdi-food-apple-outline',
      [Food.NONE]: 'mdi-food-off-outline'
    }
    return { value, title: foodText(value), props: { prependIcon: foodIconMap[value] } }
  })
})

const updateBirthday = (date: unknown) => {
  current.value.birthday = `${adapter.getYear(date)}-${adapter.getMonth(date) + 1}-${adapter.getDate(date)}`
}

const tshirtRules = [
  (value: string) => {
    if (value) return true

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

onMounted(async () => {
  tShirtSizes.value = (await getTShirtSizes()).map((shirtSize) => ({
    title: shirtSize,
    props: { prependIcon: 'mdi-tshirt-crew-outline' }
  }))
  const eventDays = await getEventDays()
  helperDays.value = eventDays.map((day) => ({ title: day.name, value: day.id }))
})
</script>

<template>
  <v-form @submit.prevent="emit('save', current)">
    <div class="d-flex flex-row ga-4 justify-space-between align-start mt-4">
      <!-- First column -->
      <div class="d-flex flex-column ga-3" style="flex: 6">
        <div class="d-flex align-center ga-4">
          <v-text-field
            label="Vorname"
            variant="outlined"
            density="comfortable"
            required
            :rules="requiredRule"
            :modelValue="current.firstName"
            @update:modelValue="current.firstName = $event"
          ></v-text-field>
          <v-text-field
            label="Nachname"
            variant="outlined"
            density="comfortable"
            required
            :rules="requiredRule"
            :modelValue="current.lastName"
            @update:modelValue="current.lastName = $event"
          ></v-text-field>
        </div>

        <v-date-input
          label="Geburtstag"
          variant="outlined"
          density="comfortable"
          required
          :rules="requiredRule"
          :modelValue="birthdayAsDate"
          @update:modelValue="updateBirthday"
        ></v-date-input>

        <div class="d-flex align-center ga-4">
          <v-select
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
            <template v-slot:selection="{ item, props }">
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
      <div class="d-flex flex-column ga-3" style="flex: 6">
        <v-select
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
          row-height="14"
          rows="4.1"
          auto-grow
          clearable
          :modelValue="current.additionalInformation"
          @update:modelValue="current.additionalInformation = $event"
        ></v-textarea>

        <div class="d-flex ga-4">
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
              prepend-icon="mdi-check"
              variant="flat"
              type="submit"
              @click="emit('save', current)"
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
