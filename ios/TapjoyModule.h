//
//  TapjoyModule.h
//  GetRich
//
//  Created by Marius on 24/02/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#ifndef TapjoyModule_h
#define TapjoyModule_h

#import <Foundation/Foundation.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
#import <Tapjoy/Tapjoy.h>
#import <Tapjoy/TJPlacement.h>
#import <React/RCTLog.h>
#import "TapjoyPlacementListener.h"

@interface TapjoyModule : RCTEventEmitter <RCTBridgeModule> {
  RCTPromiseResolveBlock mResolve;
  RCTPromiseRejectBlock mReject;
  
  NSMutableDictionary *placementMap;
  NSMutableDictionary *placementListenerMap;
  Boolean listening;
}

@property (nonatomic, retain) UIViewController *viewController;

- (void) sendJSEvent:(NSString *)title props:(NSDictionary *)props;

@end

static NSString *const TJ_EVENT_EARNED_CURRENCY = @"earnedCurrency";
static NSString *const TJ_EVENT_PLACEMENT_DISMISS = @"onPlacementDismiss";
static NSString *const TJ_EVENT_PLACEMENT_CONTENT_READY = @"onPlacementContentReady";
static NSString *const TJ_EVENT_PLACEMENT_PURCHASE_REQUEST = @"onPurchaseRequest";
static NSString *const TJ_EVENT_PLACEMENT_REWARD_REQUEST = @"onRewardRequest";

#endif /* TapjoyModule_h */
