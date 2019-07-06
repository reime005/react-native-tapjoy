package com.mariusreimer.tapjoy;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.tapjoy.TJConnectListener;

/**
 * Created by mariu on 04.01.2017.
 */

public class MyTJConnectListener implements TJConnectListener {

    private Promise promise;

    MyTJConnectListener(Promise promise) {
        this.promise = promise;
    }

    @Override
    public void onConnectSuccess() {
        promise.resolve("Tapjoy Android connect success.");
    }

    @Override
    public void onConnectFailure() {
        promise.reject(TapjoyModule.E_LAYOUT_ERROR, "Tapjoy Android connect failure.");
    }
}
