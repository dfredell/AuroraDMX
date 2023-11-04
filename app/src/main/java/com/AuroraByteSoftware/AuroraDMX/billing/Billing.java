package com.AuroraByteSoftware.AuroraDMX.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In app purchases from https://developer.android.com/google/play/billing/billing_java_kotlin
 */
public class Billing  {

    private Boolean purchased = true;

    public static final String ITEM_SKU = "unlock_channels";
    private com.AuroraByteSoftware.AuroraDMX.billing.PurchasesUpdatedListener purchasesUpdatedListener = new com.AuroraByteSoftware.AuroraDMX.billing.PurchasesUpdatedListener();

    private BillingClient billingClient = null;
    private ProductDetails productDetails;

    /**
     * 0 - Unknown
     * 1 - purchased
     * 2 - not purchased
     * 3 - error
     */
    public static int PURCHASED = 1;

    public void setup(Context mActivity) {
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
                    try {
                        query();
                    } catch (Exception e) {
                        Log.e("Billing", "onBillingSetupFinished: ", e);
                    }
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
        QueryProductDetailsParams.Product build = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ITEM_SKU)
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(build);
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();

        Log.d("Billing", "query: queryProductDetailsParams" + queryProductDetailsParams);

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    // check billingResult
                    // process returned productDetailsList
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        String responseMessage = getBillingResponseMessage(billingResult.getResponseCode());
                        Log.d(getClass().getSimpleName(), "Billing response " + responseMessage);
                        productDetails = productDetailsList.get(0);
                    }
                }
        );


        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                (billingResult, purchases) -> {
                    // check billingResult
                    // process returned purchase list, e.g. display the plans user owns
                    purchased = false;
                    if (purchases.size() > 0 && purchases.get(0) != null){
                        Purchase purchase = purchases.get(0);
                        purchased = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED;
                    }

                }
        );


    }

    public boolean check() {
        return purchased;
    }

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
    public void requestPurchase(Activity activity){
        String offerToken = null;
        if (productDetails!= null && productDetails.getOneTimePurchaseOfferDetails() != null) {
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                    Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder()
                            // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                            .setProductDetails(productDetails)
                            .build());

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();

            // Launch the billing flow
            BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
            Log.d("Billing", "billingResult: " + billingResult);
        }
    }
}
