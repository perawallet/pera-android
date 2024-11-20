package co.algorand.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

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

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModules())
    }