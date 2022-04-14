<template>
  <v-app>
    <div class="grid-container">
      <div class="my-container">
        <Header class="header" />
        <main class="content">
          <router-view />
        </main>
        <Footer class="footer" />
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
.grid-container {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;
}

.my-container {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  height: 100%;

  .header {
    flex: 0 1 auto;
  }

  .content {
    flex: 1 1 auto;
    padding: 0;
    @media screen and (min-width: 768px) {
      padding: 0 14px;
    }
  }

  .footer {
    flex: 0 1 auto;
  }
}
</style>
