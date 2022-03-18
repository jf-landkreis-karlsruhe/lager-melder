<template>
  <v-textarea disabled :value="value" auto-grow></v-textarea>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

@Component({ name: "MailRegistrationEndText" })
export default class MailRegistrationEndText extends Vue {
  value: string = `
Onlineanmeldung zum Kreiszeltlager beendet

Hallo {leaderName},
die Onlineanmeldung zum Kreiszeltlager der Jugendfeuerwehr Landkreis Karlsruhe in {hostCity} ist nun abgeschlossen. Falls du eine Liste mit deinen angemeldeten Betreuern und Teilnehmern benötigst, logge dich bitte ein und drucke die Seite mit den Anmeldungen aus.

Kameradschaftliche Grüße,
euer Fachgebietsleiter Lager- und Fahrten
  `;
}
</script>

<style scoped>
</style>
