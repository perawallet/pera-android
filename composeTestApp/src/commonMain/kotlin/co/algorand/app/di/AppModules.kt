package co.algorand.app.di

import com.algorand.wallet.di.commonModuleKoinModules
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.koinConfiguration

expect fun nativeConfig(): KoinAppDeclaration

val initKoinConfig = koinConfiguration {
    includes(nativeConfig())
    modules(appModules())
}

fun appModules() = listOf(
    provideHttpClientModules,
    provideRepositoryModules,
    provideViewModelModules,
    accountInformationModule
) + commonModuleKoinModules
