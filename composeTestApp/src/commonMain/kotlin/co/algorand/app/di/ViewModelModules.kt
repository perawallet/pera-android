package co.algorand.app.di

import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import com.algorand.common.viewmodel.EventDelegate
import org.koin.dsl.module

val provideViewModelModules = module {
    single { SnackbarViewModel(EventDelegate()) }
}