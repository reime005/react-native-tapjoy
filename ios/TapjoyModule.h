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
#import "AppDelegate.h"

@interface TapjoyModule : RCTEventEmitter <RCTBridgeModule> {
  RCTResponseSenderBlock m_callback;
  
  NSMutableDictionary *placementMap;
  NSMutableDictionary *placementListenerMap;
  Boolean listening;
}

@property (nonatomic, retain) AppDelegate *viewController;

- (void) sendJSEvent:(NSString *)title props:(NSDictionary *)props;

@end

static NSString *const TJ_EARNED_CURRENCY_EVENT = @"earnedCurrency";
static NSString *const TJ_PLACEMENT_OFFERWALL_EVENT = @"Offerwall";

#endif /* TapjoyModule_h */
