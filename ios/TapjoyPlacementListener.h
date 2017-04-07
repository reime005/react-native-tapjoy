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

@interface TapjoyPlacementListener : RCTEventEmitter <TJPlacementDelegate> {
  Boolean listening;
  NSString *m_placementName;
}

- (id)initWithPlacementName:(NSString *)placementName;

// Tapjoy Events
- (void)requestDidSucceed:(TJPlacement*)placement;
- (void)requestDidFail:(TJPlacement*)placement error:(NSError*)error;
- (void)contentIsReady:(TJPlacement*)placement;
- (void)contentDidAppear:(TJPlacement*)placement;
- (void)contentDidDisappear:(TJPlacement*)placement;

@end

#endif /* TapjoyPlacementListener_h */
