package de.reimerm.tapjoy;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
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

    public static final String PLACEMENT_REQUEST_SUCCESS = "onRequestSuccess";
    public static final String PLACEMENT_REQUEST_FAILURE = "onRequestFailure";
    public static final String PLACEMENT_CONTENT_READY = "onContentReady";
    public static final String PLACEMENT_CONTENT_SHOW = "onContentShow";
    public static final String PLACEMENT_CONTENT_DISMISS = "onContentDismiss";
    public static final String PLACEMENT_PURCHASE_REQUEST = "onPurchaseRequest";
    public static final String PLACEMENT_REWARD_REQUEST = "onRewardRequest";

    public MyTJPlacementListener(ReactContext reactContext, String placementName) {
        this.reactContext = reactContext;
        this.placementName = placementName;
    }

    @Override
    // This just means the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestSuccess(TJPlacement tjPlacement) {
        event(PLACEMENT_REQUEST_SUCCESS);
    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
        failureEvent(PLACEMENT_REQUEST_FAILURE, tjError.message);
    }

    @Override
    //This is called when the content is actually available to display.
    public void onContentReady(TJPlacement tjPlacement) {
        event(PLACEMENT_CONTENT_READY);
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {
        event(PLACEMENT_CONTENT_SHOW);
    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {
        event(PLACEMENT_CONTENT_DISMISS);
    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String productId) {
        onPurchaseRequestEvent(PLACEMENT_PURCHASE_REQUEST, tjActionRequest.getRequestId(), tjActionRequest.getToken(), productId);
    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String itemId, int quantity) {
        onRewardRequestEvent(PLACEMENT_REWARD_REQUEST, tjActionRequest.getRequestId(), tjActionRequest.getToken(), itemId, quantity);
    }

    private void fireEvent(WritableMap resp) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(placementName, resp);
        } else {
            Log.d(TAG, "Waiting for CatalystInstance before sending event.");
        }
    }

    private void event(String eventName) {
        final WritableMap responseMap = Arguments.createMap();
        responseMap.putString(eventName, eventName);
        fireEvent(responseMap);
    }

    private void failureEvent(String eventName, String errorMessage) {
        final WritableMap responseMap = Arguments.createMap();
        responseMap.putString(eventName, eventName);
        responseMap.putString("errorMessage", errorMessage);
        fireEvent(responseMap);
    }

    private void onPurchaseRequestEvent(String eventName, String requestId, String token, String productId) {
        final WritableMap responseMap = Arguments.createMap();
        responseMap.putString(eventName, eventName);
        responseMap.putString("requestId", requestId);
        responseMap.putString("token", token);
        responseMap.putString("productId", productId);
        fireEvent(responseMap);
    }

    private void onRewardRequestEvent(String eventName, String requestId, String token, String itemId, int quantity) {
        final WritableMap responseMap = Arguments.createMap();
        responseMap.putString(eventName, eventName);
        responseMap.putString("requestId", requestId);
        responseMap.putString("token", token);
        responseMap.putString("itemId", itemId);
        responseMap.putInt("quantity", quantity);
        fireEvent(responseMap);
    }
}
