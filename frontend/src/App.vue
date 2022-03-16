<template>
  <v-app>
    <div id="grid-container">
      <div class="my-container">
        <Header />
        <main class="content">
          <router-view />
        </main>
        <Footer />
      </div>
    </div>
  </v-app>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import Header from "./components/Header.vue";
import Footer from "./components/Footer.vue";

@Component({
  components: { Header, Footer },
})
export default class App extends Vue {}
</script>

<style scoped lang="scss">
#grid-container {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(auto, 1400px) minmax(0, 1fr);
}

.my-container {
  grid-column: 2;
  grid-row: 1;

  .content {
    padding: 0;
    @media screen and (min-width: 768px) {
      padding: 0 14px;
    }
  }
}
</style>
