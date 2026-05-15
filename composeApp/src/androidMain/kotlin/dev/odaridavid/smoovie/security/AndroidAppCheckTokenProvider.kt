package dev.odaridavid.smoovie.security

import android.util.Log
import com.google.firebase.appcheck.FirebaseAppCheck

class AndroidAppCheckTokenProvider : AppCheckTokenProvider {
    override fun fetchToken(callback: (String?) -> Unit) {
        FirebaseAppCheck
            .getInstance()
            .getAppCheckToken(false)
            .addOnSuccessListener { result -> callback(result.token) }
            .addOnFailureListener { error ->
                Log.e("AppCheck", "Failed to fetch App Check token", error)
                callback(null)
            }
    }
}
