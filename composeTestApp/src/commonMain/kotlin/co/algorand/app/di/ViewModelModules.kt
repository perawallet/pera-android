package co.algorand.app.di

import co.algorand.app.ui.AppViewModel
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val provideViewModelModules = module {
    single { SnackbarViewModel() }
    // viewModel<AccountsViewModel> { AccountsViewModel(get(), get(), get(), get(), get(), StateDelegate(), get()) }
    viewModel<AppViewModel> { AppViewModel() }
}
