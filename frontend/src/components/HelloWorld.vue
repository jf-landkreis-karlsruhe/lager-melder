<template>
  <div class="hello">
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
import Vue from "vue";
import { login, isLoggedIn } from "../services/authentication";

export default Vue.extend({
  name: "HelloWorld",
  data: function() {
    return {
      username: "admin",
      password: "password",
      loggedIn: false
    };
  },
  methods: {
    loginHandler: function() {
      login(this.username, this.password).then(() => (this.loggedIn = true));
    }
  },
  mounted: function() {
    this.loggedIn = isLoggedIn();
  }
});
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
