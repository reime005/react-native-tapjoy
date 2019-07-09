//
//  TapjoyIosModule.m
//  GetRich
//
//  Created by Marius on 24/02/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "TapjoyModule.h"

@implementation TapjoyModule

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initialise:(NSString *)name debug:(BOOL)debug resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  mResolve = resolve;
  mReject = reject;

  placementMap = [[NSMutableDictionary alloc]init];
  placementListenerMap = [[NSMutableDictionary alloc]init];

  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(tjcConnectSuccess: )
                                               name:TJC_CONNECT_SUCCESS
                                             object:nil];

  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(tjcConnectFail:)
                                               name:TJC_CONNECT_FAILED
                                             object:nil];

  //Turn on Tapjoy debug mode
  [Tapjoy setDebugEnabled:debug]; //Do not set this for any version of the app released to an app store

  //Tapjoy connect call
  [Tapjoy connect:name];
}

RCT_EXPORT_METHOD(setUserId:(nonnull NSString *)userId resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  [Tapjoy setUserIDWithCompletion:userId completion:^(BOOL success, NSError *error) {
    if (error != nil) {
      [TapjoyModule rejectPromise:reject withError:error];
    } else {
      resolve(nil);
    }
  }];
}

RCT_EXPORT_METHOD(addPlacement:(NSString *)placementName resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  if ([Tapjoy isConnected]) {
    if ([placementMap objectForKey:placementName] || [placementListenerMap objectForKey:placementName]) {
      [placementMap removeObjectForKey:placementName];
      [placementListenerMap removeObjectForKey:placementName];
    }

    TapjoyPlacementListener *placementListener = [[TapjoyPlacementListener alloc] initWithTapjoyModule:self];
    TJPlacement *placement = [TJPlacement placementWithName:placementName delegate:placementListener ];

    [placementMap setObject:placement forKey:placementName];
    [placementListenerMap setObject:placementListener forKey:placementName];
    resolve(@"Tapjoy placement added.");
  } else {
      [TapjoyModule rejectPromise:reject withMessage:@"Tapjoy is not connected."];
  }
}

RCT_EXPORT_METHOD(showPlacement:(NSString *)placementName resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  if ([Tapjoy isConnected]) {
    TJPlacement *placement = [placementMap objectForKey:placementName];

    if (placement) {
      dispatch_async(dispatch_get_main_queue(),  ^(void) {
        TapjoyPlacementListener* listener = [placementListenerMap objectForKey:placementName];

        if (listener != nil) {
          [listener setShowPromiseResolve:resolve];
          [listener setShowPromiseReject:reject];
        }

        [placement showContentWithViewController: [UIApplication sharedApplication].delegate.window.rootViewController];
      });
    } else {
      [TapjoyModule rejectPromise:reject withMessage:@"Tapjoy showPlacement error"];
    }
  } else {
    [TapjoyModule rejectPromise:reject withMessage:@"Tapjoy is not connected"];
  }
}

RCT_EXPORT_METHOD(requestContent:(NSString *)placementName resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  if ([Tapjoy isConnected]) {
    TJPlacement *placement = [placementMap objectForKey:placementName];

    TapjoyPlacementListener* listener = [placementListenerMap objectForKey:placementName];

    if (listener != nil) {
      [listener setRequestPromiseResolve:resolve];
      [listener setRequestPromiseReject:reject];
    }

    [placement requestContent];
  } else {
    [TapjoyModule rejectPromise:reject withMessage:@"Tapjoy is not connected"];
  }
}

RCT_EXPORT_METHOD(getCurrencyBalance:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  if ([Tapjoy isConnected]) {
    [Tapjoy getCurrencyBalanceWithCompletion:^(NSDictionary *parameters, NSError *error) {
      if (error != nil) {
        [TapjoyModule rejectPromise:reject withError:error];
      } else {
        resolve(@{
                  @"amount": parameters[@"amount"],
                  @"currencyName": parameters[@"currencyName"],
                  @"currencyID": parameters[@"currencyID"]
                });
      }
    }];
  } else {
    [TapjoyModule rejectPromise:reject withMessage:@"Tapjoy is not connected"];
  }
}

