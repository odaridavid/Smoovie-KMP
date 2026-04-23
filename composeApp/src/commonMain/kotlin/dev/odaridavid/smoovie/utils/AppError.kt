package dev.odaridavid.smoovie.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode

sealed class AppError {
    data object NetworkError : AppError()

    data object ServerError : AppError()

    data object NotFound : AppError()

    data object Unauthorized : AppError()

    data object Unknown : AppError()
}

fun Throwable.toAppError(): AppError =
    when (this) {
        is ServerResponseException -> {
            AppError.ServerError
        }

        is ClientRequestException -> {
            when (response.status) {
                HttpStatusCode.Unauthorized -> AppError.Unauthorized
                HttpStatusCode.NotFound -> AppError.NotFound
                else -> AppError.Unknown
            }
        }

        else -> {
            AppError.NetworkError
        }
    }
