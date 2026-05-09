package dev.odaridavid.smoovie.security

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
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

val AppCheckHeader =
    createClientPlugin("AppCheckHeader") {
        onRequest { request, _ ->
            val token = fetchAppCheckToken()
            if (token != null) request.header("X-Firebase-AppCheck", token)
        }
    }
