<template>
  <div>
    <v-container>
      <v-row justify="center">
        <v-col cols="4">
          <v-card v-if="!loggedIn">
            <v-card-title>Login</v-card-title>
            <form @submit.prevent="loginHandler">
              <v-card-text>
                <v-text-field
                  prepend-icon="mdi-account"
                  v-model="username"
                  label="Benutzername"
                />
                <v-text-field
                  type="password"
                  prepend-icon="mdi-lock"
                  v-model="password"
                  label="Passwort"
                />
              </v-card-text>
              <v-card-actions>
                <v-container>
                  <v-row justify="end">
                    <v-btn color="primary" type="submit" rounded
                      >Einloggen</v-btn
                    >
                  </v-row>
                </v-container>
              </v-card-actions>
            </form>
          </v-card>

          <v-card v-if="loggedIn">
            <v-card-title>Eingeloggt</v-card-title>
            <v-card-text class="d-flex flex-column align-center justify-center">
              Du bist eingeloggt.
              <br />
              <br />
              <v-btn rounded color="error" @click="logout"> Ausloggen </v-btn>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import { login, isLoggedIn, logout } from "../services/authentication";

@Component({})
export default class Login extends Vue {
  username = "";
  password = "";
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