RCT_EXPORT_METHOD(spendCurrencyAction:(nonnull NSNumber*)amount resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  [Tapjoy getCurrencyBalanceWithCompletion:^(NSDictionary *parameters, NSError *error) {
    if (error != nil) {
      [TapjoyModule rejectPromise:reject withError:error];
    } else {
      NSNumber *balance = parameters[@"amount"];

      if (balance.integerValue - amount.integerValue >= 0) {
        [Tapjoy spendCurrency:amount.integerValue completion:^(NSDictionary *parameters, NSError *error) {
          if (error) {
            [TapjoyModule rejectPromise:reject withError:error];
          } else {
            resolve(nil);
          }
        }];
      } else {
        [TapjoyModule rejectPromise:reject withMessage:@"Not enough currency"];
      }
    }
  }];
}

RCT_EXPORT_METHOD(listenForEarnedCurrency:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  [Tapjoy trackEvent:TJC_CURRENCY_EARNED_NOTIFICATION category:nil parameter1:nil parameter2:nil];

  if ([Tapjoy isConnected]) {
    // Add an observer for when a user has successfully earned currency.
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(showEarnedCurrencyAlert:)
                                                 name:TJC_CURRENCY_EARNED_NOTIFICATION
                                               object:nil];
    resolve(@"Listening for earned currency.");
  } else {
    [TapjoyModule rejectPromise:reject withMessage:@"Tapjoy is not connected"];
  }
}

- (void)showEarnedCurrencyAlert:(NSNotification*)notifyObj
{
  [self sendJSEvent:TJ_EVENT_EARNED_CURRENCY props: @{
                                                      @"currencyName": @"",
                                                      @"amount": notifyObj.object
                                                    }];

  // Pops up a UIAlert notifying the user that they have successfully earned some currency.
  // This is the default alert, so you may place a custom alert here if you choose to do so.
  //[Tapjoy showDefaultEarnedCurrencyAlert];
}

- (void) sendJSEvent:(NSString *)title props:(NSDictionary *)props
{
  @try {
      [self sendEventWithName:title
                         body:props];
  }
  @catch (NSException *err) {
    NSLog(@"An error occurred in sendJSEvent: %@", [err debugDescription]);
  }
}

-(void)tjcConnectSuccess:(NSNotification*)notifyObj
{
  if (mResolve != nil) {
    mResolve(@{
               @"info": @"Tapjoy iOS connect success."
               });
    mResolve = nil;
  }
}

- (void)tjcConnectFail:(NSNotification*)notifyObj
{
  if (mReject != nil) {
      [TapjoyModule rejectPromise:mReject withMessage:@"Tapjoy iOS connect failure."];
  }
  mReject = nil;
}

- (NSArray<NSString *> *)supportedEvents {
  return @[TJ_EVENT_EARNED_CURRENCY, TJ_EVENT_PLACEMENT_DISMISS, TJ_EVENT_PLACEMENT_CONTENT_READY, TJ_EVENT_PLACEMENT_REWARD_REQUEST, TJ_EVENT_PLACEMENT_PURCHASE_REQUEST];
}

- (NSDictionary *)constantsToExport
{
  return @{ @"events" : [self supportedEvents] };
};

+ (BOOL)requiresMainQueueSetup
{
  return NO;  // only do this if your module initialization relies on calling UIKit!
}

#pragma mark - Helper Fuctions

+ (void)rejectPromise:(RCTPromiseRejectBlock)reject withError:(NSError *)error {
  reject([NSString stringWithFormat:@"%ld", error.code], error.localizedDescription, error);
}

+ (void)rejectPromise:(RCTPromiseRejectBlock)reject withMessage:(NSString *)message {
  reject([NSString stringWithFormat:@"%@", message], message, nil);
}

@end
