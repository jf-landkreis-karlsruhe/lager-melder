<template>
  <header>
    <img alt="Zeltlager logo" class="hero-image" src="../assets/logo.png" />
    <div id="nav">
      <router-link to="/">Home</router-link>|
      <router-link to="/login">Login</router-link>
    </div>
  </header>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import { getTokenData } from "../services/authentication";

@Component({})
export default class Header extends Vue {
  timeoutId = 0;

  mounted() {
    window.addEventListener("focus", this.checkToken);
    this.checkToken();
  }

  beforeDestroy() {
    this.timeoutId && clearTimeout(this.timeoutId);
    window.removeEventListener("focus", this.checkToken);
  }

  checkToken() {
    const token = getTokenData();
    if (!token) {
      this.$route.path !== "/login" && this.$router.push("/login");
      return;
    }
    const secondsUntilTokenExpires = token.exp - new Date().getTime() / 1000;
    this.timeoutId = setTimeout(
      () => this.checkToken,
      secondsUntilTokenExpires * 1000
    );
  }
}
</script>

<style scoped>
.hero-image {
  width: 100%;
}
header {
  margin-bottom: 30px;
}
</style>
