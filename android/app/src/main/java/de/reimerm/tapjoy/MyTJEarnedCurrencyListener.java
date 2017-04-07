package de.reimerm.tapjoy;

/**
 * Created by marius on 01/03/17.
 */

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tapjoy.TJEarnedCurrencyListener;

public class MyTJEarnedCurrencyListener implements TJEarnedCurrencyListener {

    private final ReactContext reactContext;

    public MyTJEarnedCurrencyListener(ReactContext reactContext) {
        this.reactContext = reactContext;
    }

    @Override
    public void onEarnedCurrency(final String currencyName, final int amount) {
        final WritableMap responseMap = Arguments.createMap();

        responseMap.putString(TapjoyModule.EARNED_CURRENCY_NAME, currencyName);
        responseMap.putInt(TapjoyModule.EARNED_CURRENCY_VALUE, amount);

        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(TapjoyModule.EVENT_EARNED_CURRENCY, responseMap);
    }
}
