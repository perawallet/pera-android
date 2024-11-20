package co.algorand.app.di

import co.algorand.app.ui.viewmodels.AlgorandBaseViewModel
import org.koin.dsl.module

val provideViewModelModules =
    module {
        single { AlgorandBaseViewModel() }
    }