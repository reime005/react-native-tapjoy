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
  return [self initWithPlacementName:@"default" tapjoyModule:nil];
}

- (id)initWithPlacementName:(NSString *)placementName tapjoyModule:(TapjoyModule*)tapjoyModule {
  self = [super init];
  _tapjoyModule = tapjoyModule;
  if (self) {
    m_placementName = placementName;
  }
  return self;
}

// Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
- (void)requestDidSucceed:(TJPlacement*)placement{
  NSLog(@"requestDidSucceed");
  NSDictionary *evt = @{
                        @"onRequestSuccess": @"onRequestSuccess"
                        };
  [_tapjoyModule sendJSEvent:m_placementName props:evt];
}

// Called when there was a problem during connecting Tapjoy servers.
- (void)requestDidFail:(TJPlacement*)placement error:(NSError*)error{
  NSLog(@"requestDidFail");
  NSDictionary *evt = @{
                        @"onRequestFailure": @"onRequestFailure"
                        };
  [_tapjoyModule sendJSEvent:m_placementName props:evt];
}

// Called when the content is actually available to display.
- (void)contentIsReady:(TJPlacement*)placement{
  NSLog(@"contentIsReady");
  NSDictionary *evt = @{
                        @"onContentReady": @"onContentReady"
                        };
  [_tapjoyModule sendJSEvent:m_placementName props:evt];
}

// Called when the content is shown.
- (void)contentDidAppear:(TJPlacement*)placement{
  NSLog(@"contentDidAppear");
  NSDictionary *evt = @{
                        @"onContentShow": @"onContentShow"
                        };
  [_tapjoyModule sendJSEvent:m_placementName props:evt];
}

// Called when the content is dismissed.
- (void)contentDidDisappear:(TJPlacement*)placement{
  NSLog(@"contentDidDisappear");
  NSDictionary *evt = @{
                        @"onContentDismiss": @"onContentDismiss"
                        };
  [_tapjoyModule sendJSEvent:m_placementName props:evt];
}

- (NSArray<NSString *> *)supportedEvents {
  return @[m_placementName];
}

@end
