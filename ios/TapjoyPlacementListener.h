//
//  TapjoyPlacementListener.h
//  GetRich
//
//  Created by Marius on 12/03/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#ifndef TapjoyPlacementListener_h
#define TapjoyPlacementListener_h

#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
#import <Tapjoy/Tapjoy.h>
#import <React/RCTLog.h>
#import "TapjoyModule.h"

@class TapjoyModule;

@interface TapjoyPlacementListener : NSObject <TJPlacementDelegate, RCTBridgeModule> { }

@property (strong) TapjoyModule *tapjoyModule;
@property (strong) RCTPromiseRejectBlock requestPromiseReject;
@property (strong) RCTPromiseResolveBlock requestPromiseResolve;
@property (strong) RCTPromiseRejectBlock showPromiseReject;
@property (strong) RCTPromiseResolveBlock showPromiseResolve;

- (id)initWithTapjoyModule:(TapjoyModule*)tapjoyModule;

@end

#endif /* TapjoyPlacementListener_h */
