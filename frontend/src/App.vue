<template>
  <v-app>
    <div id="grid-container">
      <div class="my-container">
        <Header />
        <main>
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

<style scoped>
#grid-container {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(auto, 1000px) minmax(0, 1fr);
  grid-gap: 14px;
}

.my-container {
  grid-column: 2;
  grid-row: 1;
}
</style>
