package com.algorand.android.accountinfo.component.data.mapper.model

import com.algorand.android.accountinfo.component.data.model.AccountInformationResponse
import com.algorand.android.accountinfo.component.data.model.RekeyedAccountsResponse
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.model.AssetHolding
import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal interface AccountInformationMapper {
    operator fun invoke(response: AccountInformationResponse): AccountInformation?
    operator fun invoke(response: RekeyedAccountsResponse): List<AccountInformation>
    operator fun invoke(entity: AccountInformationEntity, assetHoldingList: List<AssetHolding>): AccountInformation
}
