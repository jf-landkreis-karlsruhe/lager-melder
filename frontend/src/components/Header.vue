<template>
  <header>
    <div class="d-flex justify-center align-center hero-image-container">
      <router-link to="/">
        <img
          alt="Zeltlager logo"
          class="hero-image"
          src="../assets/logo2.jpg"
        />
      </router-link>
    </div>
    <nav id="nav" v-if="loggedIn">
      <v-container fluid class="nav-bar">
        <v-row justify="space-between align-center">
          <ul class="pa-0 nav-list">
            <li class="nav-item">
              <router-link to="/teilnehmer">Teilnehmer</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/pcr-tests">PCR Tests</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/files">Anmeldeunterlagen</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/feuerwehr">Meine Feuerwehr</router-link>
            </li>
            <li class="nav-item admin" v-if="hasAdministrationRole()">
              <router-link to="/overview"> Übersicht </router-link>
            </li>
            <li class="nav-item admin" v-if="hasAdministrationRole()">
              <router-link to="/einstellungen"> Einstellungen </router-link>
            </li>
          </ul>

          <router-link to="/login" class="account">
            <v-icon medium color="blue darken-2"> mdi-account </v-icon>
            <span class="account__link pl-1">Account</span>
          </router-link>
        </v-row>
      </v-container>
    </nav>
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

<style scoped lang="scss">
.hero-image-container {
  background: #006fb7;
}
.hero-image {
  max-width: 100%;
  max-height: 200px;
  margin-top: -30px;
}
header {
  margin-bottom: 30px;
}
.nav-bar {
  margin-top: 12px;
  font-weight: 500;
  padding: 10px 24px;

  .nav-list {
    .nav-item {
      display: inline-block;
      background: #d3e9f8;
      border-radius: 12px;
      margin: 0 4px;
      transition: padding 0.2s ease-in-out;

      &:hover {
        background: #95caee;
        padding: 0px 8px;
        border-radius: 16px;
      }

      &.admin {
        background: #ffe760;

        &:hover {
          background: #ecd032;
        }
      }
      a {
        text-decoration: none;
        color: #303030;
        padding: 4px 8px;
        border-radius: 16px;
      }
    }
  }
  .account {
    display: flex;
    align-items: center;
    text-decoration: none;
    transition: all 0.2 ease-in-out;

    &:hover {
      color: #0e569c;
      font-weight: bold;
    }

    .account__link {
      text-decoration: none;
    }
  }
}
</style>
