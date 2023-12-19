<template>
  <Login />
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import Login from "../components/Login.vue";

@Component({
  components: { Login },
})
export default class LoginView extends Vue {}
</script>
