<template>
  <header>
    <div class="d-flex justify-center align-center hero-image-container">
      <img alt="Zeltlager logo" class="hero-image" src="../assets/logo2.jpg" />
    </div>
    <div id="nav" v-if="loggedIn">
      <router-link to="/">Home</router-link> |
      <router-link to="/login">Login</router-link> |
      <router-link to="/teilnehmer">Teilnehmer</router-link> |
      <router-link to="/pcr-tests">PCR Tests</router-link> |
      <router-link to="/files">Anmeldeunterlagen</router-link> |
      <router-link to="/feuerwehr">Meine Feuerwehr</router-link>
      <span v-if="hasAdministrationRole()">
        | <router-link to="/overview">Übersicht</router-link>
      </span>
      <span v-if="hasAdministrationRole()">
        | <router-link to="/einstellungen">Einstellungen</router-link>
      </span>
    </div>
  </header>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  getTokenData,
  AuthenticationChangedEvent,
  hasAdministrationRole,
} from "../services/authentication";

@Component({})
export default class Header extends Vue {
  timeoutId = 0;
  loggedIn = false;

  hasAdministrationRole = hasAdministrationRole;

  mounted() {
    window.addEventListener("focus", this.checkToken);
    this.checkToken();
    window.addEventListener(AuthenticationChangedEvent, this.checkToken);
  }

  beforeDestroy() {
    this.timeoutId && clearTimeout(this.timeoutId);
    window.removeEventListener("focus", this.checkToken);
    window.removeEventListener(AuthenticationChangedEvent, this.checkToken);
  }

  checkToken() {
    const token = getTokenData();
    if (!token) {
      this.loggedIn = false;
      this.$route.path !== "/login" && this.$router.push("/login");
      return;
    }
    this.loggedIn = true;
    const secondsUntilTokenExpires = token.exp - new Date().getTime() / 1000;
    this.timeoutId = setTimeout(
      () => this.checkToken,
      secondsUntilTokenExpires * 1000
    );
  }
}
</script>

<style scoped>
.hero-image-container {
  background: #2e6db2;
}
.hero-image {
  max-width: 100%;
  max-height: 350px;
}
header {
  margin-bottom: 30px;
}
</style>
