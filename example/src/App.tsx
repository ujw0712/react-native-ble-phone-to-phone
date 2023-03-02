import React, {useEffect, useState} from 'react';
import {
    NativeEventEmitter,
    NativeModules,
    SafeAreaView,
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from 'react-native';
import {
    advertiseStart,
    advertiseStop,
    scanStart,
    scanStop,
} from 'react-native-ble-phone-to-phone';
import {PERMISSIONS, requestMultiple} from "react-native-permissions";

const App = () => {
  const [list, setList] = useState([]);

  const [uuids] = useState([
    '26f08670-ffdf-40eb-9067-78b9ae6e7919',
    '342730d1-9221-4da0-ab8b-bbd7da07ca62',
  ]);

  const [uuid] = useState('26f08670-ffdf-40eb-9067-78b9ae6e7919');

  useEffect(() => {
    // 권한
    const permission = async() => {
      const result = await requestMultiple([
        PERMISSIONS.ANDROID.BLUETOOTH_SCAN,
        PERMISSIONS.ANDROID.BLUETOOTH_CONNECT,
        PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
        PERMISSIONS.ANDROID.BLUETOOTH_ADVERTISE,
      ]);

      if (result['android.permission.BLUETOOTH_CONNECT']) {
        console.log('Module initialized');
      }
    };
    permission().then();

    // 이벤트 리스터
    const eventEmitter = new NativeEventEmitter(NativeModules.BLEAdvertiser);
    // eslint-disable-next-line @typescript-eslint/no-shadow
    eventEmitter.addListener('foundUuid', (data) => {
        console.log('> data : ', data)
        const newList = new Set([...list,data.uuid]);
        // @ts-ignore
      setList([...newList]);
    });
      eventEmitter.addListener('foundDevice', (data) =>
          console.log('> foundDevice data : ', data)
      );
      eventEmitter.addListener('error', (message) =>
          console.log('> error : ', message)
      );
      eventEmitter.addListener('log', (message) =>
          console.log('> log : ', message)
      );
  }, []);

  const onAdvertiseStart = () => {
    // advertiseStart(uuid);
      advertiseStart();
  };

  const onAdvertiseStop = () => {
    advertiseStop();
  };

  const onScanStart = () => {
    // scanStart(uuids.join());
      scanStart();

    setTimeout(() => {
      onScanStop();
    }, 15000);
  };

  const onScanStop = () => {
    scanStop();
  };

  const onResetList = () => {
    setList([]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView>
        {list.map((item, index) => (
          <View
            style={{ marginBottom: 5, backgroundColor: 'black' }}
            key={index}
          >
            <Text style={{ color: 'white' }}>{JSON.stringify(item)}</Text>
          </View>
        ))}
      </ScrollView>

      <TouchableOpacity
        style={{
          height: 50,
          backgroundColor: 'orange',
        }}
        onPress={onResetList}
      >
        <Text style={{ textAlign: 'center' }}>리스트 리셋</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={{
          height: 50,
          backgroundColor: 'pink',
        }}
        onPress={onAdvertiseStart}
      >
        <Text style={{ textAlign: 'center' }}>Advertise Start</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={{
          height: 50,
          backgroundColor: 'pink',
        }}
        onPress={onAdvertiseStop}
      >
        <Text style={{ textAlign: 'center' }}>Advertise Stop</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={{
          height: 50,
          backgroundColor: 'skyblue',
        }}
        onPress={onScanStart}
      >
        <Text style={{ textAlign: 'center' }}>onScanStart</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={{
          height: 150,
          backgroundColor: 'skyblue',
        }}
        onPress={onScanStop}
      >
        <Text style={{ textAlign: 'center' }}>onScanStop</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f2f2f2',
  },
  heartRateTitleWrapper: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  heartRateTitleText: {
    fontSize: 30,
    fontWeight: 'bold',
    textAlign: 'center',
    marginHorizontal: 20,
    color: 'black',
  },
  heartRateText: {
    fontSize: 25,
    marginTop: 15,
  },
  ctaButton: {
    backgroundColor: 'purple',
    justifyContent: 'center',
    alignItems: 'center',
    height: 50,
    marginHorizontal: 20,
    marginBottom: 100,
    borderRadius: 8,
  },
  ctaButtonText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: 'white',
  },
});

export default App;
