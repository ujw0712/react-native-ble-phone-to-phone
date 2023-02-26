import type { BleMobileInterface } from 'src/types';
import { NativeModules } from 'react-native';

// 블루투스 모듈
const { BlePhoneToPhone } = NativeModules;

const NativeBleMobile: BleMobileInterface = {
  advertiseStart(uuid: string) {
    return BlePhoneToPhone.onAdvertiseStart(uuid);
  },
  advertiseStop() {
    return BlePhoneToPhone.onAdvertiseStop();
  },
  scanStart(uuids: string) {
    return BlePhoneToPhone.onScanStart(uuids);
  },
  scanStop() {
    return BlePhoneToPhone.onScanStop();
  },
};

export const advertiseStart = NativeBleMobile.advertiseStart;
export const advertiseStop = NativeBleMobile.advertiseStop;
export const scanStart = NativeBleMobile.scanStart;
export const scanStop = NativeBleMobile.scanStop;

export const BlePhoneToPhoneEvent = NativeModules.BLEAdvertiser;
