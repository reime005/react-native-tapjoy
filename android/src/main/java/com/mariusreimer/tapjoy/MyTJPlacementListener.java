package com.mariusreimer.tapjoy;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;

/**
 * Created by mariu on 29.12.2016.
 */

public class MyTJPlacementListener implements TJPlacementListener {

    private final ReactContext reactContext;
    private final String placementName;
    private static final String TAG = "TapjoyPlacementListener";

    private Promise requestPromise;
    private Promise showPromise;

    MyTJPlacementListener(ReactContext reactContext, String placementName) {
        this.reactContext = reactContext;
        this.placementName = placementName;
    }

    @Override
    // This just means the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestSuccess(TJPlacement tjPlacement) {
        if (requestPromise != null) {
            requestPromise.resolve(TapjoyModule.TAPJOY_PLACEMENT_REQUEST_SUCCESS);
            requestPromise = null;
        }
    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
        if (requestPromise != null) {
            requestPromise.reject(TapjoyModule.TAPJOY_PLACEMENT_REQUEST_FAILURE, tjError.message);
            requestPromise = null;
        }
    }

    @Override
    //This is called when the content is actually available to display.
    public void onContentReady(TJPlacement tjPlacement) {
        event(TapjoyModule.Events.EVENT_PLACEMENT_CONTENT_READY.toString(), tjPlacement.getName());
    }

    public Promise getRequestPromise() {
        return requestPromise;
    }

    public void setRequestPromise(Promise requestPromise) {
        this.requestPromise = requestPromise;
    }

    public void setShowPromise(Promise showPromise) {
        this.showPromise = showPromise;
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {
        if (showPromise != null) {
            showPromise.resolve(TapjoyModule.TAPJOY_SHOWING_PLACEMENT);
            showPromise = null;
        }
    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {
        event(TapjoyModule.Events.EVENT_PLACEMENT_DISMISS.toString(), tjPlacement.getName());
    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String productId) {
        onPurchaseRequestEvent(TapjoyModule.Events.EVENT_PLACEMENT_PURCHASE_REQUEST.toString(), tjActionRequest.getRequestId(), tjActionRequest.getToken(), productId, tjPlacement.getName());
    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String itemId, int quantity) {
        onRewardRequestEvent(TapjoyModule.Events.EVENT_PLACEMENT_REWARD_REQUEST.toString(), tjActionRequest.getRequestId(), tjActionRequest.getToken(), itemId, quantity, tjPlacement.getName());
    }

    @Override
    public void onClick(TJPlacement tjPlacement) {
        event(TapjoyModule.Events.EVENT_PLACEMENT_CLICK.toString(), tjPlacement.getName());
    }

    private void fireEvent(String eventName, WritableMap resp) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, resp);
        } else {
            Log.d(TAG, "Waiting for CatalystInstance before sending event.");
        }
    }

    private void event(String eventName, String placementName) {
        final WritableMap responseMap = Arguments.createMap();
        if (placementName != null) {
            responseMap.putString("placementName", placementName);
        }
        fireEvent(eventName, responseMap);
    }

    private void onPurchaseRequestEvent(String eventName, String requestId, String token, String productId, String placementName) {
        final WritableMap responseMap = Arguments.createMap();
        responseMap.putString("placementName", placementName);
        responseMap.putString("requestId", requestId);
        responseMap.putString("token", token);
        responseMap.putString("productId", productId);
        fireEvent(eventName, responseMap);
    }

    private void onRewardRequestEvent(String eventName, String requestId, String token, String itemId, int quantity, String placementName) {
        final WritableMap responseMap = Arguments.createMap();
        responseMap.putString("placementName", placementName);
        responseMap.putString("requestId", requestId);
        responseMap.putString("token", token);
        responseMap.putString("itemId", itemId);
        responseMap.putInt("quantity", quantity);
        fireEvent(eventName, responseMap);
    }
}
