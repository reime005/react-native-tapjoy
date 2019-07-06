//
//  TapjoyPlacementListener.m
//  GetRich
//
//  Created by Marius on 12/03/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "TapjoyPlacementListener.h"

@implementation TapjoyPlacementListener

- (id)init {
  // Forward to the "designated" initialization method
  return [self initWithTapjoyModule:nil];
}

- (id)initWithTapjoyModule:(TapjoyModule*)tapjoyModule {
  self = [super init];
  _tapjoyModule = tapjoyModule;
  return self;
}

// Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
- (void)requestDidSucceed:(TJPlacement*)placement{
  if (_requestPromiseResolve != nil) {
    _requestPromiseResolve(@"Placement request succeeded.");
    _requestPromiseResolve = nil;
  }
}

// Called when there was a problem during connecting Tapjoy servers.
- (void)requestDidFail:(TJPlacement*)placement error:(NSError*)error{
  if (_requestPromiseReject != nil) {
    _requestPromiseReject([NSString stringWithFormat:@"%ld", error.code], error.localizedDescription, error);
    _requestPromiseReject = nil;
  }
}

// Called when the content is actually available to display.
- (void)contentIsReady:(TJPlacement*)placement{
  [_tapjoyModule sendJSEvent:TJ_EVENT_PLACEMENT_CONTENT_READY props:@{
                                                                @"placementName": placement.placementName
                                                                }];
}

// Called when the content is shown.
- (void)contentDidAppear:(TJPlacement*)placement{
  if (_showPromiseResolve != nil) {
    _showPromiseResolve(@"Showing Placement.");
    _showPromiseResolve = nil;
  }
}

// Called when the content is dismissed.
- (void)contentDidDisappear:(TJPlacement*)placement{
  [_tapjoyModule sendJSEvent:TJ_EVENT_PLACEMENT_DISMISS props:@{
                                                                @"placementName": placement.placementName
                                                                }];
}

@end
