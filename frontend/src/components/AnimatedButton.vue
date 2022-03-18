<template>
  <button class="draw meet">
    <slot></slot>
  </button>
</template>

<script>
// src: https://codepen.io/chancesq/pen/qBOyQoW
import { Vue, Component } from "vue-property-decorator";

@Component({ name: "animted-button" })
export default class AnimatedButton extends Vue {}
</script>

<style scoped lang="scss">
// Basic styles
button {
  --border-radius: 12px;

  background: none;
  border: 0;
  border-radius: var(--border-radius);
  box-sizing: border-box;
  padding: 0.25rem 0.5rem;

  // Using inset box-shadow instead of border for sizing simplicity
  box-shadow: inset 0 0 0 2px #006fb7;
  color: #006fb7;
  font-size: inherit;
  font-weight: 700;

  // Required, since we're setting absolute on pseudo-elements
  position: relative;
  vertical-align: middle;

  &::before,
  &::after {
    box-sizing: inherit;
    content: "";
    position: absolute;
    width: 100%;
    height: 100%;
  }
}

.draw {
  transition: color 0.25s;

  &::before,
  &::after {
    // Set border to invisible, so we don't see a 4px border on a 0x0 element before the transition starts
    border: 2px solid transparent;
    border-radius: var(--border-radius);
    width: 0;
    height: 0;
  }

  // This covers the top & right borders (expands right, then down)
  &::before {
    top: 0;
    left: 0;
  }

  // And this the bottom & left borders (expands left, then up)
  &::after {
    bottom: 0;
    right: 0;
  }

  &:hover {
    color: #006fb7;
  }

  // Hover styles
  &:hover::before,
  &:hover::after {
    width: 100%;
    height: 100%;
  }

  &:hover::before {
    border-top-color: #006fb7; // Make borders visible
    border-right-color: #006fb7;
    transition: width 0.25s ease-out,
      // Width expands first
      height 0.25s ease-out 0.25s; // And then height
  }

  &:hover::after {
    border-radius: var(--border-radius);
    border-bottom-color: #006fb7; // Make borders visible
    border-left-color: #006fb7;
    transition: border-color 0s ease-out 0.5s,
      // Wait for ::before to finish before showing border
      width 0.25s ease-out 0.5s,
      // And then exanding width
      height 0.25s ease-out 0.75s; // And finally height
  }
}

// Inherits from .draw
.meet {
  &:hover {
    color: #cb2219;
  }

  // Start ::after in same position as ::before
  &::after {
    top: 0;
    left: 0;
  }

  // Change colors
  &:hover::before {
    border-radius: var(--border-radius);
    border-top-color: #cb2219;
    border-right-color: #cb2219;
  }

  &:hover::after {
    border-radius: var(--border-radius);
    border-bottom-color: #cb2219;
    border-left-color: #cb2219;
    transition: // Animate height first, then width
      height 0.25s ease-out, width 0.25s ease-out 0.25s;
  }
}
</style>
