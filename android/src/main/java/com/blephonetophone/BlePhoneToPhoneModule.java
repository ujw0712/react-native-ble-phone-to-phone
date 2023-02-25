package com.blephonetophone;

import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.module.annotations.ReactModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;

@ReactModule(name = BlePhoneToPhoneModule.NAME)
public class BlePhoneToPhoneModule extends ReactContextBaseJavaModule {
    public static final String NAME = "BlePhoneToPhone";

    //Constructor
    public BlePhoneToPhoneModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mAdvertiserList = new Hashtable<String, BluetoothLeAdvertiser>();
        mAdvertiserCallbackList = new Hashtable<String, AdvertiseCallback>();

        BluetoothManager bluetoothManager = (BluetoothManager) reactContext.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter != null) {
            mObservedState = mBluetoothAdapter.isEnabled();
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        reactContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private ReactApplicationContext context;
    private BluetoothAdapter mBluetoothAdapter;
    private static Hashtable<String, BluetoothLeAdvertiser> mAdvertiserList ;
    private static Hashtable<String, AdvertiseCallback> mAdvertiserCallbackList;
    private static BluetoothLeScanner mScanner;
    private static ScanCallback mScannerCallback;
    private Boolean mObservedState;


    private BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = manager.getAdapter();
        }
        return mBluetoothAdapter;
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void onAdvertiseStart(String uuid) {
        BluetoothLeAdvertiser tempAdvertiser;
        AdvertiseCallback tempCallback;

        tempAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        tempCallback = new BlePhoneToPhoneModule.SimpleAdvertiseCallback();

        if (tempAdvertiser == null) {
            WritableMap params = Arguments.createMap();
            params.putString("onAdvertiseStart", "Advertiser Not Available unavailable");
            sendEvent("error", params);
            return;
        }

        ParcelUuid parcelUuid = new ParcelUuid(UUID.fromString(uuid));
        AdvertiseData data = buildAdvertiseData(parcelUuid);
        AdvertiseSettings settings = buildAdvertiseSettings(null);
        tempAdvertiser.startAdvertising(settings, data, tempCallback);

        mAdvertiserList.put(uuid, tempAdvertiser);
        mAdvertiserCallbackList.put(uuid, tempCallback);

        WritableMap params = Arguments.createMap();
        params.putString("onAdvertiseStart", uuid);
        sendEvent("log", params);
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void onAdvertiseStop() {
        if (mBluetoothAdapter == null) {
            WritableMap params = Arguments.createMap();
            params.putString("onAdvertiseStop", "mBluetoothAdapter unavailable");
            sendEvent("error", params);
            return;
        }

        if (mObservedState != null && !mObservedState) {
            WritableMap params = Arguments.createMap();
            params.putString("onAdvertiseStop", "Bluetooth disabled");
            sendEvent("error", params);
            return;
        }

        WritableArray promiseArray=Arguments.createArray();

        Set<String> keys = mAdvertiserList.keySet();
        for (String key : keys) {
            BluetoothLeAdvertiser tempAdvertiser = mAdvertiserList.remove(key);
            AdvertiseCallback tempCallback = mAdvertiserCallbackList.remove(key);
            if (tempAdvertiser != null) {
                tempAdvertiser.stopAdvertising(tempCallback);
                promiseArray.pushString(key);
            }
        }

        WritableMap params = Arguments.createMap();
        params.putString("onAdvertiseStop", promiseArray+"");
        sendEvent("log", params);
    }

    private AdvertiseSettings buildAdvertiseSettings(ReadableMap options) {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        return settingsBuilder.build();
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void onScanStart(String uuids) {
        if (mBluetoothAdapter == null) {
            WritableMap params = Arguments.createMap();
            params.putString("onScanStart", "Device does not support Bluetooth. Adapter is Null");
            sendEvent("error", params);
            return;
        }

        if (mObservedState != null && !mObservedState) {
            WritableMap params = Arguments.createMap();
            params.putString("onScanStart", "Bluetooth disabled");
            sendEvent("error", params);
            return;
        }

        if (mScannerCallback == null) {
            // Cannot change.
            mScannerCallback = new SimpleScanCallback();
        }

        if (mScanner == null) {
            mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        } else {
            // was running. Needs to stop first.
            mScanner.stopScan(mScannerCallback);
        }

        if (mScanner == null) {
            WritableMap params = Arguments.createMap();
            params.putString("onScanStart", "Scanner unavailable on this device");
            sendEvent("error", params);
            return;
        }

        ScanSettings scanSettings = buildScanSettings(null);

        List<ScanFilter> filters = new ArrayList<>();

        List<String> uuidList = Arrays.asList(uuids.split(","));
        for (String uuid : uuidList) {
            filters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(uuid)).build());
        }

        mScanner.startScan(filters, scanSettings, mScannerCallback);

        WritableMap params = Arguments.createMap();
        params.putString("onScanStart", "Scanner started : "+uuids);
        sendEvent("log", params);
    }

    @SuppressLint("MissingPermission")
    @ReactMethod
    public void onScanStop() {
        if (mBluetoothAdapter == null) {
            WritableMap params = Arguments.createMap();
            params.putString("onScanStop", "Device does not support Bluetooth. Adapter is Null");
            sendEvent("error", params);
            return;
        }

        if (mObservedState != null && !mObservedState) {
            WritableMap params = Arguments.createMap();
            params.putString("onScanStop", "Bluetooth disabled");
            sendEvent("error", params);
            return;
        }


        WritableMap params = Arguments.createMap();
        if (mScanner != null) {
            mScanner.stopScan(mScannerCallback);
            mScanner = null;

            params.putString("onScanStop", "Scanner stopped");
        } else {
            params.putString("onScanStop", "Scanner not started");
        }
        sendEvent("log", params);
    }

    private AdvertiseData buildAdvertiseData(ParcelUuid uuid) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(uuid);
        return dataBuilder.build();
    }

    private class SimpleAdvertiseCallback extends AdvertiseCallback {
        public SimpleAdvertiseCallback () {
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);

            WritableMap params = Arguments.createMap();

            switch (errorCode) {
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    params.putString("onStartFailure", "This feature is not supported on this platform."); break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    params.putString("onStartFailure", "Failed to start advertising because no advertising instance is available."); break;
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    params.putString("onStartFailure", "Failed to start advertising as the advertising is already started."); break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    params.putString("onStartFailure", "Failed to start advertising as the advertise data to be broadcastbroadcasted is larger than 31 bytes."); break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    params.putString("onStartFailure", "Operation failed due to an internal error."); break;
            }

            sendEvent("error", params);
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                final int prevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        mObservedState = false;
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        mObservedState = false;
                        break;
                    case BluetoothAdapter.STATE_ON:
                        mObservedState = true;
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        mObservedState = true;
                        break;
                }

                // Only send enabled when fully ready. Turning on and Turning OFF are seen as disabled.
                if (state == BluetoothAdapter.STATE_ON && prevState != BluetoothAdapter.STATE_ON) {
                    WritableMap params = Arguments.createMap();
                    params.putBoolean("enabled", true);
                    sendEvent("onBTStatusChange", params);
                } else if (state != BluetoothAdapter.STATE_ON && prevState == BluetoothAdapter.STATE_ON ) {
                    WritableMap params = Arguments.createMap();
                    params.putBoolean("enabled", false);
                    sendEvent("onBTStatusChange", params);
                }
            }
        }
    };

    private ScanSettings buildScanSettings(ReadableMap options) {
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        return scanSettingsBuilder.build();
    }

    private class SimpleScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            WritableMap params = Arguments.createMap();
            WritableArray paramsUUID = Arguments.createArray();

            if (result.getScanRecord().getServiceUuids()!=null) {
                for (ParcelUuid uuid : result.getScanRecord().getServiceUuids()) {
                    paramsUUID.pushString(uuid.toString());
                }
            }

            String uuid = paramsUUID.getString(0);
            if (uuid.length() > 0) {
                params.putString("uuid", uuid);
                sendEvent("foundUuid", params);
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            WritableMap params = Arguments.createMap();
           switch (errorCode) {
                case SCAN_FAILED_ALREADY_STARTED:
                    params.putString("onScanFailed", "Fails to start scan as BLE scan with the same settings is already started by the app."); break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    params.putString("onScanFailed", "Fails to start scan as app cannot be registered."); break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED:
                    params.putString("onScanFailed", "Fails to start power optimized scan as this feature is not supported."); break;
                case SCAN_FAILED_INTERNAL_ERROR:
                    params.putString("onScanFailed", "Fails to start scan due an internal error"); break;
                default:
                    params.putString("onScanFailed", "Scan failed: " + errorCode);
            }
            sendEvent("error", params);
        }
    };

    private void sendEvent(String eventName, WritableMap params) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
