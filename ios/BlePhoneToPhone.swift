import CoreBluetooth

@objc(BlePhoneToPhone)
class BlePhoneToPhone: NSObject {

  let DEFAULT_UUID = CBUUID(string: "26f08670-ffdf-40eb-9067-78b9ae6e7919")

  // @objc(multiply:withB:withResolver:withRejecter:)
  // func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
  //   resolve(a*b)
  // }
  var blePeripheral: CBPeripheralManager!
  var bleCentral: CBCentralManager!

  @objc(onAdvertiseStart:rejecter:)
   func onAdvertiseStart(uuid:string, resolve: @escaping RCTPromiseResolveBlock,
                rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
                  if uuid == nil {
                    uuid = DEFAULT_UUID
                  }
                  let dictionary: [String: Any] = [CBAdvertisementDataServiceUUIDsKey: [uuid],
                                         CBAdvertisementDataLocalNameKey: ""]
                  blePeripheral.startAdvertising(dictionary)
                }

  @objc(onAdvertiseStop:rejecter:)
   func onAdvertiseStop(_ resolve: @escaping RCTPromiseResolveBlock,
                rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
                  blePeripheral.stopAdvertising()
                }

  @objc(onScanStart:rejecter:)
   func onScanStart(uuids:string, resolve: @escaping RCTPromiseResolveBlock,
                rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
                  let uuidList = uuids.componentsSeparatedByString(",")
                  bleCentral.scanForPeripherals(withServices: uuidList, options: nil)
                }

  @objc(onScanStop:rejecter:)
   func onScanStop(_ resolve: @escaping RCTPromiseResolveBlock,
                rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
                  if bleCentral.isScanning {
                    bleCentral.stopScan()
                  }
                  if let peripheral = connectedPeripheral {
                    bleCentral.cancelPeripheralConnection(peripheral)
                  }
                }                
}
