<script setup lang="ts">
const value: string = `
Erinnerung an die Kreiszeltlageranmeldung

Hallo {leaderName},
die Onlineanmeldung zum Kreiszeltlager der Jugendfeuerwehr Landkreis Karlsruhe in {hostCity} ist noch {daysLeft} Tage offen, daher jetzt schnell anmelden.

Der Login ist über die URL: https://anmeldung.jugendfeuerwehrlager.de möglich.
Die eingegeben Teilnehmerdaten sind bis zum Anmeldeschluss am {registrationDeadline} um 23:00 Uhr editier- und löschbar.

Anhand der dann angemeldeten Teilnehmer werden auch die T-Shirts bestellt.
Sollte es im Nachhinein noch zu Änderungen kommen, diese bitte an fgl.zeltlager@jf-landkreis-karlsruhe.de schicken.

Kameradschaftliche Grüße,
euer Fachgebietsleiter Lager- und Fahrten
  `
</script>

<template>
  <v-textarea disabled :value="value" auto-grow></v-textarea>
</template>

<style scoped></style>
