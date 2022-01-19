<template>
  <div>
    <v-container class="scanner-root">
      <h1>🏕 Event: {{ eventName || "Anstehendes Event" }}</h1>
      <v-row justify="center">
        <transition name="fade" mode="out-in">
          <div>
            <div v-show="isScanning">
              <label class="camera-selection">
                <span>📸 Kamera</span>
                <select
                  id="deviceSelection"
                  name="input-stream_constraints"
                  class="camera-select"
                  @change="cameraChanged"
                >
                  <option value="">--Select your camera--</option>
                </select>
              </label>

              <div id="scanner" class="scanner"></div>
            </div>

            <div
              class="d-flex"
              :class="{
                'justify-space-between': isScanning,
                'justify-center': !isScanning
              }"
            >
              <v-flex grow="true" v-if="isScanning"> ...Scanning... </v-flex>
              <v-flex shrink="true">
                <v-btn
                  @click="toggleScanning"
                  color="primary"
                  type="button"
                  x-small
                  outlined
                >
                  <span v-if="isScanning"
                    ><v-icon medium>mdi-stop</v-icon> Stop Scanner</span
                  >
                  <span v-else
                    ><v-icon medium>mdi-play</v-icon> Start Scanner</span
                  >
                </v-btn>
              </v-flex>
            </div>

            <v-form
              ref="manualAttendeeCodeForm"
              v-model="manualAttendeeCodeValid"
              @submit.prevent="manualCodeSubmit"
              class="manual-code-form d-flex justify-center mt-6"
            >
              <v-row class="manual-code-row align-center">
                <v-text-field
                  v-model="manualAttendeeCode"
                  label="Manuelle Eingabe"
                  :hide-details="false"
                  hint="8 Zeichen benötigt"
                  :rules="manualCodeInputRules"
                  class="manual-code-input mr-3"
                />
                <v-btn
                  :disabled="!manualAttendeeCodeValid"
                  type="submit"
                  small
                  outlined
                >
                  Abschicken
                </v-btn>
              </v-row>
            </v-form>
          </div>
        </transition>
      </v-row>

      <transition name="slide-fade" mode="out-in">
        <v-alert
          class="attandee-code-success"
          :key="attendeeAddedSentence"
          :value="!!attendeeAddedSentence"
          type="success"
          dismissible
        >
          {{ attendeeAddedSentence }}
        </v-alert>
      </transition>

      <v-row justify="center">
        <div class="errors">
          <v-alert
            class="network-error"
            :value="!!networkError"
            type="error"
            transition="slide-y-transition"
            dismissible
          >
            {{ networkError }}
          </v-alert>
          <v-alert
            class="scanner-error"
            v-if="scannerError"
            type="error"
            transition="slide-y-transition"
            dismissible
          >
            {{ scannerError }}
          </v-alert>
        </div>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import Quagga from "quagga"; // ES6
import { getEventByCode, loginToEvent } from "../services/event";
import { isValidTestCode } from "../assets/config";

@Component({ name: "ScannerComponent" })
export default class ScannerComponent extends Vue {
  eventCode: string = "";
  eventName: string = "";

  attendeeCode: string = "";
  previousAttandeeCode: string = "";
  attendeeAddedSentence: string = "";

  manualAttendeeCode: string = "";
  manualAttendeeCodeValid = false;

  isScanning: boolean = true;
  scannerError: string = "";
  networkError: string = "";

  private get manualCodeInputRules() {
    return [
      (value: string) => !!value || "Required.",
      (value: string) => isValidTestCode(value) || "8 Zeichen benötigt"
    ];
  }

  private getQuaggaConfig(deviceId?: string, inputStreamTarget = "#scanner") {
    return {
      debug: true,
      inputStream: {
        name: "Live",
        type: "LiveStream",
        target: document.querySelector(inputStreamTarget),
        constraints: {
          // facingMode: "environment",
          deviceId: deviceId,
          width: 640,
          height: 480
        }
      },
      decoder: {
        readers: ["code_128_reader"]
        // debug: {
        //   drawBoundingBox: true,
        //   showFrequency: true,
        //   drawScanline: true,
        //   showPattern: true,
        // },
      }
    };
  }

  protected toggleScanning() {
    if (this.isScanning) {
      this.stopQuagga();
    } else {
      this.initQuagga(this.getQuaggaConfig());
    }
    this.isScanning = !this.isScanning;
  }

  protected async codeDetected(data: Quagga.Code) {
    const detectedCode = data.codeResult.code;
    // TODO: use isValidCode for prod
    if (!isValidTestCode(detectedCode)) {
      return;
    }
    this.attendeeCode = detectedCode;

    if (this.attendeeCode !== this.previousAttandeeCode) {
      this.previousAttandeeCode = this.attendeeCode;
      this.submitEvent(this.attendeeCode);
    }
  }

  protected manualCodeSubmit() {
    const manualCodeForm = this.$refs.manualAttendeeCodeForm as any;
    const manualCode = this.manualAttendeeCode;
    manualCodeForm.validate();
    this.submitEvent(manualCode);
    manualCodeForm.reset();
    (document?.activeElement as any)?.blur();
  }

  protected async submitEvent(attendeeCode: string) {
    if (!isValidTestCode(attendeeCode)) {
      return;
    }
    const attendeeRes = await loginToEvent(this.eventCode, attendeeCode).catch(
      reason => {
        this.networkError = JSON.stringify(reason);
      }
    );
    console.log(attendeeRes);
    if (attendeeRes) {
      this.attendeeAddedSentence = `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich hinzugefügt.`;
    }
  }

  async mounted() {
    this.eventCode = this.$route.params.eventCode;
    getEventByCode(this.eventCode).then(event => {
      this.eventName = event.name;
    });
    this.initCameraSelection();
    this.initQuagga(this.getQuaggaConfig());
  }

  stopQuagga() {
    Quagga.stop();
    this.attendeeCode = "";
    this.networkError = "";
    this.scannerError = "";
  }

  initQuagga(config: any) {
    Quagga.init(config, (err: any) => {
      if (err) {
        console.log(err);
        this.scannerError = err;
        return;
      }
      console.log("Initialization finished. Ready to start");
      Quagga.start();
      Quagga.onDetected(this.codeDetected);
    });
  }

  cameraChanged(e: any) {
    e.preventDefault();
    const cameraDeviceId = e.target.value;

    Quagga.stop();
    this.initQuagga(this.getQuaggaConfig(cameraDeviceId));
  }

  initCameraSelection() {
    var streamLabel = Quagga.CameraAccess.getActiveStreamLabel();

    return Quagga.CameraAccess.enumerateVideoDevices().then((devices: any) => {
      function pruneText(text: string) {
        return text?.length > 30 ? text.substr(0, 30) : text;
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

.scanner-root {
  margin-bottom: 8rem;
  position: relative;
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

.manual-code-form {
  .manual-code-row {
    max-width: 300px;
  }
}

.attandee-code-success,
.network-error,
.scanner-error {
  max-width: 100%;
  min-width: 300px;
}
.attandee-code-success {
  position: absolute;
  bottom: 0;
  right: 12px;
  transform: translateY(100%);
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
