<template>
  <header>
    <div class="d-flex justify-center align-center hero-image-container">
      <router-link :to="isLoggedIn ? '/teilnehmer' : ''">
        <img
          alt="Zeltlager logo"
          class="hero-image"
          src="../assets/logo2.jpg"
        />
      </router-link>
    </div>
    <v-container>
      <nav id="nav" v-if="loggedIn">
        <v-container fluid class="nav-bar">
          <v-row justify="space-between" align="center" class="nav-bar__row">
            <ul class="pa-0 nav-bar__list">
              <li class="nav-item">
                <router-link to="/teilnehmer">Teilnehmer</router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole">
                <router-link to="/feuerwehr">Feuerwehren</router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole">
                <router-link to="/overview"> Übersicht </router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole">
                <router-link to="/einstellungen"> Einstellungen </router-link>
              </li>
            </ul>

            <router-link to="/account" class="account">
              <v-icon medium color="blue darken-2"> mdi-account </v-icon>
              <span class="account__link pl-1">Mein Profil</span>
            </router-link>
          </v-row>
        </v-container>
      </nav>
    </v-container>
  </header>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  getTokenData,
  AuthenticationChangedEvent,
  hasAdministrationRole,
  isLoggedIn,
} from "../services/authentication";

@Component({})
export default class Header extends Vue {
  timeoutId = 0;
  loggedIn = false;

  private get isLoggedIn(): boolean {
    return isLoggedIn();
  }

  private get hasAdministrationRole(): boolean {
    return hasAdministrationRole();
  }

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
header {
  margin-bottom: 30px;

  .hero-image-container {
    background: #006fb7;

    .hero-image {
      max-width: 100%;
      max-height: 200px;
      margin-top: -30px;
    }
  }

  .nav-bar {
    margin-top: 12px;
    font-weight: 500;

    .nav-bar__row {
      gap: 12px;

      .nav-bar__list {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 12px;

        .nav-item {
          display: inline-block;

          a {
            text-decoration: none;
            color: #303030;
            border-radius: 16px;
            border: 2px solid #d3e9f8;
            transition: padding 0.2s ease-in-out;
            padding: 0 0.5rem;
            background: linear-gradient(to left, #d3e9f8 50%, #95caee 50%) right;
            background-size: 200%;
            transition: background 0.25s ease-in-out;

            &:hover,
            &:active {
              border: 2px solid #95caee;
              background-position: left;
            }
            &.router-link-active {
              background: linear-gradient(to left, #95caee 50%, #95caee 50%)
                right;
            }
          }

          &.admin {
            a {
              border: 2px solid #ffe760;
              background: linear-gradient(to left, #ffe760 50%, #ecd032 50%)
                right;
              background-size: 200%;

              &:hover {
                border: 2px solid #ecd032;
                background-position: left;
              }

              &.router-link-active {
                background: linear-gradient(to left, #ecd032 50%, #ecd032 50%);
              }
            }
          }
        }
      }
      .account {
        display: flex;
        align-items: center;
        text-decoration: none;
        transition: all 0.2s ease-in-out;
        margin-left: auto;

        @media screen and (min-width: 768px) {
          margin: unset;
        }

        &:hover,
        &:active,
        &.router-link-active {
          color: #1976d2;
          box-shadow: 0 3px 0 #1976d2;
        }

        .account__link {
          text-decoration: none;
        }
      }
    }
  }
}
</style>
