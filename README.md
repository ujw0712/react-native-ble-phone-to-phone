# react-native-ble-phone-to-phone [![npm version](https://img.shields.io/npm/v/react-native-ble-phone-to-phone.svg?style=flat)](https://www.npmjs.com/package/react-native-ble-phone-to-phone) [![npm downloads](https://img.shields.io/npm/dm/react-native-ble-phone-to-phone.svg?style=flat)](https://www.npmjs.com/package/react-native-ble-phone-to-phone) [![GitHub issues](https://img.shields.io/github/issues/ujw0712/react-native-ble-phone-to-phone.svg?style=flat)](https://github.com/ujw0712/react-native-ble-phonet-phone/issues)

A very simple Bluetooth advertiser and scanner for React Native.

## Supported Platforms
- ReactNative 0.71+
- Android 21+
- Bluetooth API 5.0+

## Features

- [x] Android Advertiser
- [x] Android Scanner
- [ ] iOS Advertiser
- [ ] iOS Scanner

## Installation

```bash
npm install react-native-ble-phone-to-phone --save
```

or

```bash
yarn add react-native-ble-phone-to-phone
```

### Setting up the Android Project

In the AndroidManifest.xml file, add the Bluetooth permissions

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

## Usage

### Advertiser

Import the module

```js
import {
  advertiseStart,
  advertiseStop,
} from 'react-native-ble-phone-to-phone';
```

Define uuid to start advertise.
or Start advertise without uuid.

```js
const uuid = '26f08670-ffdf-40eb-9067-78b9ae6e7919';
advertiseStart(uuid);

advertiseStart();
```

Stop advertising

```js
advertiseStop();
```

### Scanner

Import the modules

```js
import {
  BlePhoneToPhoneEvent,
  scanStart,
  scanStop,
} from 'react-native-ble-phone-to-phone'
import { NativeEventEmitter, NativeModules } from 'react-native';
```

Register a listener to collect the devices through ReactNative events.

```js
const eventEmitter = new NativeEventEmitter(NativeModules.BLEAdvertiser);
eventEmitter.addListener('foundUuid', (data) => {
  console.log('> foundUuid data : ', data)   // found uuid
});
eventEmitter.addListener('foundDevice', (data) =>
  console.log('> foundDevice data : ', data) // found device
);
eventEmitter.addListener('error', (error) =>
  console.log('> error : ', error)           // error message
);
eventEmitter.addListener('log', (log) =>
  console.log('> log : ', log)               // log message
);
```

Start scanning with uuid array of strings

```js
const uuids = [
  '26f08670-ffdf-40eb-9067-78b9ae6e7919',
  '342730d1-9221-4da0-ab8b-bbd7da07ca62',
];
scanStart(uuids.join());  
```

If you need to scan all the nearby devices, Start scanning without uuid array

```js
scanStart();  
```

Stop scanning
```js
scanStop();
```
