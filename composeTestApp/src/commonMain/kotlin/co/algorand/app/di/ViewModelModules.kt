package co.algorand.app.di

import co.algorand.app.ui.screens.home.AccountsViewModel
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import com.algorand.common.viewmodel.EventDelegate
import com.algorand.common.viewmodel.StateDelegate
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val provideViewModelModules = module {
    single { SnackbarViewModel(EventDelegate()) }
    viewModel<AccountsViewModel> { AccountsViewModel(get(), get(), get(), get(), get(), StateDelegate()) }
}
