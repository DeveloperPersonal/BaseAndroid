package com.datastore.billing

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface SDKBillingV4Listener {
    /**
     * Thông tin về [productId] & [skuDetails]
     * */
    fun onSDKBillingProductIdPrice(productId: String, skuDetails: SkuDetails) {}

    /**
     * Mục đích phương thức này
     * nhằm kiểm tra các đơn hàng của người dùng và trả về [productId] & [purchase] tương ứng
     * */
    fun onSDKBillingPurchased(productId: String, purchase: Purchase) {}

    /**
     * Khi người dùng mua hàng thành công thì sẽ gọi tới phương thức này
     * và trả về [productId] & [purchase] tương ứng
     * */
    fun onSDKBillingV4SuccessfulPurchased(productId: String) {}

    /**
     * Có lỗi với Billing hoặc trong quá trình xử lí
     * */
    fun onSDKBillingV4Error(billingError: BillingError) {}
}