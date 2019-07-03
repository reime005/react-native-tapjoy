/**
 * Created by marius on 06/03/17.
 * @flow
 * @format
 */

import {NativeEventEmitter, AsyncStorage, NativeModules, Platform} from 'react-native';

import PropTypes from 'prop-types';
import { useTapjoy } from './useTapjoy';

const {TapjoyModule} = NativeModules;
const TapjoyModuleEvt = new NativeEventEmitter(TapjoyModule);

export class Tapjoy {
    constructor(options) {
        this.eventHandlers = {};
        this.options = options;
    }

    setUserId(userId: number) {
        return TapjoyModule.setUserId(userId);
    }

    async initialise() {
        if (Platform.OS === 'android' && this.options.gcmSenderIdAndroid != null) {
            TapjoyModule.registerForPushNotifications(this.options.gcmSenderIdAndroid);
        }

        const sdkKey = Platform.OS === 'ios' ? this.options.sdkKeyIos : this.options.sdkKeyAndroid;

        return TapjoyModule.initialise(sdkKey, this.options.debug);
    }

    spendCurrency(amount: number) {
        return TapjoyModule.spendCurrencyAction(amount);
    }

    requestContent(name: string) {
        return TapjoyModule.requestContent(name);
    }

    isConnected() {
        return Tapjoy.isConnected();
    }

    addPlacement(name: string, callback: () => {}) {
        const oldSub = this.eventHandlers[name];
        if (oldSub) {
            oldSub.remove();
            this.eventHandlers[name] = null;
        }

        const sub = this._on(name, callback, TapjoyModuleEvt);

        return TapjoyModule.addPlacement(name);
    }

    showPlacement(name: string) {
        return TapjoyModule.showPlacement(name);
    }

    getCurrencyBalance() {
        return TapjoyModule.getCurrencyBalance();
    }

    listenForEarnedCurrency(callback: (earned: number) => {}) {
        const oldSub = this.eventHandlers.earnedCurrency;
        if (oldSub) {
            oldSub.remove();
            this.eventHandlers.earnedCurrency = null;
        }

        const sub = this._on('earnedCurrency', callback, TapjoyModuleEvt);
        return TapjoyModule.listenForEarnedCurrency(callback);
    }

    _addConstantExports(constants: string[]) {
        Object.keys(constants).forEach(name => {
            TapjoyModule[name] = constants[name];
        });
    }

    // _addToTapjoyInstance(...methods) {
    //     methods.forEach(name => {
    //         this.tapjoy[name] = this[name].bind(this);
    //     })
    // }

    // whenReady(fn) {
    //     return this.tapjoy.configurePromise.then(fn);
    // }

    // Event handler
    // proxy to the tapjoy instance
    _on(name: string, cb: () => {}) {
        return new Promise((resolve) => {
            const sub = TapjoyModuleEvt.addListener(name, cb);
            this.eventHandlers[name] = sub;
            resolve(sub);
        })
    }
}

export default Tapjoy

export { useTapjoy }
