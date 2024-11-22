package co.algorand.app.di


// import co.algorand.app.data.repositories.AlgorandRepository
import org.koin.dsl.module

val provideRepositoryModules =
    module {
        // single<AlgorandRepository> { AlgorandRepository() }
    }