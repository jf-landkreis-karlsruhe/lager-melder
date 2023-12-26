<script setup lang="ts">
import { ref, onUnmounted, onMounted } from 'vue'
import { isValidTestCode } from '../assets/config'
import Quagga from 'quagga' // ES6
import { useToast } from 'vue-toastification'

const toast = useToast()

const CAMERA_DEVICE_ID_KEY = 'cameraDeviceId'

const manualCodeForm = ref<HTMLElement | undefined>()
const code = ref<string>('')
const previousCode = ref<string>('')
const availableDevices = ref<{ id: string; label: string }[]>([])
const activeDeviceId = ref<string | undefined>()
const manualCode = ref<string>('')
const manualCodeValid = ref<boolean>(false)
const isScanning = ref<boolean>(true)

const props = withDefaults(
  defineProps<{
    manualCodeHint?: string
    manualCodeInputRules: ((value: string) => boolean | string)[]
  }>(),
  {
    manualCodeInputRules: () => []
  }
)

const emit = defineEmits<{
  (e: 'submitCode', code: string): void
}>()

const getQuaggaConfig = (deviceId?: string, inputStreamTarget = '#scanner') => {
  return {
    debug: true,
    inputStream: {
      name: 'Live',
      type: 'LiveStream',
      target: document.querySelector(inputStreamTarget),
      constraints: {
        // facingMode: "environment",
        deviceId: deviceId,
        width: 640,
        height: 480
      }
    },
    decoder: {
      readers: ['code_128_reader']
      // debug: {
      //   drawBoundingBox: true,
      //   showFrequency: true,
      //   drawScanline: true,
      //   showPattern: true,
      // },
    }
  }
}

const toggleScanning = () => {
  if (isScanning.value) {
    stopQuagga()
  } else {
    initQuagga(getQuaggaConfig())
  }
  isScanning.value = !isScanning.value
}

const codeDetected = async (data: Quagga.Code) => {
  const detectedCode = data.codeResult.code
  // TODO: use isValidCode for prod
  if (!isValidTestCode(detectedCode)) {
    return
  }
  code.value = detectedCode

  if (code.value && code.value !== previousCode.value) {
    previousCode.value = code.value
    emit('submitCode', code.value)
  }
}

const manualCodeSubmit = () => {
  const manCodeForm = manualCodeForm.value as any
  const manCode = manualCode.value
  manCodeForm.validate()
  emit('submitCode', manCode)
  manCodeForm.reset()
  ;(document?.activeElement as any)?.blur()
}

onMounted(async () => {
  initCameraSelection()
  const storedCameraDeviceId = localStorage.getItem(CAMERA_DEVICE_ID_KEY) || undefined
  initQuagga(getQuaggaConfig(storedCameraDeviceId))
})

onUnmounted(() => {
  Quagga.stop()
})

const stopQuagga = () => {
  Quagga.stop()
  code.value = ''
}

const initQuagga = (config: any) => {
  Quagga.init(config, (err: any) => {
    if (err) {
      console.error(err)
      toast(err)
      return
    }
    console.log('Initialization finished. Ready to start')
    Quagga.start()
    Quagga.onDetected(codeDetected)
  })
}

const cameraChanged = (e: any) => {
  e.preventDefault()
  const cameraDeviceId = e.target.value
  localStorage.setItem(CAMERA_DEVICE_ID_KEY, cameraDeviceId)

  Quagga.stop()
  initQuagga(getQuaggaConfig(cameraDeviceId))
}

const initCameraSelection = () => {
  let streamId = Quagga.CameraAccess.getActiveStreamLabel()
  const storedCameraDeviceId = localStorage.getItem(CAMERA_DEVICE_ID_KEY) || undefined
  if (storedCameraDeviceId) {
    streamId = storedCameraDeviceId
  }
  activeDeviceId.value = streamId

  return Quagga.CameraAccess.enumerateVideoDevices().then((devices: any) => {
    availableDevices.value = [...devices].map((device) => {
      return {
        id: device.deviceId || device.id,
        label: device.label.substring(0, 30) || device.deviceId || device.id
      }
    })
  })
}
</script>

<template>
  <div>
    <v-container class="scanner-root">
      <v-row justify="center">
        <transition name="fade" mode="out-in" :appear="true">
          <div v-if="true">
            <div v-show="isScanning">
              <label class="camera-selection">
                <span>📸 Kamera</span>
                <select
                  name="input-stream_constraints"
                  class="camera-select"
                  @change="cameraChanged"
                >
                  <option value="">--Select your camera--</option>
                  <option
                    v-for="device in availableDevices"
                    :key="device.id"
                    :value="device.id"
                    :selected="activeDeviceId === device.id"
                  >
                    {{ device.label }}
                  </option>
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
                <v-btn @click="toggleScanning" color="primary" type="button" x-small outlined>
                  <span v-if="isScanning"><v-icon medium>mdi-stop</v-icon> Stop Scanner</span>
                  <span v-else><v-icon medium>mdi-play</v-icon> Start Scanner</span>
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
                  :hint="props.manualCodeHint"
                  :rules="props.manualCodeInputRules"
                  class="manual-code-input mr-3"
                  variant="underlined"
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
      content: '';
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
