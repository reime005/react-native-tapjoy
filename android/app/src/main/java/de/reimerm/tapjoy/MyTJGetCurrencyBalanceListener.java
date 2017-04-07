package de.reimerm.tapjoy;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.tapjoy.TJGetCurrencyBalanceListener;

/**
 * Created by marius on 01/03/17.
 */

public class MyTJGetCurrencyBalanceListener implements TJGetCurrencyBalanceListener {

    private Callback callback;

    public MyTJGetCurrencyBalanceListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onGetCurrencyBalanceResponse(String s, int i) {
        WritableMap responseMap = Arguments.createMap();

        responseMap.putString(TapjoyModule.CURRENCY_BALANCE_NAME, s);
        responseMap.putInt(TapjoyModule.CURRENCY_BALANCE_VALUE, i);

        if (callback != null) {
            callback.invoke(null, responseMap);
            callback = null;
        }
    }

    @Override
    public void onGetCurrencyBalanceResponseFailure(String s) {
        WritableMap responseMap = Arguments.createMap();
        responseMap.putString(TapjoyModule.E_LAYOUT_ERROR, s);
        try {
            callback.invoke(responseMap);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
