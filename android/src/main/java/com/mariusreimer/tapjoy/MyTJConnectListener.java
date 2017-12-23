package com.mariusreimer.tapjoy;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.tapjoy.TJConnectListener;

/**
 * Created by mariu on 04.01.2017.
 */

public class MyTJConnectListener implements TJConnectListener {

    private Callback callback;
    private WritableMap responseMap = Arguments.createMap();

    public MyTJConnectListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onConnectSuccess() {
        responseMap.putString(TapjoyModule.E_LAYOUT_INFO, "Tapjoy Android connect success.");
        callback.invoke(null, responseMap);
    }

    @Override
    public void onConnectFailure() {
        responseMap.putString(TapjoyModule.E_LAYOUT_ERROR, "Tapjoy Android connect failure.");
        callback.invoke(responseMap);
    }
}
