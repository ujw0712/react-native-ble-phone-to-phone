
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNBlePhoneToPhoneSpec.h"

@interface BlePhoneToPhone : NSObject <NativeBlePhoneToPhoneSpec>
#else
#import <React/RCTBridgeModule.h>

@interface BlePhoneToPhone : NSObject <RCTBridgeModule>
#endif

@end
