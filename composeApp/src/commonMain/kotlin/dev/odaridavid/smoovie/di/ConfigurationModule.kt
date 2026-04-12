package dev.odaridavid.smoovie.di

import dev.odaridavid.smoovie.data.ConfigurationRepository
import dev.odaridavid.smoovie.data.ConfigurationRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val configurationModule =
    module {
        single { ConfigurationRepositoryImpl(get()) } bind ConfigurationRepository::class
    }
