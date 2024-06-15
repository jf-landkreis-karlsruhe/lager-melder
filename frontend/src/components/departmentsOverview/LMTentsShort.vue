<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getTentsForDepartment, type Tents } from '@/services/tents'

const tents = ref<Tents>({} as Tents)

const props = defineProps<{
  departmentId: number
}>()

onMounted(async () => {
  tents.value = await getTentsForDepartment(props.departmentId)
})
</script>

<template>
  <v-container>
    <v-row>
      <h4>Zelte</h4>
    </v-row>
    <v-row align="center" justify="start" wrap="wrap" style="gap: 20px">
      <div>SG200: {{ tents.sg200 }}</div>
      <div>SG20: {{ tents.sg20 }}</div>
      <div>SG30: {{ tents.sg30 }}</div>
      <div>SG40: {{ tents.sg40 }}</div>
      <div>SG50: {{ tents.sg50 }}</div>
      <div>Total: {{ tents.sg200 + tents.sg20 + tents.sg30 + tents.sg40 + tents.sg50 }}</div>
    </v-row>
  </v-container>
</template>
<style scoped lang="scss"></style>
