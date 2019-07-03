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
    public static final String TAPJOY_PLACEMENT_ADDED = "Tapjoy placement added.";
    public static final String TAPJOY_IS_CONNECTED = "Tapjoy is connected.";
    public static final String TAPJOY_IS_NOT_CONNECTED = "Tapjoy is not connected.";
    public static final String NOT_ENOUGH_CURRENCY = "Not enough currency";
    public static final String TAPJOY_PLACEMENT_NOT_CREATED = "Placement not created.";

    public static final String TAPJOY_PLACEMENT_CONTENT_READY = "Placement content not ready.";

    public static final String TAPJOY_PLACEMENT_CONTENT_NOT_READY = "Placement content not ready.";
    public static final String TAPJOY_SHOWING_PLACEMENT = "Showing Placement";

    class Key {
        public Key(Promise promise, TJPlacement placement) {
            this.promise = promise;
            this.placement = placement;
        }

        Promise promise;
        TJPlacement placement;
    }

    private Map<String, Key> placementMap = new HashMap<>();

    public TapjoyModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void initialise(String sdkKey, boolean debug, final Promise promise) {
        MyTJConnectListener tjConnectListener = new MyTJConnectListener(promise);
        Tapjoy.connect(getReactApplicationContext(), sdkKey, new Hashtable(), tjConnectListener);
        Tapjoy.setDebugEnabled(debug);
    }

    @ReactMethod
    public void setUserId(final String userId, final Promise promise) {
        if (!Tapjoy.isConnected()) {
            promiseReject(promise, TAPJOY_IS_NOT_CONNECTED);
            return;
        }

        Tapjoy.setUserID(userId, new TJSetUserIDListener() {
            @Override
            public void onSetUserIDSuccess() {
                promiseResolve(promise, null);
            }

            @Override
            public void onSetUserIDFailure(String s) {
                promiseReject(promise, s);
            }
        });
    }

    @ReactMethod
    public void spendCurrencyAction(final int amount, final Promise promise) {
        if (!Tapjoy.isConnected()) {
            promiseReject(promise, TAPJOY_IS_NOT_CONNECTED);
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
                                promiseReject(promise, s);
                            }
                        });
                    } catch (IllegalViewOperationException e) {
                        promiseReject(promise, e.getLocalizedMessage());
                    }
                } else {
                    promiseReject(promise, NOT_ENOUGH_CURRENCY);
                }
            }

            @Override
            public void onGetCurrencyBalanceResponseFailure(String s) {

            }
        });
    }

    @ReactMethod
    public void listenForEarnedCurrency(final Promise promise) {
        if (Tapjoy.isConnected()) {
            Tapjoy.setEarnedCurrencyListener(new MyTJEarnedCurrencyListener(getReactApplicationContext()));
        } else {
            responseNotConnected(promise);
        }
    }

    @ReactMethod
    public void isConnected(final Promise promise) {
        if (Tapjoy.isConnected()) {
            responseConnected(promise);
        } else {
            responseNotConnected(promise);
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
            promiseReject(promise, TAPJOY_IS_NOT_CONNECTED);
        }
    }

    private void responseNotConnected(final Promise promise) {
        WritableMap responseMap = Arguments.createMap();
        responseMap.putString(E_LAYOUT_ERROR, TAPJOY_IS_NOT_CONNECTED);
        promiseReject(promise, TAPJOY_IS_NOT_CONNECTED);
    }

    private void responseConnected(final Promise promise) {
        WritableMap responseMap = Arguments.createMap();
        responseMap.putBoolean(E_LAYOUT_INFO, true);
        promiseResolve(promise, responseMap);
    }

    @ReactMethod
    public void addPlacement(String placementName, final Promise promise) {
        if (Tapjoy.isConnected()) {
            placementMap.put(placementName, new Key(null, new TJPlacement(getReactApplicationContext(), placementName, new MyTJPlacementListener(getReactApplicationContext(), placementName))));
            promiseResolve(promise, TAPJOY_PLACEMENT_ADDED);
        } else {
            responseNotConnected(promise);
        }
    }

    @ReactMethod
    public void showPlacement(String placementName, final Promise promise) {
        if (Tapjoy.isConnected()) {
            TJPlacement placement = placementMap.get(placementName).placement;
            if (placement == null) {
                promiseReject(promise, TAPJOY_PLACEMENT_NOT_CREATED);
                return;
            }

            if (placement.isContentReady()) {
                placement.showContent();
                promiseResolve(promise, TAPJOY_SHOWING_PLACEMENT);
            } else {
                promiseReject(promise, TAPJOY_PLACEMENT_CONTENT_NOT_READY);
            }
        } else {
            promiseReject(promise, TAPJOY_IS_NOT_CONNECTED);
        }
    }

    @ReactMethod
    public void requestContent(String placementName, final Promise promise) {
        if (Tapjoy.isConnected()) {
            Key key = placementMap.get(placementName);
            if (key == null) {
                promiseReject(promise, "Placement not created.");
                return;
            } else if (key.placement.isContentReady()) {
                promise.resolve(TAPJOY_PLACEMENT_CONTENT_READY);
                return;
            }

            final MyTJPlacementListener listener = (MyTJPlacementListener)key.placement.getListener();

            if (listener.getPromise() != null) {
                promiseReject(promise, "Placement is already requesting content.");
                return;
            }



//TODO isContentAvailable

            key.placement.requestContent();

            ((MyTJPlacementListener)key.placement.getListener()).setPromise(promise);
        } else {
            promiseReject(promise, TAPJOY_IS_NOT_CONNECTED);
        }
    }

    static void promiseReject(final Promise promise, String message) {
        try {
            promise.reject(E_LAYOUT_ERROR, message);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    static void promiseResolve(final Promise promise, final Object value) {
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
