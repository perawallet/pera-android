package com.algorand.android.modules.keyreg.domain.usecase

import com.algorand.android.R
import com.algorand.android.core.AccountManager
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ScreenState
import com.algorand.android.modules.accounticon.ui.usecase.CreateAccountIconDrawableUseCase
import com.algorand.android.modules.accounts.domain.usecase.AccountDisplayNameUseCase
import com.algorand.android.modules.accounts.domain.usecase.GetAccountValueUseCase
import com.algorand.android.modules.accountstatehelper.domain.usecase.AccountStateHelperUseCase
import com.algorand.android.modules.basesingleaccountselection.ui.mapper.SingleAccountSelectionListItemMapper
import com.algorand.android.modules.basesingleaccountselection.ui.model.SingleAccountSelectionListItem
import com.algorand.android.modules.basesingleaccountselection.ui.usecase.BaseSingleAccountSelectionUsePreviewCase
import com.algorand.android.modules.currency.domain.usecase.CurrencyUseCase
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.modules.rekey.rekeytostandardaccount.accountselection.ui.mapper.RekeyToStandardAccountSelectionPreviewMapper
import com.algorand.android.modules.rekey.rekeytostandardaccount.accountselection.ui.model.RekeyToStandardAccountSelectionPreview
import com.algorand.android.modules.sorting.accountsorting.domain.usecase.AccountSortPreferenceUseCase
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.usecase.GetSortedLocalAccountsUseCase
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@SuppressWarnings("LongParameterList")
class KeyRegAccountSelectionPreviewUseCase @Inject constructor(
    private val rekeyToStandardAccountSelectionPreviewMapper: RekeyToStandardAccountSelectionPreviewMapper,
    private val accountStateHelperUseCase: AccountStateHelperUseCase,
    accountDetailUseCase: AccountDetailUseCase,
    getAccountValueUseCase: GetAccountValueUseCase,
    accountSortPreferenceUseCase: AccountSortPreferenceUseCase,
    accountDisplayNameUseCase: AccountDisplayNameUseCase,
    parityUseCase: ParityUseCase,
    currencyUseCase: CurrencyUseCase,
    singleAccountSelectionListItemMapper: SingleAccountSelectionListItemMapper,
    getSortedLocalAccountsUseCase: GetSortedLocalAccountsUseCase,
    accountManager: AccountManager,
    createAccountIconDrawableUseCase: CreateAccountIconDrawableUseCase
) : BaseSingleAccountSelectionUsePreviewCase(
    singleAccountSelectionListItemMapper = singleAccountSelectionListItemMapper,
    getSortedLocalAccountsUseCase = getSortedLocalAccountsUseCase,
    accountDisplayNameUseCase = accountDisplayNameUseCase,
    getAccountValueUseCase = getAccountValueUseCase,
    parityUseCase = parityUseCase,
    currencyUseCase = currencyUseCase,
    accountManager = accountManager,
    accountSortPreferenceUseCase = accountSortPreferenceUseCase,
    accountDetailUseCase = accountDetailUseCase,
    createAccountIconDrawableUseCase = createAccountIconDrawableUseCase
) {

    fun getInitialKeyRegSingleAccountSelectionPreview(): RekeyToStandardAccountSelectionPreview {
        return rekeyToStandardAccountSelectionPreviewMapper.mapToRekeyToStandardAccountSelectionPreview(
            screenState = null,
            singleAccountSelectionListItems = emptyList(),
            isLoading = true
        )
    }

    fun getKeyRegSingleAccountSelectionPreviewFlow() = channelFlow {
        getSortedCachedAccountDetailFlow().map { accountsDetails ->
            accountsDetails.map { accountDetail ->
                createAccountItemListFromAccountDetail(accountDetail)
            }
        }.collectLatest { singleAccountItems ->
            val screenState = if (singleAccountItems.isEmpty()) {
                ScreenState.CustomState(title = R.string.no_account_found)
            } else {
                null
            }
            val titleItem = createTitleItem(textResId = R.string.select_account)
            val descriptionItem = createDescriptionItem(
                descriptionAnnotatedString = AnnotatedString(
                    stringResId = R.string.choose_account_for_txn_signature_request
                )
            )
            val singleAccountSelectionListItems = mutableListOf<SingleAccountSelectionListItem>().apply {
                add(titleItem)
                add(descriptionItem)
                addAll(singleAccountItems)
            }
            val preview = rekeyToStandardAccountSelectionPreviewMapper.mapToRekeyToStandardAccountSelectionPreview(
                screenState = screenState,
                singleAccountSelectionListItems = singleAccountSelectionListItems,
                isLoading = false
            )
            send(preview)
        }
    }
}
