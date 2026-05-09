package dev.odaridavid.smoovie.security

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface AppCheckTokenProvider {
    fun fetchToken(callback: (String?) -> Unit)
}

object AppCheckTokenProviderRegistry {
    var instance: AppCheckTokenProvider? = null
}

suspend fun fetchAppCheckToken(): String? {
    val provider = AppCheckTokenProviderRegistry.instance ?: return null
    return suspendCancellableCoroutine { cont ->
        provider.fetchToken { token ->
            if (cont.isActive) cont.resume(token)
        }
    }
}
