package dev.odaridavid.smoovie.observability

import io.github.aakira.napier.Napier

internal class NapierLogger : Logger {
    override fun verbose(
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        Napier.v(tag = tag, throwable = throwable, message = message)
    }

    override fun debug(
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        Napier.d(tag = tag, throwable = throwable, message = message)
    }

    override fun info(
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        Napier.i(tag = tag, throwable = throwable, message = message)
    }

    override fun warning(
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        Napier.w(tag = tag, throwable = throwable, message = message)
    }

    override fun error(
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        Napier.e(tag = tag, throwable = throwable, message = message)
    }
}
