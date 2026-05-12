package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.utils.AppReviewRequester
import platform.StoreKit.SKStoreReviewController

class IosAppReviewRequester : AppReviewRequester {
    override fun requestReview() {
        SKStoreReviewController.requestReview()
    }
}
