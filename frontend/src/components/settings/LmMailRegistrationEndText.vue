<script setup lang="ts">
const value: string = `
Onlineanmeldung zum Kreiszeltlager beendet

Hallo {leaderName},
die Onlineanmeldung zum Kreiszeltlager der Jugendfeuerwehr Landkreis Karlsruhe in {hostCity} ist nun abgeschlossen. Falls du eine Liste mit deinen angemeldeten Betreuern und Teilnehmern benötigst, logge dich bitte ein und drucke die Seite mit den Anmeldungen aus.

Kameradschaftliche Grüße,
euer Fachgebietsleiter Lager- und Fahrten
`
</script>

<template>
  <v-textarea disabled :value="value" auto-grow></v-textarea>
</template>

<style scoped></style>
