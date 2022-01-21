<template>
  <div>
    <v-container class="pcr-tests-root">
      <h1>Scanne einen Code für einen PCR Test</h1>
      <v-row justify="center">
        <Scanner
          class="scanner"
          manualCodeHint="Mindestens X Zeichen"
          @submitCode="goToPcrTestPool"
        />
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Vue, Component } from "vue-property-decorator";
import Scanner from "../components/Scanner.vue";

@Component({ name: "PcrTestsView", components: { Scanner } })
export default class PcrTestsView extends Vue {
  protected goToPcrTestPool(poolId: string) {
    // TODO: verify if valid poolId
    if (window) {
      window.location.href = window.location.href + `/${poolId}`;
    }
  }
}
</script>

<style scoped lang="scss">
.pcr-tests-root {
  .scanner {
    margin-bottom: 2rem;
  }
}
</style>
