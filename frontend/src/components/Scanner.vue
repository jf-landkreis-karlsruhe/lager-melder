<template>
  <div>
    <v-container>
      <h1>Event: {{ eventName || "Anstehendes Event" }}</h1>
      <v-row justify="center">
        <div
          id="scanner"
          v-show="isScanning"
          style="width: 640px; height: 480px; margin-bottom: 30px"
        ></div>
      </v-row>

      <v-row justify="center">
        <form v-on:submit.prevent="() => {}">
          <p v-if="isScanning">...Scanning</p>

          <transition name="slide-fade" mode="out-in">
            <v-alert
              :key="attendeeCode"
              class="attandee-code-alert"
              :value="!!attendeeCode"
              type="success"
            >
              {{ attendeeCode }}
            </v-alert>
          </transition>

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
import { getEventByCode, loginToEvent } from "../services/event";

@Component({ name: "ScannerComponent" })
export default class ScannerComponent extends Vue {
  eventCode: string = "";
  eventName: string = "";

  attendeeCode: string = "";
  previousAttandeeCode: string = "";

  isScanning: boolean = true;
  scannerError: string = "";
  networkError: string = "";

  protected toggleScanning() {
    if (this.isScanning) {
      this.stopQuagga();
    } else {
      this.initQuagga();
    }
    this.isScanning = !this.isScanning;
  }

  protected async codeDetected(data: Quagga.Code) {
    this.attendeeCode = data.codeResult.code;
    if (this.previousAttandeeCode !== this.attendeeCode) {
      this.previousAttandeeCode = this.attendeeCode;
      await loginToEvent(this.eventCode, this.attendeeCode).catch((reason) => {
        this.networkError = JSON.stringify(reason);
      });
    }
  }

  async mounted() {
    this.eventCode = this.$route.params.eventCode;
    getEventByCode(this.eventCode).then(name => {
      this.eventName = name;
    });
    this.initQuagga();
  }

  stopQuagga() {
    Quagga.stop();
    this.attendeeCode = "";
    this.networkError = "";
    this.scannerError = "";
  }

  initQuagga() {
    Quagga.init(
      {
        debug: true,
        inputStream: {
          name: "Live",
          type: "LiveStream",
<<<<<<< HEAD
          target: document.querySelector("#scanner"), // Or '#yourElement' (optional)
=======
          target: document.querySelector("#scanner")
>>>>>>> add getEventNameByCode
        },
        decoder: {
          readers: ["code_93_reader", "code_128_reader"],
          // debug: {
          //   drawBoundingBox: true,
          //   showFrequency: true,
          //   drawScanline: true,
          //   showPattern: true,
          // },
        },
      },
<<<<<<< HEAD
      (err) => {
=======
      (err: any) => {
>>>>>>> add getEventNameByCode
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
.underline {
  text-decoration: underline;
}

.attandee-code-alert,
.network-error,
.scanner-error {
  max-width: 100%;
  width: 300px;
}

#scanner .drawingBuffer {
  width: 10px !important;
  height: auto;
}

/* animations */
.slide-fade-enter-active {
  transition: all 0.25s ease;
}
.slide-fade-leave-active {
  transition: all 0.25s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter, .slide-fade-leave-to
/* .slide-fade-leave-active for <2.1.8 */ {
  transform: translateX(100%);
  opacity: 0;
}
</style>
