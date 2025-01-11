<script setup lang="ts">
import { RouterView } from 'vue-router'
import Footer from './components/LmFooter.vue'
import Header from './components/LmHeader.vue'
</script>

<template>
  <v-app>
    <div class="grid-container">
      <div class="my-container">
        <Header class="header" />
        <main class="content">
          <RouterView />
        </main>
        <Footer class="footer" />
      </div>
    </div>
  </v-app>
</template>

<style lang="scss">
.v-application__wrap {
  height: 100dvh;
}
</style>

<style scoped lang="scss">
.grid-container {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;
  overflow: hidden;
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
