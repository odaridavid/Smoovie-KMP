package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.utils.AppReviewRequester

class FakeAppReviewRequester : AppReviewRequester {
    var requestCount = 0

    override fun requestReview() {
        requestCount++
    }
}
