package de.reimerm.tapjoy;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.tapjoy.TJPlacement;
import com.tapjoy.Tapjoy;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

/**
 * Created by marius on 20/02/17.
 */

public class TapjoyModule extends ReactContextBaseJavaModule {

    public static final String EVENT_EARNED_CURRENCY = "earnedCurrency";
    public static final String E_LAYOUT_INFO = "info";
    public static final String E_LAYOUT_ERROR = "error";
    public static final String EARNED_CURRENCY_NAME = "currency";
    public static final String EARNED_CURRENCY_VALUE = "value";
    public static final String CURRENCY_BALANCE_NAME = "currencyBalance";
    public static final String CURRENCY_BALANCE_VALUE = "value";

    private Map<String, TJPlacement> placementMap = new HashMap<>();

    public TapjoyModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void initialise(String sdkKey, boolean debug, Callback callback) {
        MyTJConnectListener tjConnectListener = new MyTJConnectListener(callback);
        Tapjoy.connect(getCurrentActivity(), sdkKey, new Hashtable(), tjConnectListener);
        Tapjoy.setDebugEnabled(debug);
    }

    @ReactMethod
    public void listenForEarnedCurrency(Callback callback) {
        if (Tapjoy.isConnected()) {
            Tapjoy.setEarnedCurrencyListener(new MyTJEarnedCurrencyListener(getReactApplicationContext()));
        } else {
            responseNotConnected(callback);
        }
    }

    @ReactMethod
    public void isConnected(Callback callback) {
        if (Tapjoy.isConnected()) {
            responseConnected(callback);
        } else {
            responseNotConnected(callback);
        }
    }

    @ReactMethod
    public void getCurrencyBalance(final Callback callback) {
        if (Tapjoy.isConnected()) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    Tapjoy.getCurrencyBalance(new MyTJGetCurrencyBalanceListener(callback));
                    super.run();
                }
            });
        } else {
            responseNotConnected(callback);
        }
    }

    private void responseNotConnected(Callback callback) {
        WritableMap responseMap = Arguments.createMap();
        responseMap.putString(E_LAYOUT_ERROR, "Tapjoy is not connected.");
        callback.invoke(responseMap);
    }

    private void responseConnected(Callback callback) {
        WritableMap responseMap = Arguments.createMap();
        responseMap.putBoolean(E_LAYOUT_INFO, true);
        callback.invoke(responseMap);
    }

    @ReactMethod
    public void addPlacement(String placementName, Callback callback) {
        if (Tapjoy.isConnected()) {
            placementMap.put(placementName, new TJPlacement(getReactApplicationContext(), placementName, new MyTJPlacementListener(getReactApplicationContext(), placementName)));
        } else {
            responseNotConnected(callback);
        }
    }

    @ReactMethod
    public void showPlacement(String placementName, Callback callback) {
        if (Tapjoy.isConnected()) {
            TJPlacement placement = placementMap.get(placementName);
            if (placement == null) {
                return;
            }

            if (placement.isContentReady()) {
                placement.showContent();
            }
        } else {
            responseNotConnected(callback);
        }
    }

    @ReactMethod
    public void requestContent(String placementName, Callback callback) {
        if (Tapjoy.isConnected()) {
            TJPlacement placement = placementMap.get(placementName);
            if (placement == null) {
                return;
            }

            placement.requestContent();
        } else {
            responseNotConnected(callback);
        }
    }

    @ReactMethod
    public void registerForPushNotifications(String gcmSenderId) {
        Tapjoy.setGcmSender(gcmSenderId);
    }

    @ReactMethod
    public void setDebugEnabled(boolean debugEnabled) {
        Tapjoy.setDebugEnabled(debugEnabled);
    }

    @Override
    public String getName() {
        return "TapjoyModule";
    }
}
