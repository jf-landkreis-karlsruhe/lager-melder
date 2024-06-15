<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { AttendeeRole, getAttendeesForDepartment } from '../../services/attendee'

const distribution = ref<Distribution>({})

const props = defineProps<{
  departmentId: number
}>()

onMounted(async () => {
  const attendees = await getAttendeesForDepartment(props.departmentId)
  distribution.value = attendees.reduce(
    (acc, attendee) => {
      if (attendee.role === AttendeeRole.YOUTH) {
        acc.youths++
      }
      if (attendee.role === AttendeeRole.YOUTH_LEADER) {
        acc.youthLeader++
      }
      return acc
    },
    { youths: 0, youthLeader: 0 }
  )
})
interface Distribution {
  youths?: number
  youthLeader?: number
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
          </tr>
        </thead>
        <tbody>
        <tr>
            <td>{{ distribution.youths }}</td>
            <td>{{ distribution.youthLeader }}</td>
          </tr>
        </tbody>
      </v-table>
    </v-row>
  </v-container>
</template>
<style scoped lang="scss"></style>
