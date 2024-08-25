/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.transactionui.sendasset.model

import android.os.Parcelable
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.contacts.component.domain.model.Contact
import kotlinx.parcelize.Parcelize

sealed interface AssetTransferTargetUser : Parcelable {

    fun getReceiverAddress(): String

    @Parcelize
    data class ContactUser(val value: Contact) : AssetTransferTargetUser {
        override fun getReceiverAddress(): String = value.address
    }

    @Parcelize
    data class NfDomainUser(
        val nfDomainAddress: String,
        val nfDomainServiceLogoUrl: String?
    ) : AssetTransferTargetUser {
        override fun getReceiverAddress(): String = nfDomainAddress
    }

    @Parcelize
    data class Account(
        val accountDisplayName: AccountDisplayName,
        val accountIconDrawablePreview: AccountIconDrawablePreview
    ) : AssetTransferTargetUser {
        override fun getReceiverAddress(): String = accountDisplayName.accountAddress
    }

    @Parcelize
    data class Address(
        val address: String
    ) : AssetTransferTargetUser {
        override fun getReceiverAddress(): String = address
    }
}
