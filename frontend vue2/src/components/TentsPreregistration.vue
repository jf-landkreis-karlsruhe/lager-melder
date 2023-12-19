<template>
  <v-card>
    <v-card-title>
      <h3>Voranmeldung Zelte</h3>
    </v-card-title>
    <v-card-text>
      <p v-if="!short">
        Bitte gebt hier an welche Zelten ihr plant aufzustellen. Sollte euer
        exaktes Zelt nicht vorhanden sein, wählt das Zelt mit ähnlichen Abmaßen
        aus. Die Anzahl und Größen der Zelte soll uns helfen die größe des
        Zeltplatzes planen zu können.
      </p>

      <form v-if="tents !== {}" v-on:submit.prevent="saveTents()">
        <p>Die Größen der Zelte sind angegeben in Länge x Breite.</p>
        <v-text-field
          type="number"
          v-model="tents.sg200"
          label="SG 200 (4m x 5,9m)"
          min="0"
          required
          :error-messages="
            tents.sg200 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''
          "
        />
        <v-text-field
          type="number"
          v-model="tents.sg20"
          label="SG 20 (5m x 4,74m)"
          min="0"
          required
          :error-messages="
            tents.sg20 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''
          "
        />
        <v-text-field
          type="number"
          v-model="tents.sg30"
          label="SG 30 (6m x 5,64m)"
          min="0"
          required
          :error-messages="
            tents.sg30 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''
          "
        />
        <v-text-field
          type="number"
          v-model="tents.sg40"
          label="SG 40 (8m x 5,64m)"
          min="0"
          required
          :error-messages="
            tents.sg40 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''
          "
        />
        <v-text-field
          type="number"
          v-model="tents.sg50"
          label="SG 50 (10m x 5,64m)"
          min="0"
          required
          :error-messages="
            tents.sg50 < 0 ? 'Anzahl darf nicht kleiner als 0 sein.' : ''
          "
        />
        <v-row justify="end" class="my-4">
          <v-btn rounded color="primary" :loading="saving" type="submit">
            Zelte speichern
          </v-btn>
        </v-row>
      </form>
      <p v-if="!short">
        Weitere Informationen gibt es auch auf der Herstellerseite von
        <a
          href="https://www.lanco.eu/produkte/zelte/zelte-mit-40-mm-aluminiumgerust/sanitats-und-aufenthaltszelte"
          target="_blank"
          rel="noopener noreferrer"
          >Lanco</a
        >.
      </p>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { getTentsForDepartment, Tents, updateTents } from "@/services/tents";
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

@Component({
  components: {},
})
export default class TentsPreregistration extends Vue {
  @Prop() departmentId!: number;
  @Prop() short: boolean | undefined;

  tents: Tents = {} as Tents;
  saving: boolean = false;

  saveTents() {
    this.saving = true;
    updateTents(this.tents)
      .then((tents) => {
        this.tents = tents;
        this.saving = false;
        this.$toast.success("Zelte wurden gespeichert.");
      })
      .catch(() => {
        this.saving = false;
        this.$toast.error("Fehler beim speichern der Zelte.");
      });
  }

  mounted() {
    getTentsForDepartment(this.departmentId).then((tents) => {
      this.tents = tents;
    });
  }
}
</script>

<style scoped></style>
