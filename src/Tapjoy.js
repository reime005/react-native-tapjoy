/**
 * Created by marius on 06/03/17.
 * @flow
 * @format
 */

import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const { TapjoyModule } = NativeModules;
const TapjoyModuleEvt = new NativeEventEmitter(TapjoyModule);

export class Tapjoy {
  constructor(options) {
    this.eventHandlers = {};
    this.options = options;

    this.constants = TapjoyModule.getConstants();
  }

  static setUserId(userId: number) {
    return TapjoyModule.setUserId(userId);
  }

  async initialise() {
    if (Platform.OS === 'android' && this.options.gcmSenderIdAndroid != null) {
      TapjoyModule.registerForPushNotifications(
        this.options.gcmSenderIdAndroid,
      );
    }

    const sdkKey =
      Platform.OS === 'ios'
        ? this.options.sdkKeyIos
        : this.options.sdkKeyAndroid;

    return TapjoyModule.initialise(sdkKey, this.options.debug);
  }

  static spendCurrency(amount: number) {
    return TapjoyModule.spendCurrencyAction(amount);
  }

  static requestContent(name: string) {
    return TapjoyModule.requestContent(name);
  }

  static isConnected() {
    return Tapjoy.isConnected();
  }

  static addPlacement(name: string) {
    return TapjoyModule.addPlacement(name);
  }

  static showPlacement(name: string) {
    return TapjoyModule.showPlacement(name);
  }

  static getCurrencyBalance() {
    return TapjoyModule.getCurrencyBalance();
  }

  listenForEarnedCurrency(callback) {
    this.eventHandlers.earnedCurrency = TapjoyModuleEvt.addListener(
      'earnedCurrency',
      callback,
    );

    return TapjoyModule.listenForEarnedCurrency();
  }

  // Event handler
  // proxy to the tapjoy instance
  _on(name: string, cb: () => {}) {
    const oldSub = this.eventHandlers[name];

    if (oldSub) {
      oldSub.remove();
      this.eventHandlers[name] = null;
    }

    this.eventHandlers[name] = TapjoyModuleEvt.addListener(name, cb);

    return this.eventHandlers[name];
  }
}
