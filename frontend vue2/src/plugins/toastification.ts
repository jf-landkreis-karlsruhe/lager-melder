import { createToastInterface } from "vue-toastification";

const pluginOptions = {
  timeout: 4000,
};

// Create the interface
export const toast = createToastInterface(pluginOptions);
