<template>
  <div>
    <h1>☄️ Scanner</h1>

    <div
      id="scanner"
      style="width: 640px; height: 480px; margin-bottom: 30px"
    ></div>

    <form v-on:submit.prevent="() => {}">
      <v-text-field label="Dein gescannter Teilnahmecode" :value="attendeeCode" />

      <v-alert v-if="scannerError.length > 0" type="error">
        {{ scannerError }}
      </v-alert>

      <v-btn @click="toggleScanning" color="primary" type="button">
        <span v-if="isScanning"><v-icon medium>mdi-stop</v-icon> Stop</span>
        <span v-else><v-icon medium>mdi-play</v-icon> Start</span>
      </v-btn>
    </form>
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
  isScanning: boolean = true;
  scannerError: string = "";
  previousAttandeeCode: string = "";

  protected toggleScanning() {
    if (this.isScanning) {
      Quagga.stop();
    } else {
      this.initQuagga();
    }
    this.isScanning = !this.isScanning;
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
          target: document.querySelector("#scanner") // Or '#yourElement' (optional)
        },
        decoder: {
          readers: ["code_93_reader"],
          // debug: {
          //   drawBoundingBox: true,
          //   showFrequency: true,
          //   drawScanline: true,
          //   showPattern: true,
          // },
        }
      },
      err => {
        if (err) {
          console.log(err);
          this.scannerError = err;
          return;
        }
        console.log("Initialization finished. Ready to start");
        Quagga.start();
        Quagga.onDetected(data => {
          console.warn("HERE IS YOUR CODE:", data.codeResult.code, data);
          this.attendeeCode = data.codeResult.code;
          if(this.previousAttandeeCode !== this.attendeeCode) {
            this.previousAttandeeCode = this.attendeeCode;
            loginToEvent(this.eventCode, this.attendeeCode);
          }
        });
      }
    );
  }
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>
