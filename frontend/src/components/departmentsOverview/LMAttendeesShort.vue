<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAttendeesForDepartment } from '../../services/attendee'

const distribution = ref<Distribution>({})

const props = defineProps<{
  departmentId: number
}>()

onMounted(async () => {
  const attendees = await getAttendeesForDepartment(props.departmentId)
  distribution.value = {
    youths: attendees.youths.length,
    youthLeader: attendees.youthLeaders.length,
    children: attendees.children.length,
    childLeaders: attendees.childLeaders.length
  }
})
interface Distribution {
  youths?: number
  youthLeader?: number
  children?: number
  childLeaders?: number
}
</script>

<template>
  <v-container>
    <v-row>
      <h4>Teilnehmer</h4>
    </v-row>
    <v-row>
      <v-table>
        <thead>
          <tr>
            <th>Jugendliche</th>
            <th>Jugendleiter</th>
            <th>Kinder (Kindergruppentag)</th>
            <th>Jugendleiter (Kindergruppentag)</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ distribution.youths }}</td>
            <td>{{ distribution.youthLeader }}</td>
            <td>{{ distribution.children }}</td>
            <td>{{ distribution.childLeaders }}</td>
          </tr>
        </tbody>
      </v-table>
    </v-row>
  </v-container>
</template>
<style scoped lang="scss"></style>
