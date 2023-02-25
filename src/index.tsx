import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-ble-phone-to-phone' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const BlePhoneToPhone = NativeModules.BlePhoneToPhone
  ? NativeModules.BlePhoneToPhone
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return BlePhoneToPhone.multiply(a, b);
}
