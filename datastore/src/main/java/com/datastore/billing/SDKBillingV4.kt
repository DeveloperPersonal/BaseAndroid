package com.datastore.billing

import com.android.billingclient.api.*
import com.datastore.BaseActivity
import com.datastore.BuildConfig
import com.datastore.launchIO
import com.datastore.launchMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SDKBillingV4(private val baseActivity: BaseActivity<*>) :
    PurchasesUpdatedListener,
    BillingClientStateListener {

    private val billingClient by lazy {
        BillingClient.newBuilder(baseActivity)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    private val skuDetailsListCache = mutableListOf<SkuDetails>()
    private val billingList = mutableListOf<Billing>()
    private var listener: SDKBillingV4Listener? = null

    private suspend fun onBillingSetupFinished() {
        skuDetailsListCache.clear()
        val inAppList = billingList.filter { it.billingStyle == BillingStyle.IN_APP }
        if (inAppList.isNotEmpty()) {
            mOnBillingSetupFinished(inAppList, BillingClient.SkuType.INAPP)
        }
        val subList = billingList.filter { it.billingStyle == BillingStyle.SUBS }
        if (subList.isNotEmpty()) {
            mOnBillingSetupFinished(subList, BillingClient.SkuType.SUBS)
        }
    }

    private suspend fun mOnBillingSetupFinished(list: List<Billing>, skuType: String) {
        val params = SkuDetailsParams.newBuilder()
        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(skuType)
        }
        val productList = mutableListOf<String>()
        list.forEach {
            productList.add(it.productId)
        }
        params.setSkusList(productList).setType(skuType)
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }
        val skuDetailsList = skuDetailsResult.skuDetailsList
        if (!skuDetailsList.isNullOrEmpty()) {
            skuDetailsListCache.addAll(skuDetailsList)
            for (skuDetails in skuDetailsList) {
                val productId = skuDetails.sku
                withContext(Dispatchers.Main) {
                    listener?.onSDKBillingProductIdPrice(productId, skuDetails)
                }
            }
            purchasesResult.purchasesList.forEach { purchase ->
                val skus = purchase.skus
                if (skus.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        listener?.onSDKBillingPurchased(skus.first(), purchase)
                    }
                }
            }
        }
    }

    private suspend fun onBillingResult(result: BillingResult, list: List<Purchase?>) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            repeat(list.size) { position ->
                val purchase = list[position]
                if (purchase != null) {
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) {
            checkPurchased(purchase)
            return
        }
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val billingResult = withContext(Dispatchers.IO) {
            billingClient.acknowledgePurchase(acknowledgePurchaseParams)
        }
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            checkPurchased(purchase)
        }
    }

    private suspend fun checkPurchased(purchase: Purchase) {
        val list = purchase.skus
        if (list.isNotEmpty()) {
            val productId = list.first()
            withContext(Dispatchers.Main) {
                listener?.onSDKBillingV4SuccessfulPurchased(productId)
            }
        }
    }

    fun startConnection(billingList: MutableList<Billing>, listener: SDKBillingV4Listener? = null) {
        this.billingList.clear()
        this.billingList.addAll(billingList)
        this.listener = listener
        billingClient.startConnection(this)
    }

    fun buy(productId: String) {
        if (skuDetailsListCache.isEmpty()) {
            listener?.onSDKBillingV4Error(BillingError.BUY_ERROR_SKU_DETAILS_LIST_CACHE_EMPTY)
            return
        }
        val skuDetails = skuDetailsListCache.firstOrNull { it.sku == productId }
        if (skuDetails == null) {
            listener?.onSDKBillingV4Error(BillingError.BUY_ERROR_PRODUCT_ID_NOT_FOUND)
            return
        }
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(baseActivity, billingFlowParams)
    }

    fun consume(purchase: Purchase) {
        if (!BuildConfig.DEBUG) return
        val build = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        baseActivity.launchIO { billingClient.consumePurchase(build) }
    }

    fun endConnection() {
        listener = null
        billingClient.endConnection()
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (!baseActivity.exist() || purchases == null) return
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                baseActivity.launchIO { onBillingResult(billingResult, purchases) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                baseActivity.launchMain {
                    listener?.onSDKBillingV4Error(BillingError.BUY_ERROR_USER_CANCELED)
                }
            }
            else -> {
                baseActivity.launchMain {
                    listener?.onSDKBillingV4Error(BillingError.BUY_ERROR_OTHER)
                }
            }
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && baseActivity.exist()) {
            baseActivity.launchIO {
                onBillingSetupFinished()
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        baseActivity.launchMain {
            listener?.onSDKBillingV4Error(BillingError.BILLING_ERROR_DISCONNECT)
        }
    }
}