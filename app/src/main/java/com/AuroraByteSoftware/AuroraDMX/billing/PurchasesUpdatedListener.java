package com.AuroraByteSoftware.AuroraDMX.billing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;

import java.util.List;

public class PurchasesUpdatedListener implements com.android.billingclient.api.PurchasesUpdatedListener {
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
