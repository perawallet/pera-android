package com.algorand.android.ui.accounts

import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.algorand.android.contacts.component.domain.model.Contact
import com.algorand.android.core.BaseViewModel
import com.algorand.android.deeplink.model.BaseDeepLink
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.tracking.accounts.AccountsEventTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
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
        deepLink: BaseDeepLink.AssetTransferDeepLink,
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
