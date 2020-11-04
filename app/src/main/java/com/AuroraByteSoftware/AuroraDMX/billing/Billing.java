package com.AuroraByteSoftware.AuroraDMX.billing;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.io.Serializable;
import java.util.List;

/**
 * In app purchases from https://developer.android.com/google/play/billing/billing_java_kotlin
 */
public class Billing implements PurchasesUpdatedListener, Serializable {
    private ClientStateListener clientStateListener;
    private BillingClient mBillingClient;
    private Context activity;


    public void setup(Context mActivity) {
        activity = mActivity;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        clientStateListener = new ClientStateListener(mBillingClient);
        mBillingClient.startConnection(clientStateListener);
        Log.d(getClass().getSimpleName(), "Billing setup finished");
    }

    public boolean check() {
        if (!clientStateListener.connect()) {
            Log.d(getClass().getSimpleName(), "Connection was lost, reconnecting");
        }
        Log.d(getClass().getSimpleName(), "Billing check: " + clientStateListener.getPurchaseStatus());
        if (clientStateListener.getPurchaseStatus() == ClientStateListener.NOT_PURCHASED) {
            return false;
        } else if (clientStateListener.getPurchaseStatus() == ClientStateListener.PURCHASED) {
            return true;
        }
        return true;
    }

    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, @Nullable List<Purchase> purchases) {
        Log.d(getClass().getSimpleName(), "onPurchasesUpdated " + responseCode + " " + purchases);
        Toast.makeText(activity, getBillingResponseMessage(responseCode), Toast.LENGTH_SHORT).show();

    }

    private String getBillingResponseMessage(@BillingClient.BillingResponse int responseCode) {
        switch (responseCode) {
            case BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED:
                return "Feature not supported";
            case BillingClient.BillingResponse.SERVICE_DISCONNECTED:
                return "Service Disconnected";
            case BillingClient.BillingResponse.OK:
                return "Success";
            case BillingClient.BillingResponse.USER_CANCELED:
                return "Store Canceled";
            case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
                return "Service Unavailable";
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
                return "Store Unavailable";
            case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
                return "Item Unavailable";
            case BillingClient.BillingResponse.DEVELOPER_ERROR:
                return "Developer error";
            case BillingClient.BillingResponse.ERROR:
                return "Store Error";
            case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
                return "Item already owned";
            case BillingClient.BillingResponse.ITEM_NOT_OWNED:
                return "Item not owned";
            default:
                return "Store Message " + responseCode;
        }
    }

    public BillingClient getBillingClient() {
        return mBillingClient;
    }
}
