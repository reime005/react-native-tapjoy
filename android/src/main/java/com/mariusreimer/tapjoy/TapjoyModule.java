package com.mariusreimer.tapjoy;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJSetUserIDListener;
import com.tapjoy.TJSpendCurrencyListener;
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
    public static final String TAPJOY_IS_NOT_CONNECTED = "Tapjoy is not connected.";
    public static final String NOT_ENOUGH_CURRENCY = "Not enough currency";

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
    public void setUserId(final String userId, final Promise promise) {
        if (!Tapjoy.isConnected()) {
            promiseReject(promise, E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
            return;
        }

        Tapjoy.setUserID(userId, new TJSetUserIDListener() {
            @Override
            public void onSetUserIDSuccess() {
                promiseResolve(promise, null);
            }

            @Override
            public void onSetUserIDFailure(String s) {
                promiseReject(promise, E_LAYOUT_ERROR, s);
            }
        });
    }

    @ReactMethod
    public void spendCurrencyAction(final int amount, final Promise promise) {
        if (!Tapjoy.isConnected()) {
            promiseReject(promise, E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
            return;
        }

        Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener() {

            @Override
            public void onGetCurrencyBalanceResponse(String s, int i) {
                if (i - amount >= 0) {
                    try {
                        Tapjoy.spendCurrency(amount, new TJSpendCurrencyListener() {
                            @Override
                            public void onSpendCurrencyResponse(String s, int i) {
                                promiseResolve(promise, null);
                            }

                            @Override
                            public void onSpendCurrencyResponseFailure(String s) {
                                promiseReject(promise, E_LAYOUT_ERROR, s);
                            }
                        });
                    } catch (IllegalViewOperationException e) {
                        promiseReject(promise, E_LAYOUT_ERROR, e.getLocalizedMessage());
                    }
                } else {
                    promiseReject(promise, E_LAYOUT_ERROR, NOT_ENOUGH_CURRENCY);
                }
            }

            @Override
            public void onGetCurrencyBalanceResponseFailure(String s) {

            }
        });
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
    public void getCurrencyBalance(final Promise promise) {
        if (Tapjoy.isConnected()) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    Tapjoy.getCurrencyBalance(new MyTJGetCurrencyBalanceListener(promise));
                    super.run();
                }
            });
        } else {
            promiseReject(promise, E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
        }
    }

    private void responseNotConnected(Callback callback) {
        WritableMap responseMap = Arguments.createMap();
        responseMap.putString(E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
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
    public void showPlacement(String placementName, final Promise promise) {
        if (Tapjoy.isConnected()) {
            TJPlacement placement = placementMap.get(placementName);
            if (placement == null) {
                return;
            }

            if (placement.isContentReady()) {
                placement.showContent();
            }

            promiseResolve(promise, null);
        } else {
            promiseReject(promise, E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
        }
    }

    @ReactMethod
    public void requestContent(String placementName, final Promise promise) {
        if (Tapjoy.isConnected()) {
            TJPlacement placement = placementMap.get(placementName);
            if (placement == null) {
                return;
            }

            placement.requestContent();

            promiseResolve(promise, null);
        } else {
            promiseReject(promise, E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
        }
    }

    public static void promiseReject(final Promise promise, String code, String message) {
        try {
            promise.reject(code, message);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void promiseResolve(final Promise promise, final Object value) {
        try {
            promise.resolve(value);
        } catch (RuntimeException e) {
            e.printStackTrace();
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
