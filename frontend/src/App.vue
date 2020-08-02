<template>
  <div id="app">
    <div class="wrapper">
  <div class="one">One</div>
  <div class="two">Two</div>
  <div class="three">Two</div>
  </div>
    <Header />
    <HelloWorld msg="Welcome to Your Vue.js App" />
  </div>
</template>

<script>
import HelloWorld from "./components/HelloWorld.vue";
import Header from "./components/Header.vue";

export default {
  name: "App",
  components: {
    HelloWorld,
    Header
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

.wrapper {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(auto, 1000px) minmax(0, 1fr);
  grid-gap: 14px;
}

.one {
  grid-column: 1;
  grid-row: 1;
  background-color: red;
}
.two {
  grid-column: 2;
  grid-row: 1;
  background-color: yellowgreen;
}
.three {
  grid-column: 3;
  grid-row: 1;
  background-color: hotpink;
}
</style>
