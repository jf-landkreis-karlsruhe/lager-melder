<template>
  <v-card class="mb-16">
    <h2 class="ml-12">Mails</h2>
    <v-col>
      <v-row justify="center">
        <v-col sm="12" md="12" lg="10" xl="8">
          <h3>Errinnerungsmail</h3>
          <MailReminderText />
          <v-row justify="end">
            <v-dialog v-model="reminderDialogOpen" persistent max-width="500">
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  rounded
                  color="primary"
                  dark
                  v-bind="attrs"
                  v-on="on"
                  class="mb-2"
                >
                  Errinnerungsmail senden
                </v-btn>
              </template>
              <v-card class="mb-0">
                <form v-on:submit.prevent="sendReminderEmail">
                  <v-card-title class="headline">
                    Errinnerungsmail versenden
                  </v-card-title>
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
                    <v-icon medium>mdi-check</v-icon> Errinnerungsmail
                    erfolgreich versendet
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn rounded text @click="closeModal"> Schließen </v-btn>
                    <v-btn
                      color="primary"
                      v-if="!emailSent"
                      :loading="sendingEmail"
                      type="submit"
                    >
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
      <v-row justify="center">
        <v-col sm="12" md="12" lg="10" xl="8">
          <h3>Registrierungsende Mail</h3>
          <MailRegistrationEndText />
          <v-row justify="end">
            <v-dialog
              v-model="registrationEndDialogOpen"
              persistent
              max-width="500"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  rounded
                  color="primary"
                  dark
                  v-bind="attrs"
                  v-on="on"
                  class="mb-2"
                >
                  Registrierungsende Mail senden
                </v-btn>
              </template>
              <v-card class="mb-0">
                <form v-on:submit.prevent="sendRegistrationEndEmail">
                  <v-card-title class="headline">
                    Registrierungsende Mail versenden
                  </v-card-title>
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
                    <v-icon medium>mdi-check</v-icon> Registrierungsende Mail
                    erfolgreich versendet
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn rounded text @click="closeModal"> Schließen </v-btn>
                    <v-btn
                      color="primary"
                      v-if="!emailSent"
                      :loading="sendingEmail"
                      type="submit"
                    >
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

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  sendReminderMail,
  sendRegistrationFinishedMail,
  SentTo,
  sentToReadable,
} from "../../services/mails";
import MailReminderText from "./MailReminderText.vue";
import MailRegistrationEndText from "./MailRegistrationEndText.vue";

@Component({
  name: "MailSettings",
  components: { MailReminderText, MailRegistrationEndText },
})
export default class MailSettings extends Vue {
  reminderDialogOpen: boolean = false;
  registrationEndDialogOpen: boolean = false;
  sendingEmail: boolean = false;
  emailSent: boolean = false;
  sentToValues: SentTo[] = [
    SentTo.ALL_DEPARTMENTS,
    SentTo.DEPARTMENTS_WITH_ATTENDEES,
    SentTo.DEPARTMENTS_WITHOUT_ATTENDEES,
  ];
  sentTo: SentTo | null = null;
  sentToReadableInternal = sentToReadable;

  closeModal() {
    this.reminderDialogOpen = false;
    this.registrationEndDialogOpen = false;
    this.sentTo = null;
    this.emailSent = false;
  }

  sendReminderEmail() {
    if (this.sentTo) {
      this.sendingEmail = true;
      sendReminderMail({ sendTo: this.sentTo })
        .then(() => {
          this.emailSent = true;
          this.sendingEmail = false;
        })
        .catch(() => {
          this.sendingEmail = false;
        });
    }
  }

  sendRegistrationEndEmail() {
    if (this.sentTo) {
      this.sendingEmail = true;
      sendRegistrationFinishedMail({ sendTo: this.sentTo })
        .then(() => {
          this.emailSent = true;
          this.sendingEmail = false;
          this.sentTo = null;
        })
        .catch(() => {
          this.sendingEmail = false;
        });
    }
  }
}
</script>

<style scoped></style>
