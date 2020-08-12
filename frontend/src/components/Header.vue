<template>
  <header>
    <img alt="Zeltlager logo" class="hero-image" src="../assets/logo.png" />
    <div id="nav">
      <router-link to="/">Home</router-link>|
      <router-link to="/login">Login</router-link>
    </div>
  </header>
</template>

<script>
export default {
  name: "Header"
};
</script>

<style scoped>
.hero-image {
  width: 100%;
}
header {
  margin-bottom: 30px;
}
</style>
