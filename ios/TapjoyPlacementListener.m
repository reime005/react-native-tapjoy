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
  return [self initWithPlacementName:@"default"];
}

- (id)initWithPlacementName:(NSString *)placementName {
  self = [super init];
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
  [self sendJSEvent:m_placementName props: evt];
}

// Called when there was a problem during connecting Tapjoy servers.
- (void)requestDidFail:(TJPlacement*)placement error:(NSError*)error{
  NSLog(@"requestDidFail");
  NSDictionary *evt = @{
                        @"onRequestFailure": @"onRequestFailure"
                        };
  [self sendJSEvent:m_placementName props: evt];
}

// Called when the content is actually available to display.
- (void)contentIsReady:(TJPlacement*)placement{
  NSLog(@"contentIsReady");
  NSDictionary *evt = @{
                        @"onContentReady": @"onContentReady"
                        };
  [self sendJSEvent:m_placementName
              props: evt];
}

// Called when the content is showed.
- (void)contentDidAppear:(TJPlacement*)placement{
  NSLog(@"contentDidAppear");
  NSDictionary *evt = @{
                        @"onContentShow": @"onContentShow"
                        };
  [self sendJSEvent:m_placementName
              props: evt];
}

// Called when the content is dismissed.
- (void)contentDidDisappear:(TJPlacement*)placement{
  NSLog(@"contentDidDisappear");
  NSDictionary *evt = @{
                        @"onContentDismiss": @"onContentDismiss"
                        };
  [self sendJSEvent:m_placementName
              props: evt];
}

- (void) sendJSEvent:(NSString *)title props:(NSDictionary *)props
{
  @try {
    if (self->listening) {
      [self sendEventWithName:title
                         body:props];
    }
  }
  @catch (NSException *err) {
    NSLog(@"An error occurred in sendJSEvent: %@", [err debugDescription]);
  }
}

- (NSArray<NSString *> *)supportedEvents {
  return @[m_placementName];
}

@end
