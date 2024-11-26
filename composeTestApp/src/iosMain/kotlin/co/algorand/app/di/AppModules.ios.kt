package co.algorand.app.di

import org.koin.dsl.koinConfiguration

actual fun nativeConfig() = koinConfiguration {
    printLogger()
}
