<script setup lang="ts">
import { ref } from 'vue'
import {
  sendReminderMail,
  sendRegistrationFinishedMail,
  SentTo,
  sentToReadable
} from '../../services/mails'
import MailReminderText from './LmMailReminderText.vue'
import MailRegistrationEndText from './LmMailRegistrationEndText.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()

const reminderDialogOpen = ref<boolean>(false)
const registrationEndDialogOpen = ref<boolean>(false)
const sendingEmail = ref<boolean>(false)
const emailSent = ref<boolean>(false)
const sentToValues = ref<SentTo[]>([
  SentTo.ALL_DEPARTMENTS,
  SentTo.DEPARTMENTS_WITH_ATTENDEES,
  SentTo.DEPARTMENTS_WITHOUT_ATTENDEES
])
const sentTo = ref<SentTo | null>(null)
const sentToReadableInternal = ref<(sentTo: SentTo) => string>(sentToReadable)

const closeModal = () => {
  reminderDialogOpen.value = false
  registrationEndDialogOpen.value = false
  sentTo.value = null
  emailSent.value = false
}

const sendReminderEmail = () => {
  if (sentTo.value) {
    sendingEmail.value = true
    sendReminderMail({ sendTo: sentTo.value })
      .then(() => {
        emailSent.value = true
        sendingEmail.value = false
        toast.success('Erinnerungsemail versendet.')
      })
      .catch(() => {
        sendingEmail.value = false
        toast.success('Fehler beim versenden der Erinnerungsemail.')
      })
  }
}

const sendRegistrationEndEmail = () => {
  if (sentTo.value) {
    sendingEmail.value = true
    sendRegistrationFinishedMail({ sendTo: sentTo.value })
      .then(() => {
        emailSent.value = true
        sendingEmail.value = false
        sentTo.value = null
        toast.success('Registrierungsmail versendet.')
      })
      .catch(() => {
        sendingEmail.value = false
        toast.success('Fehler beim versenden der Registrierungsmail.')
      })
  }
}
</script>

<template>
  <v-card class="mb-16">
    <h2 class="pa-4 ml-md-6">Mails</h2>
    <v-col>
      <v-row justify="center">
        <v-col sm="12" md="8" lg="6" xl="6">
          <h3>Errinnerungsmail</h3>
          <MailReminderText />
          <v-row justify="end" class="mb-2">
            <v-dialog v-model="reminderDialogOpen" persistent max-width="500">
              <template v-slot:activator="{ props }">
                <v-btn rounded color="primary" dark v-bind="props" class="my-4">
                  Errinnerungsmail senden
                </v-btn>
              </template>

              <v-card class="mb-4">
                <form class="pa-4" v-on:submit.prevent="sendReminderEmail">
                  <v-card-title class="headline"> Errinnerungsmail versenden </v-card-title>
                  <v-card-text v-if="!emailSent">
                    An welche Gruppe soll die Mail verschickt werden?
                    <v-radio-group v-model="sentTo">
                      <v-radio
                        v-for="sendGroup in sentToValues"
                        :key="sendGroup"
                        :label="sentToReadableInternal(sendGroup)"
                        :value="sendGroup"
                        name="sendGroup"
                        required
                      ></v-radio>
                    </v-radio-group>
                  </v-card-text>
                  <v-card-text v-if="emailSent">
                    <v-icon medium>mdi-check</v-icon> Errinnerungsmail erfolgreich versendet
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn rounded @click="closeModal"> Schließen </v-btn>
                    <v-btn color="primary" v-if="!emailSent" :loading="sendingEmail" type="submit">
                      Senden
                    </v-btn>
                  </v-card-actions>
                </form>
              </v-card>
            </v-dialog>
          </v-row>
        </v-col>
      </v-row>
    </v-col>
    <v-divider />

    <v-col>
      <v-row justify="center" class="mt-8">
        <v-col sm="12" md="8" lg="6" xl="6">
          <h3>Registrierungsende Mail</h3>
          <MailRegistrationEndText />
          <v-row justify="end">
            <v-dialog v-model="registrationEndDialogOpen" persistent max-width="500">
              <template v-slot:activator="{ props }">
                <v-btn rounded color="primary" dark v-bind="props" class="my-4">
                  Registrierungsende Mail senden
                </v-btn>
              </template>
              <v-card class="mb-0">
                <form v-on:submit.prevent="sendRegistrationEndEmail">
                  <v-card-title class="headline"> Registrierungsende Mail versenden </v-card-title>
                  <v-card-text v-if="!emailSent">
                    An welche Gruppe soll die Mail verschickt werden?
                    <v-radio-group v-model="sentTo">
                      <v-radio
                        v-for="sendGroup in sentToValues"
                        :key="sendGroup"
                        :label="sentToReadableInternal(sendGroup)"
                        :value="sendGroup"
                        name="sendGroupRegistration"
                        required
                      ></v-radio>
                    </v-radio-group>
                  </v-card-text>
                  <v-card-text v-if="emailSent">
                    <v-icon medium>mdi-check</v-icon> Registrierungsende Mail erfolgreich versendet
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn rounded @click="closeModal"> Schließen </v-btn>
                    <v-btn color="primary" v-if="!emailSent" :loading="sendingEmail" type="submit">
                      Senden
                    </v-btn>
                  </v-card-actions>
                </form>
              </v-card>
            </v-dialog>
          </v-row>
        </v-col>
      </v-row>
    </v-col>
  </v-card>
</template>

<style scoped></style>
