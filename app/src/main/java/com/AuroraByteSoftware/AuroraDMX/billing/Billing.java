package com.AuroraByteSoftware.AuroraDMX.billing;

import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.QueryProductDetailsParams;

import java.util.List;

/**
 * In app purchases from https://developer.android.com/google/play/billing/billing_java_kotlin
 */
public class Billing  {

    private Boolean purchaced = null;


        public static final String ITEM_SKU = "unlock_channels";
    private com.AuroraByteSoftware.AuroraDMX.billing.PurchasesUpdatedListener purchasesUpdatedListener = new com.AuroraByteSoftware.AuroraDMX.billing.PurchasesUpdatedListener();

    private BillingClient billingClient = null;

    /**
     * 0 - Unknown
     * 1 - purchased
     * 2 - not purchased
     * 3 - error
     */
    private int purchaseStatus = 0;
    public static int PURCHASED = 1;
    public static int NOT_PURCHASED = 2;
    public static int ERROR = 3;

    public void setup(Context mActivity) {
//        activity = mActivity;
//        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
//        clientStateListener = new ClientStateListener(mBillingClient);
//        mBillingClient.startConnection(clientStateListener);
//
//
        billingClient = BillingClient.newBuilder(mActivity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        //start billing
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    query();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

        Log.d(getClass().getSimpleName(), "Billing setup finished");



    }

    private void query() {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                (List<QueryProductDetailsParams.Product>) QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(ITEM_SKU)
                                        .setProductType(BillingClient.ProductType.SUBS)
                                        .build())
                        .build();
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    // check billingResult
                    // process returned productDetailsList
                    purchaced = billingResult.getResponseCode() == PURCHASED;
                    String responseMessage = getBillingResponseMessage(billingResult.getResponseCode());
                    Log.d(getClass().getSimpleName(), "Billing response " + responseMessage);
                    ;
                }
        );
    }

    public boolean check() {

        //
//        if (!clientStateListener.connect()) {
//            Log.d(getClass().getSimpleName(), "Connection was lost, reconnecting");
//        }
//        Log.d(getClass().getSimpleName(), "Billing check: " + clientStateListener.getPurchaseStatus());
//        if (clientStateListener.getPurchaseStatus() == ClientStateListener.NOT_PURCHASED) {
//            return false;
//        } else if (clientStateListener.getPurchaseStatus() == ClientStateListener.PURCHASED) {
//            return true;
//        }
//        return true;

        return purchaced;
    }
//
//        @Override
//    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, @Nullable List<Purchase> purchases) {
//        Log.d(getClass().getSimpleName(), "onPurchasesUpdated " + responseCode + " " + purchases);
//        Toast.makeText(activity, getBillingResponseMessage(responseCode), Toast.LENGTH_SHORT).show();
//
//    }

    private String getBillingResponseMessage(int responseCode) {
        switch (responseCode) {
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "Feature not supported";
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "Service Disconnected";
            case BillingClient.BillingResponseCode.OK:
                return "Success";
            case BillingClient.BillingResponseCode.USER_CANCELED:
                return "Store Canceled";
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                return "Service Unavailable";
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                return "Store Unavailable";
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                return "Item Unavailable";
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "Developer error";
            case BillingClient.BillingResponseCode.ERROR:
                return "Store Error";
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                return "Item already owned";
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                return "Item not owned";
            default:
                return "Store Message " + responseCode;
        }
    }
}
