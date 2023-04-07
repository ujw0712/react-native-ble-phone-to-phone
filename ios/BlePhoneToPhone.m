#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(BlePhoneToPhone, NSObject)

// RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
//                  withResolver:(RCTPromiseResolveBlock)resolve
//                  withRejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(onAdvertiseStart:(string)uuid
                  (RCTPromiseResolveBlock *)resolve rejecter:(RCTPromiseRejectBlock *)reject);
RCT_EXTERN_METHOD(onAdvertiseStop:(RCTPromiseResolveBlock *)resolve rejecter:(RCTPromiseRejectBlock *)reject);                 
RCT_EXTERN_METHOD(onScanStart:(string)uuids
                  (RCTPromiseResolveBlock *)resolve rejecter:(RCTPromiseRejectBlock *)reject);                 
RCT_EXTERN_METHOD(onScanStop:(RCTPromiseResolveBlock *)resolve rejecter:(RCTPromiseRejectBlock *)reject);                 

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
