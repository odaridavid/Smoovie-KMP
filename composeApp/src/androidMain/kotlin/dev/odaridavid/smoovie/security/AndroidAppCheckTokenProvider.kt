package dev.odaridavid.smoovie.security

import com.google.firebase.appcheck.FirebaseAppCheck
import io.github.aakira.napier.Napier

class AndroidAppCheckTokenProvider : AppCheckTokenProvider {
    override fun fetchToken(callback: (String?) -> Unit) {
        FirebaseAppCheck
            .getInstance()
            .getAppCheckToken(false)
            .addOnSuccessListener { result -> callback(result.token) }
            .addOnFailureListener { error ->
                Napier.e(tag = "AppCheck", throwable = error) { "Failed to fetch App Check token" }
                callback(null)
            }
    }
}
