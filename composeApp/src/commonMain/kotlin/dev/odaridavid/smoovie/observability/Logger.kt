package dev.odaridavid.smoovie.observability

interface Logger {
    fun verbose(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    )

    fun debug(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    )

    fun info(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    )

    fun warning(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    )

    fun error(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    )
}
