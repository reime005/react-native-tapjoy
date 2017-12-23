package com.mariusreimer.tapjoy;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.tapjoy.TJGetCurrencyBalanceListener;

import static com.mariusreimer.tapjoy.TapjoyModule.E_LAYOUT_ERROR;

/**
 * Created by marius on 01/03/17.
 */

public class MyTJGetCurrencyBalanceListener implements TJGetCurrencyBalanceListener {

    private final Promise promise;

    public MyTJGetCurrencyBalanceListener(Promise promise) {
        this.promise = promise;
    }

    @Override
    public void onGetCurrencyBalanceResponse(String s, int i) {
        WritableMap responseMap = Arguments.createMap();

        responseMap.putString(TapjoyModule.CURRENCY_BALANCE_NAME, s);
        responseMap.putInt(TapjoyModule.CURRENCY_BALANCE_VALUE, i);

        TapjoyModule.promiseResolve(promise, responseMap);
    }

    @Override
    public void onGetCurrencyBalanceResponseFailure(String s) {
        TapjoyModule.promiseReject(promise, E_LAYOUT_ERROR, s);
    }
}
