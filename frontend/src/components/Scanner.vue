<template>
  <div>
    <v-container>
      <h1>☄️ Scanner</h1>
      <v-row justify="center">
        <div
          id="scanner"
          style="width: 640px; height: 480px; margin-bottom: 30px"
        ></div>
      </v-row>

      <v-row justify="center">
        <form v-on:submit.prevent="() => {}">
          <p v-if="!attendeeCode">...Scanning</p>

          <v-alert
            class="attandee-code-alert"
            :value="!!attendeeCode"
            type="success"
            transition="slide-y-transition"
          >
            {{ attendeeCode }}
          </v-alert>

          <div class="errors">
            <v-alert
              class="network-error"
              :value="!!networkError"
              type="error"
              transition="slide-y-transition"
            >
              {{ networkError }}
            </v-alert>
            <v-alert
              class="scanner-error"
              v-if="scannerError"
              type="error"
              transition="slide-y-transition"
            >
              {{ scannerError }}
            </v-alert>
          </div>

          <v-btn @click="toggleScanning" color="primary" type="button">
            <span v-if="isScanning"><v-icon medium>mdi-stop</v-icon> Stop</span>
            <span v-else><v-icon medium>mdi-play</v-icon> Start</span>
          </v-btn>
        </form>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import Quagga from "quagga"; // ES6
import { loginToEvent } from "../services/event";

@Component({ name: "ScannerComponent" })
export default class ListDepartment extends Vue {
  eventCode: string = "";

  attendeeCode: string = "";
  previousAttandeeCode: string = "";

  isScanning: boolean = true;
  scannerError: string = "";
  networkError: string = "";

  protected toggleScanning() {
    if (this.isScanning) {
      Quagga.stop();
    } else {
      this.initQuagga();
    }
    this.isScanning = !this.isScanning;
  }

  protected async codeDetected(data) {
    this.attendeeCode = data.codeResult.code;
    if (this.previousAttandeeCode !== this.attendeeCode) {
      this.previousAttandeeCode = this.attendeeCode;
      await loginToEvent(this.eventCode, this.attendeeCode).catch((reason) => {
        this.networkError = JSON.stringify(reason);
      });
    }
  }

  mounted() {
    this.eventCode = this.$route.params.eventCode;
    this.initQuagga();
  }

  initQuagga() {
    Quagga.init(
      {
        debug: true,
        inputStream: {
          name: "Live",
          type: "LiveStream",
          target: document.querySelector("#scanner"), // Or '#yourElement' (optional)
        },
        decoder: {
          readers: ["code_93_reader"],
          // debug: {
          //   drawBoundingBox: true,
          //   showFrequency: true,
          //   drawScanline: true,
          //   showPattern: true,
          // },
        },
      },
      (err) => {
        if (err) {
          console.log(err);
          this.scannerError = err;
          return;
        }
        console.log("Initialization finished. Ready to start");
        Quagga.start();
        Quagga.onDetected(this.codeDetected);
      }
    );
  }
}
</script>

<style scoped>
.attandee-code-alert,
.network-error,
.scanner-error {
  max-width: 100%;
  width: 300px;
}
</style>
