<template>
  <div>
    <v-card v-if="!loggedIn">
      <v-card-title>Login</v-card-title>
      <form @submit.prevent="loginHandler">
        <v-card-text>
          <v-text-field prepend-icon="mdi-account" v-model="username" label="Benutzername" />
          <v-text-field
            type="password"
            prepend-icon="mdi-lock"
            v-model="password"
            label="Passwort"
          />
        </v-card-text>
        <v-card-actions>
          <v-container fluid>
            <v-row justify="end">
              <v-btn color="primary" type="submit">Einloggen</v-btn>
            </v-row>
          </v-container>
        </v-card-actions>
      </form>
    </v-card>

    <v-card v-if="!!loggedIn">
      <v-card-title>Eingeloggt</v-card-title>
      <v-card-text>
        Du bist eingeloggt. Du kannst dich jetzt
        <v-btn text @click="logout">ausloggen</v-btn>
      </v-card-text>
    </v-card>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import { login, isLoggedIn, logout } from "../services/authentication";

@Component({})
export default class Login extends Vue {
  username = "admin";
  password = "password";
  loggedIn = false;

  loginHandler() {
    login(this.username, this.password)
      .then(() => (this.loggedIn = true))
      .then(() => this.$router.push("/"));
  }
  logout() {
    logout();
    this.loggedIn = false;
  }

  mounted() {
    this.loggedIn = isLoggedIn();
  }
}
</script>
