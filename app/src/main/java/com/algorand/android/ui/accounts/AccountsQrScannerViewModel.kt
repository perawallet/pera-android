package com.algorand.android.ui.accounts

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AssetTransaction
import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.modules.tracking.accounts.AccountsEventTracker
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AccountsQrScannerViewModel @Inject constructor(
    private val accountsEventTracker: AccountsEventTracker,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase
) : BaseViewModel() {

    fun logAccountsQrConnectEvent() {
        viewModelScope.launch {
            accountsEventTracker.logAccountsQrConnectEvent()
        }
    }

    fun isAccountLimitExceed(): Boolean {
        return isAccountLimitExceedUseCase.isAccountLimitExceed()
    }

    fun getAssetTransaction(
        deepLink: com.algorand.android.module.deeplink.model.BaseDeepLink.AssetTransferDeepLink,
        receiverAddress: String,
        receiverName: String
    ): AssetTransaction {
        return AssetTransaction(
            assetId = deepLink.assetId,
            note = deepLink.note, // normal note
            xnote = deepLink.xnote, // locked note
            amount = deepLink.amount,
            receiverUser = Contact(address = receiverAddress, name = receiverName, imageUri = null)
        )
    }
}
