package dev.odaridavid.smoovie.observability

import io.ktor.client.plugins.logging.Logger as KtorLogger

internal class NapierKtorLogger(
    private val logger: Logger,
) : KtorLogger {
    override fun log(message: String) {
        logger.debug(tag = NETWORK_TAG) { message }
    }

    private companion object {
        const val NETWORK_TAG = "Network"
    }
}
