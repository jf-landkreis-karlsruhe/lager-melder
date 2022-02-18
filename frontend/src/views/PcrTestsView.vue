<template>
  <div>
    <v-container class="pcr-tests-root">
      <h1>Scanne einen Code für einen PCR Test</h1>
      <v-row justify="center">
        <v-col align="center" class="my-8">
          <div class="explanation-image">
            <img
              src="https://www.swr.de/swraktuell/baden-wuerttemberg/1635481852699,pcr-test-symbolbild-100~_v-16x9@2dL_-6c42aff4e68b43c7868c3240d3ebfa29867457da.jpg"
              width="45%"
              height="auto"
            />
            <p>Scanne hier den Code des Pool Röhrchens ab.</p>
          </div>
          <Scanner
            class="scanner"
            manualCodeHint="Mindestens X Zeichen"
            @submitCode="goToPcrTestPool"
          />
        </v-col>
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
