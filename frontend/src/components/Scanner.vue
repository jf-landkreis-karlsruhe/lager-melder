<template>
  <div>
    <v-container class="scanner-root">
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
                'justify-center': !isScanning,
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
              ref="manualCodeForm"
              v-model="manualCodeValid"
              @submit.prevent="manualCodeSubmit"
              class="manual-code-form d-flex justify-center mt-6"
            >
              <v-row class="manual-code-row align-center">
                <v-text-field
                  v-model="manualCode"
                  label="Manuelle Eingabe"
                  :hide-details="false"
                  :hint="manualCodeHint"
                  :rules="manualCodeInputRules"
                  class="manual-code-input mr-3"
                />
                <v-btn
                  :disabled="!manualCode || !manualCodeValid"
                  type="submit"
                  small
                  outlined
                  rounded
                >
                  Abschicken
                </v-btn>
              </v-row>
            </v-form>
          </div>
        </transition>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Vue, Component, Prop, Emit } from "vue-property-decorator";
import Quagga from "quagga"; // ES6
import { isValidTestCode } from "../assets/config";

const CAMERA_DEVICE_ID_KEY = "cameraDeviceId";

@Component({ name: "ScannerComponent" })
export default class ScannerComponent extends Vue {
  code: string = "";
  previousCode: string = "";

  manualCode: string = "";
  manualCodeValid = false;

  isScanning: boolean = true;

  @Prop()
  private readonly manualCodeHint: string | undefined;

  @Prop({ default: () => [] })
  private readonly manualCodeInputRules!: () => string[];

  @Emit("submitCode")
  public submitCode(code: string) {
    return code;
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
          height: 480,
        },
      },
      decoder: {
        readers: ["code_128_reader"],
        // debug: {
        //   drawBoundingBox: true,
        //   showFrequency: true,
        //   drawScanline: true,
        //   showPattern: true,
        // },
      },
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
    this.code = detectedCode;

    if (this.code && this.code !== this.previousCode) {
      this.previousCode = this.code;
      this.submitCode(this.code);
    }
  }

  protected manualCodeSubmit() {
    const manualCodeForm = this.$refs.manualCodeForm as any;
    const manualCode = this.manualCode;
    manualCodeForm.validate();
    this.submitCode(manualCode);
    manualCodeForm.reset();
    (document?.activeElement as any)?.blur();
  }

  async mounted() {
    this.initCameraSelection();
    const storedCameraDeviceId =
      localStorage.getItem(CAMERA_DEVICE_ID_KEY) || undefined;
    this.initQuagga(this.getQuaggaConfig(storedCameraDeviceId));
  }

  destroyed() {
    Quagga.stop();
  }

  stopQuagga() {
    Quagga.stop();
    this.code = "";
  }

  initQuagga(config: any) {
    Quagga.init(config, (err: any) => {
      if (err) {
        console.error(err);
        this.$toast(err);
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
    localStorage.setItem(CAMERA_DEVICE_ID_KEY, cameraDeviceId);

    Quagga.stop();
    this.initQuagga(this.getQuaggaConfig(cameraDeviceId));
  }

  initCameraSelection() {
    let streamLabel = Quagga.CameraAccess.getActiveStreamLabel();
    const storedCameraDeviceId =
      localStorage.getItem(CAMERA_DEVICE_ID_KEY) || undefined;
    if (storedCameraDeviceId) {
      streamLabel = storedCameraDeviceId;
    }

    return Quagga.CameraAccess.enumerateVideoDevices().then((devices: any) => {
      function pruneText(text: string) {
        return text?.length > 30 ? text.substr(0, 30) : text;
      }
      const $deviceSelection = document.getElementById("deviceSelection");

      while ($deviceSelection?.firstChild) {
        $deviceSelection.removeChild($deviceSelection.firstChild);
      }
      devices.forEach((device: any) => {
        let $option = document.createElement("option");
        $option.value = device.deviceId || device.id;
        $option.appendChild(
          document.createTextNode(
            pruneText(device.label || device.deviceId || device.id)
          )
        );
        $option.selected = streamLabel === device.deviceId;
        $deviceSelection?.appendChild($option);
      });
    });
  }
}
</script>

<style lang="scss">
video {
  width: 100%;
}

// todo: figure out why this does not work
.drawingBuffer {
  position: absolute;
  width: 10px !important;
  height: auto;
}
</style>

<style scoped lang="scss">
* {
  box-sizing: border-box;
}

.scanner-root {
  margin-bottom: 3rem;

  .scanner {
    position: relative;
    overflow: hidden;
    width: 100%;
    height: 500px;
    max-width: 100%;
    max-height: 100%;
    margin-bottom: 6px;

    @media screen and (min-width: 768px) {
      width: 640px;
      height: 480px;
    }

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
      animation: scan 3s linear infinite;
      opacity: 0.5;
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
}
</style>
