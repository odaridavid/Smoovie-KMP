package dev.odaridavid.smoovie.security

import com.google.firebase.appcheck.FirebaseAppCheck

class AndroidAppCheckTokenProvider : AppCheckTokenProvider {
    override fun fetchToken(callback: (String?) -> Unit) {
        FirebaseAppCheck
            .getInstance()
            .getAppCheckToken(false)
            .addOnSuccessListener { result -> callback(result.token) }
            .addOnFailureListener { callback(null) }
    }
}
