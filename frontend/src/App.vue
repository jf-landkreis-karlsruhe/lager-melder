<template>
  <div id="app">
    <div class="container">
      <Header />
      <main>
        <router-view />
      </main>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import Header from "./components/Header.vue";

@Component({
  components: { Header }
})
export default class App extends Vue {}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;

  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(auto, 1000px) minmax(0, 1fr);
  grid-gap: 14px;
}

.container {
  grid-column: 2;
  grid-row: 1;
}
</style>
