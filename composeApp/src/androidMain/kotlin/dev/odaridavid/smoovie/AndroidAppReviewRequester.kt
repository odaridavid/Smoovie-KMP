package dev.odaridavid.smoovie

import androidx.activity.ComponentActivity
import com.google.android.play.core.review.ReviewManagerFactory
import dev.odaridavid.smoovie.utils.AppReviewRequester

class AndroidAppReviewRequester : AppReviewRequester {
    var activity: ComponentActivity? = null

    override fun requestReview() {
        val act = activity ?: return
        val manager = ReviewManagerFactory.create(act)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(act, task.result)
            }
        }
    }
}
