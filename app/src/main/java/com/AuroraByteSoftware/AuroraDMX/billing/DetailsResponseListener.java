package com.AuroraByteSoftware.AuroraDMX.billing;

import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

public class DetailsResponseListener implements SkuDetailsResponseListener {

    /**
     * 0 - Unknown
     * 1 - purchased
     * 2 - not purchased
     * 3 - error
     */
    private int purchaseStatus = 0;

    @Override
    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
        // Process the result.
        if (responseCode == BillingClient.BillingResponse.OK
                && skuDetailsList != null) {
            for (SkuDetails skuDetails : skuDetailsList) {
                String sku = skuDetails.getSku();
                if (ClientStateListener.ITEM_SKU.equals(sku)) {
                    Log.d(getClass().getSimpleName(), "Item '" + ClientStateListener.ITEM_SKU + "' purchased");
                    purchaseStatus = 1;
                    return;
                }
            }
            purchaseStatus = 2;
            return;
        }
        purchaseStatus = 3;
    }

    public int getPurchaseStatus() {
        return purchaseStatus;
    }
}
