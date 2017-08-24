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

@interface TapjoyPlacementListener : RCTEventEmitter <TJPlacementDelegate, RCTBridgeModule> {
  Boolean listening;
  NSString *m_placementName;
}

@property (strong) TapjoyModule *tapjoyModule;

- (id)initWithPlacementName:(NSString *)placementName tapjoyModule:(TapjoyModule*)tapjoyModule;

// Tapjoy Events

@end

#endif /* TapjoyPlacementListener_h */
