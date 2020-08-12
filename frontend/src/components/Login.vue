<template>
  <div class="hello">
    <form @submit.prevent="loginHandler" v-if="!loggedIn">
      <label for="username">Benutzername</label>
      <input type="text" v-model="username" id="username" />

      <label for="password">Password</label>
      <input type="password" v-model="password" id="password" />

      <button type="submit">Einloggen</button>
    </form>

    <button @click="logout">Logout</button>
    <div v-if="loggedIn">You are now logged in</div>
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

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
</style>
