declare module "quagga" {
  export interface Code {
    codeResult: {
      code: string;
    };
  }

  export function init(options: any, err: any): void;
  export function start(): void;
  export function stop(): void;
  export function onDetected(codeDetected: any): void;
  export const CameraAccess: {
    getActiveStreamLabel: () => any;
    enumerateVideoDevices: () => any;
  };
}
