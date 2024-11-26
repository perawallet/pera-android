package co.algorand.app.di

import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.koinConfiguration

expect fun nativeConfig() : KoinAppDeclaration

val initKoinConfig = koinConfiguration {
    includes(nativeConfig())
    modules(appModules())
}

fun appModules() =
    listOf(
        provideHttpClientModules,
        provideRepositoryModules,
        provideViewModelModules
        // providePlatformModules(), // Room DB & DataStore located here
//        module {
//            single { AppSettings(get()) }
//        },
    )
