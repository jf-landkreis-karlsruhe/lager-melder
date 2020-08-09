<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <form @submit.prevent="loginHandler" v-if="!loggedIn">
      <label for="username">Benutzername</label>
      <input type="text" v-model="username" id="username" />

      <label for="password">Password</label>
      <input type="password" v-model="password" id="password" />

      <button type="submit">Einloggen</button>
    </form>

    <div v-if="loggedIn">You are now logged in</div>
  </div>
</template>

<script lang="ts">
import { login, isLoggedIn } from "../services/authentication.js";

export default {
  name: "HelloWorld",
  props: {
    msg: String
  },
  data: function() {
    return {
      username: "admin",
      password: "password",
      loggedIn: false
    };
  },
  methods: {
    loginHandler: function() {
      console.log("username", this.username);
      console.log("password", this.password);
      login(this.username, this.password)
      .then(console.log)
      .then(() => this.loggedIn = true)
    }
  },
  mounted: function() {
    this.loggedIn = isLoggedIn()
    console.log(this.loggedIn)
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
