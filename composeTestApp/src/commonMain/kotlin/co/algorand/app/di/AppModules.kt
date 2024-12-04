package co.algorand.app.di

import com.algorand.common.account.local.di.localAccountsKoinModule
import com.algorand.common.di.platformKoinModule
import com.algorand.common.encryption.di.encryptionModule
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
        provideViewModelModules,
        localAccountsKoinModule,
        encryptionModule,
        platformKoinModule()
        // providePlatformModules(), // Room DB & DataStore located here
//        module {
//            single { AppSettings(get()) }
//        },
    )
