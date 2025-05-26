/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.rekeyedaccounts.view

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme

class FetchingRekeyedAccountsDialogDelegate(private val onDismiss: () -> Unit) {

    private var fetchingRekeyedAccountsDialog: AlertDialog? = null

    fun show(context: Context) {
        val dialogView = createFetchingRekeyedAccountsView(context)
        fetchingRekeyedAccountsDialog = AlertDialog.Builder(context, R.style.FullScreenDialogStyle)
            .setView(dialogView)
            .setOnDismissListener {
                onDismiss()
            }
            .show()
    }

    fun dismiss() {
        fetchingRekeyedAccountsDialog?.dismiss()
        fetchingRekeyedAccountsDialog = null
    }

    private fun createFetchingRekeyedAccountsView(context: Context): ComposeView {
        return ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PeraTheme {
                    FetchingRekeyedAccountsLoadingDialog()
                }
            }
        }
    }
}
