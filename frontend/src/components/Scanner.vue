<template>
  <div>
    <v-container>
      <h1>🏕 Event: {{ eventName || "Anstehendes Event" }}</h1>
      <v-row justify="center">
        <transition name="fade" mode="out-in">
          <div v-show="isScanning">
            <label class="camera-selection">
              <span>📸 Kamera: </span>
              <select
                id="deviceSelection"
                name="input-stream_constraints"
                class="camera-select"
              >
                <option value="">--Select your camera--</option>
              </select>
            </label>
            <div id="scanner" class="scanner"></div>
          </div>
        </transition>
      </v-row>

      <v-row justify="center">
        <form v-on:submit.prevent="() => {}">
          <p v-if="isScanning" class="scanning-label">...Scanning</p>

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
            <span v-if="isScanning"
              ><v-icon medium>mdi-stop</v-icon> Stop Scanner</span
            >
            <span v-else><v-icon medium>mdi-play</v-icon> Start Scanner</span>
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
    getEventByCode(this.eventCode).then((event) => {
      this.eventName = event.name;
    });
    this.initCameraSelection();
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
          target: document.querySelector("#scanner"),
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
      (err: any) => {
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

  initCameraSelection() {
    var streamLabel = Quagga.CameraAccess.getActiveStreamLabel();

    return Quagga.CameraAccess.enumerateVideoDevices().then((devices: any) => {
      function pruneText(text: string) {
        return text.length > 30 ? text.substr(0, 30) : text;
      }
      const $deviceSelection = document.getElementById("deviceSelection");
      while ($deviceSelection?.firstChild) {
        $deviceSelection.removeChild($deviceSelection.firstChild);
      }
      devices.forEach((device: any) => {
        var $option = document.createElement("option");
        $option.value = device.deviceId || device.id;
        $option.appendChild(
          document.createTextNode(
            pruneText(device.label || device.deviceId || device.id)
          )
        );
        $option.selected = streamLabel === device.label;
        $deviceSelection?.appendChild($option);
      });
    });
  }
}
</script>

<style scoped lang="scss">
* {
  box-sizing: border-box;
}
.underline {
  text-decoration: underline;
}

.scanner {
  position: relative;
  overflow: hidden;
  width: 640px;
  max-width: 100%;
  height: 480px;
  max-height: 100%;
  margin-bottom: 6px;

  // scan-effect-animation
  &::after {
    content: "";
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    z-index: 99;
    width: 100%;
    height: 5px;
    background-color: white;
    box-shadow: 0 0 35px 5px white;
    animation: scan 3s ease-in-out infinite;
    opacity: 0.5;
  }

  // todo: figure out why this does not work
  .drawingBuffer {
    position: absolute;
    width: 10px !important;
    height: auto;
  }
}
.scanning-label {
  text-align: center;
  margin-bottom: 24px;
}

.camera-selection {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 12px;

  .camera-select {
    font-weight: bold;
    border: 1px solid gray;
    margin: 0 0 0 6px;
    padding: 6px;
  }
}

.attandee-code-alert,
.network-error,
.scanner-error {
  max-width: 100%;
  width: 300px;
}

/* animations */
// scan-effect
@keyframes scan {
  from {
    margin-top: -20px;
  }
  to {
    margin-top: 100%;
  }
}

// fade
.fade-enter-active {
  transition: all 2s ease;
}
.fade-leave-active {
  transition: all 0.25s cubic-bezier(1, 0.5, 0.8, 1);
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

// slide-fade
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
