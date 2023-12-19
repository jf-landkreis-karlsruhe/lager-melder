<template>
  <div>
    <v-container class="pcr-tests-root">
      <v-row justify="center">
        <v-col align="center" class="mb-8">
          <h1 class="header">PCR Pool Test starten</h1>
          <div class="explanation-image">
            <img
              src="https://images.unsplash.com/photo-1582719366950-f23838e207e7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"
              alt="PCR Test Sammelröhrchen"
              title="PCR Test Sammelröhrchen"
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
    if (window) {
      window.location.href = window.location.href + `/${poolId}`;
    }
  }
}
</script>

<style scoped lang="scss">
.pcr-tests-root {
  .header {
    text-align: left;
  }
  .scanner {
    margin-bottom: 2rem;
  }
}
</style>
