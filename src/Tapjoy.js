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
    this._eventHandlers = {};
    this._options = options;

    this._tapjoy = TapjoyModule;
    this._constants = TapjoyModule.getConstants();
  }

  get constants() {
    return this._constants;
  }

  setUserId(userId: number) {
    return this._tapjoy.setUserId(userId);
  }

  initialise() {
    if (Platform.OS === 'android' && this._options.gcmSenderIdAndroid != null) {
      this._tapjoy.registerForPushNotifications(
        this._options.gcmSenderIdAndroid,
      );
    }

    const sdkKey =
      Platform.OS === 'ios'
        ? this._options.sdkKeyIos
        : this._options.sdkKeyAndroid;

    return this._tapjoy.initialise(sdkKey, this._options.debug);
  }

  spendCurrency(amount: number) {
    return this._tapjoy.spendCurrencyAction(amount);
  }

  requestContent(name: string) {
    return this._tapjoy.requestContent(name);
  }

  isConnected() {
    return this._tapjoy.isConnected();
  }

  addPlacement(name: string) {
    return this._tapjoy.addPlacement(name);
  }

  showPlacement(name: string) {
    return this._tapjoy.showPlacement(name);
  }

  getCurrencyBalance() {
    return this._tapjoy.getCurrencyBalance();
  }

  listenForEarnedCurrency(callback) {
    this._eventHandlers.earnedCurrency = TapjoyModuleEvt.addListener(
      'earnedCurrency',
      callback,
    );

    return this._tapjoy.listenForEarnedCurrency();
  }

  // Event handler
  // proxy to the tapjoy instance
  _on(name: string, cb: () => {}) {
    const oldSub = this._eventHandlers[name];

    if (oldSub) {
      oldSub.remove();
      this._eventHandlers[name] = null;
    }

    this._eventHandlers[name] = TapjoyModuleEvt.addListener(name, cb);

    return this._eventHandlers[name];
  }
}
