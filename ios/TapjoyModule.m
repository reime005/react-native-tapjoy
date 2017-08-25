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

RCT_EXPORT_METHOD(initialise:(NSString *)name debug:(BOOL)debug callback:(RCTResponseSenderBlock)callback) {
  m_callback = callback;
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
      NSLog(@"Tapjoy setUserIDWithCompletion error");
      [TapjoyModule rejectPromise:reject withError:error];
    } else {
      resolve(nil);
    }
  }];
}

RCT_EXPORT_METHOD(addPlacement:(NSString *)placementName callback:(RCTResponseSenderBlock)callback) {
  if ([Tapjoy isConnected]) {
    TapjoyPlacementListener *placementListener = [[TapjoyPlacementListener alloc] initWithPlacementName:placementName tapjoyModule:self];
    
    placementListenerMap = @{
                     placementName : placementListener
                     };
  
    TJPlacement *placement = [TJPlacement placementWithName:placementName delegate:placementListener ];
    
    if (placementMap == nil) {
      placementMap = @{
                     placementName : placement
              };
    }
    
    placementMap[placementName] = placement;
  } else {
    NSLog(@"Tapjoy is not connected.");
    NSDictionary *responseProps = @{
                                    @"error": @"Tapjoy iOS not connected."
                                    };
    callback(@[[NSNull null], responseProps]);
  }
}

RCT_EXPORT_METHOD(showPlacement:(NSString *)placementName callback:(RCTResponseSenderBlock)callback) {
  if ([Tapjoy isConnected]) {
      
    UIViewController *rootController =(UIViewController*)[[(AppDelegate*)
                                                           [[UIApplication sharedApplication]delegate] window] rootViewController];
    TJPlacement *placement = [placementMap objectForKey:placementName];
    
      
      NSArray *windows = [[UIApplication sharedApplication] windows];
      UIViewController *rootViewController = (windows.count > 0) ? [[windows objectAtIndex:0] rootViewController] : nil;
      if (rootViewController && placement) {
          dispatch_async(dispatch_get_main_queue(),  ^(void) {
	          [placement showContentWithViewController: rootViewController];
          });
      }
  } else {
    NSLog(@"Tapjoy is not connected");
    NSDictionary *responseProps = @{
                                    @"error": @"Tapjoy iOS not connected."
                                    };
    callback(@[[NSNull null], responseProps]);
  }
}

RCT_EXPORT_METHOD(requestContent:(NSString *)placementName callback:(RCTResponseSenderBlock)callback) {
  if ([Tapjoy isConnected]) {
    
    TJPlacement *placement = [placementMap objectForKey:placementName];
    [placement requestContent];
  } else {
    NSLog(@"Tapjoy is not connected");
    NSDictionary *responseProps = @{
                                    @"error": @"Tapjoy iOS not connected."
                                    };
    callback(@[[NSNull null], responseProps]);
  }
}

RCT_EXPORT_METHOD(getCurrencyBalance:(RCTResponseSenderBlock)callback) {
  NSLog(@"Tapjoy getCurrencyBalanceWithCompletion");
  if ([Tapjoy isConnected]) {
    [Tapjoy getCurrencyBalanceWithCompletion:^(NSDictionary *parameters, NSError *error) {
      if (error != nil) {
        NSLog(@"Tapjoy getCurrencyBalanceWithCompletion error");
      } else {
        NSLog(@"Tapjoy getCurrencyBalanceWithCompletion no error");
        
        NSDictionary *responseProps = @{
                                        @"value": parameters[@"amount"],
                                        @"currencyBalance": parameters[@"currencyName"]
                                        };
        callback(@[[NSNull null], responseProps]);
      }
    }];
  } else {
    NSLog(@"Tapjoy is not connected");
    NSDictionary *responseProps = @{
                                    @"error": @"Tapjoy iOS not connected."
                                    };
    callback(@[[NSNull null], responseProps]);
  }
}

RCT_EXPORT_METHOD(spendCurrencyAction:(nonnull NSNumber*)amount resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  NSLog(@"Tapjoy spendCurrencyAction");
  
  [Tapjoy getCurrencyBalanceWithCompletion:^(NSDictionary *parameters, NSError *error) {
    if (error != nil) {
      NSLog(@"Tapjoy getCurrencyBalanceWithCompletion error");
    } else {
      NSNumber *balance = parameters[@"amount"];
      
      if (balance.integerValue - amount.integerValue > 0) {
        [Tapjoy spendCurrency:amount.integerValue completion:^(NSDictionary *parameters, NSError *error) {
          if (error) {
            [TapjoyModule rejectPromise:reject withError:error];
          } else {
            resolve(nil);
          }
        }];
      } else {
        [TapjoyModule rejectPromise:reject withMessage:@"Not enough currency"];
        NSLog(@"Tapjoy spendCurrency error");
      }
    }
  }];
}

RCT_EXPORT_METHOD(listenForEarnedCurrency:(RCTResponseSenderBlock)callback) {
  [Tapjoy trackEvent:TJC_CURRENCY_EARNED_NOTIFICATION category:nil parameter1:nil parameter2:nil];
  
  NSLog(@"Tapjoy listenForEarnedCurrency");
  if ([Tapjoy isConnected]) {
    // Add an observer for when a user has successfully earned currency.
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(showEarnedCurrencyAlert:)
                                                 name:TJC_CURRENCY_EARNED_NOTIFICATION
                                               object:nil];
  } else {
    NSLog(@"Tapjoy is not connected");
    NSDictionary *responseProps = @{
                                    @"error": @"Tapjoy iOS not connected."
                                    };
    callback(@[[NSNull null], responseProps]);
  }
}

- (void)showEarnedCurrencyAlert:(NSNotification*)notifyObj
{
  NSNumber *currencyEarned = notifyObj.object;
  int earnedNum = [currencyEarned intValue];
  
  NSLog(@"Currency earned: %d", earnedNum);
  
  NSDictionary *evt = @{
                        @"currency": @"",
                        @"value": currencyEarned
                        };
  
  [self sendJSEvent:TJ_EARNED_CURRENCY_EVENT props: evt];
  
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
  NSDictionary *responseProps = @{
                                @"info": @"Tapjoy iOS connect success."
                                };
  NSLog(@"Tapjoy connect Succeeded");
  
  if (m_callback != nil) {
    m_callback(@[[NSNull null], responseProps]);
  }
  m_callback = nil;
}

- (void)tjcConnectFail:(NSNotification*)notifyObj
{
  NSDictionary *responseProps = @{
                                  @"error": @"Tapjoy iOS connect failure."
                                  };
  NSLog(@"Tapjoy connect Failed");
  
  if (m_callback != nil) {
    m_callback(@[responseProps]);
  }
  m_callback = nil;
}

- (NSArray<NSString *> *)supportedEvents {
  return @[TJ_EARNED_CURRENCY_EVENT, TJ_PLACEMENT_OFFERWALL_EVENT];
}

#pragma mark - Helper Fuctions

+ (void)rejectPromise:(RCTPromiseRejectBlock)reject withError:(NSError *)error {
  reject([NSString stringWithFormat:@"%ld", error.code], error.localizedDescription, error);
}

+ (void)rejectPromise:(RCTPromiseRejectBlock)reject withMessage:(NSString *)message {
  reject([NSString stringWithFormat:@"%@", message], message, nil);
}

@end
