<template>
  <footer>
    <div class="d-flex justify-center align-center">
      <a
        href="https://jf-landkreis-karlsruhe.de/?page_id=52"
        target="_blank"
        rel="noopener noreferrer"
        >Impressum</a
      >
    </div>
  </footer>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

@Component({})
export default class Footer extends Vue {}
</script>

<style scoped>
footer {
  margin-top: 30px;
  margin-bottom: 30px;
}
</style>
