package com.mariusreimer.tapjoy;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJSetUserIDListener;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

/**
 * Created by marius on 20/02/17.
 */

public class TapjoyModule extends ReactContextBaseJavaModule {

    public static final String E_LAYOUT_INFO = "info";
    public static final String E_LAYOUT_ERROR = "error";

    public static final String EARNED_CURRENCY_NAME = "currencyName";
    public static final String EARNED_CURRENCY_VALUE = "amount";
    public static final String CURRENCY_BALANCE_NAME = "currencyName";
    public static final String CURRENCY_BALANCE_VALUE = "amount";
    public static final String TAPJOY_PLACEMENT_ADDED = "Tapjoy placement added.";
    public static final String TAPJOY_IS_CONNECTED = "Tapjoy is connected.";
    public static final String TAPJOY_IS_NOT_CONNECTED = "Tapjoy is not connected.";
    public static final String NOT_ENOUGH_CURRENCY = "Not enough currency";
    public static final String TAPJOY_PLACEMENT_NOT_CREATED = "Placement not created.";

    public static final String TAPJOY_PLACEMENT_REQUEST_SUCCESS = "Placement request succeeded.";
    public static final String TAPJOY_PLACEMENT_REQUEST_FAILURE = "Placement request failure.";

    public static final String TAPJOY_PLACEMENT_CONTENT_READY = "Placement content is ready.";
    public static final String TAPJOY_PLACEMENT_CONTENT_NOT_READY = "Placement content not ready.";

    public static final String TAPJOY_SHOWING_PLACEMENT = "Showing Placement.";
    public static final String TAPJOY_PLACEMENT_CONTENT_AVAILABLE = "Placement content available.";

    public enum Events {
        EVENT_PLACEMENT_EARNED_CURRENCY("earnedCurrency"),
        EVENT_PLACEMENT_DISMISS("onPlacementDismiss"),
        EVENT_PLACEMENT_CONTENT_READY("onPlacementContentReady"),
        EVENT_PLACEMENT_CLICK("onPlacementClick"),
        EVENT_PLACEMENT_PURCHASE_REQUEST("onPurchaseRequest"),
        EVENT_PLACEMENT_REWARD_REQUEST("onRewardRequest");


        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private Map<String, TJPlacement> placementMap = new HashMap<>();

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        List<String> events = new ArrayList<>();

        for (Object event : Events.values()) {
            events.add(event.toString());
        }

        builder.put("events", events);
        return builder.build();
    }

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
            promiseResolve(promise, "Listening for earned currency.");
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
            placementMap.put(placementName, new TJPlacement(getReactApplicationContext(), placementName, new MyTJPlacementListener(getReactApplicationContext(), placementName)));
            promiseResolve(promise, TAPJOY_PLACEMENT_ADDED);
        } else {
            responseNotConnected(promise);
        }
    }

    @ReactMethod
    public void showPlacement(String placementName, final Promise promise) {
        if (Tapjoy.isConnected()) {
            TJPlacement placement = placementMap.get(placementName);

            if (placement == null) {
                promiseReject(promise, TAPJOY_PLACEMENT_NOT_CREATED);
                return;
            }

            if (placement.isContentReady()) {
                ((MyTJPlacementListener)placement.getListener()).setShowPromise(promise);

                placement.showContent();
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
           TJPlacement placement = placementMap.get(placementName);

            if (placement == null) {
                promiseReject(promise, TAPJOY_PLACEMENT_NOT_CREATED);
                return;
            } else if (placement.isContentReady()) {
                promise.resolve(TAPJOY_PLACEMENT_CONTENT_READY);
                return;
            }

            if (placement.isContentAvailable()) {
                promiseReject(promise, TAPJOY_PLACEMENT_CONTENT_AVAILABLE);
                return;
            }

            final MyTJPlacementListener listener = (MyTJPlacementListener)placement.getListener();

            if (listener.getRequestPromise() != null) {
                promiseReject(promise, "Placement is already requesting content.");
                return;
            }

            listener.setRequestPromise(promise);

            placement.requestContent();
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
