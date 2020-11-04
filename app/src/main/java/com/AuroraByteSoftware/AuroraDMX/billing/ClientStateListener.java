package com.AuroraByteSoftware.AuroraDMX.billing;

import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientStateListener implements BillingClientStateListener {
    public static final String ITEM_SKU = "unlock_channels";
    private static final List<String> listOfSkus = new ArrayList<String>();
    private boolean connectionMade = false;
    private BillingClient mBillingClient;
    private final static String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj9upoasavmU51/j6g7vWchEf/g2SGuntcXPlzVu8vp3avDGMQp8E20iI+IO5vqB4wVKf9QRiAv0DFLw+XAGCpx7t6GDt4Sd/qMOkj49Eas1R1Uvghp4yy9Cc/8pL7QOvSW99pq9Pg2iqqbPXlAlLmByQy2p9qhDhl788dMZsUd2VxL5NHY2zQl7a1emWH/MUpvVHNSJkTSdQrLJ4cruTvEDldtD0jSNadK1NSruwa/BH6ieLVswek1cyE7hm0Od5pWw0XCpkR6L7ZkEkeTovSihA3h+rSy6kxZCqrDzMR++EOCxwS/kB3Ly6M5E6EwjZVbK18UQM8/Ecr7/buYxalQIDAQAB";

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

    static {
        listOfSkus.add(ITEM_SKU);
    }

    ClientStateListener(BillingClient mBillingClient) {
        this.mBillingClient = mBillingClient;
    }

    @Override
    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingClient.BillingResponse.OK) {
            // The billing client is ready. You can query purchases here.
            connectionMade = true;

            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
            handleQueryPurchases(purchasesResult);

        }
    }

    private void handleQueryPurchases(Purchase.PurchasesResult purchasesResult) {
        if (purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK) {
            for (Purchase purchase : purchasesResult.getPurchasesList()) {
                if (ITEM_SKU.equals(purchase.getSku())
                        && verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    purchaseStatus = PURCHASED;
                    return;
                }
            }
            purchaseStatus = NOT_PURCHASED;
        } else {
            purchaseStatus = ERROR;
            Log.d(getClass().getSimpleName(), "Unknown billing response " + purchasesResult.getResponseCode());
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
        connectionMade = false;
    }

    public boolean connect() {
        if (!connectionMade) {
            mBillingClient.startConnection(this);
        }
        return connectionMade;
    }

    public int getPurchaseStatus() {
        return purchaseStatus;
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }

        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
}
