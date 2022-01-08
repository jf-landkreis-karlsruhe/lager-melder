<template>
  <section>
    <h2>Einstellungen</h2>
    <form>
      <!--v-text-field
        type="date"
        v-model="settingsss.registrationEnd"
        label="Ende Registration"
        required
        form="test"
      /-->
    </form>
  </section>
</template>

<script lang="ts">
import Vue from "vue";

// eslint-disable-next-line no-unused-vars
//import { getSettings, Settings } from "../../services/settings";

export default class EditSettings extends Vue {
  // settingsss: Settings = { id: "1", registrationEnd: "1212-05-21" } as Settings;
  /*
  mounted() {
    //getSettings().then((settings) => (this.settings = settings));
  }*/
}
</script>
