<script setup lang="ts">
</script>

<template>
  <footer class="px-16 py-8 mt-12 d-flex justify-space-between align-center">
    <v-container>
      <a
        href="https://jf-landkreis-karlsruhe.de/?page_id=52"
        target="_blank"
        rel="noopener noreferrer"
        class="imprint"
      >
        Impressum
      </a>
      <div>
        © {{ new Date().getFullYear() }} Jugendfeuerwehr Landkreis Karlsruhe.
        All rights reserved
      </div>
    </v-container>
  </footer>
</template>

<style scoped lang="scss">
footer {
  background: #f3f3f3;

  .imprint {
    text-decoration: none;
    font-weight: bold;
  }
}
</style>
