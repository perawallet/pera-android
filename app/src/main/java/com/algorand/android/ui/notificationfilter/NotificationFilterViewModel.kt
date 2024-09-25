/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.notificationfilter

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.module.account.core.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.core.BaseViewModel
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.database.NotificationFilterDao
import com.algorand.android.module.notification.domain.usecase.SetNotificationFilter
import com.algorand.android.utils.Resource
import com.algorand.android.utils.preference.isNotificationActivated
import com.algorand.android.utils.preference.setNotificationPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationFilterViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val notificationFilterDao: NotificationFilterDao,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val getSortedAccountsByPreference: GetSortedAccountsByPreference,
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val setNotificationFilter: SetNotificationFilter
) : BaseViewModel() {

    val notificationFilterOperation = MutableStateFlow<Resource<Unit>?>(null)
    val notificationFilterListStateFlow = MutableStateFlow<List<AccountNotificationOption>>(listOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            notificationFilterDao.getAllAsFlow().collectLatest { notificationFilterList ->
                val sortedAccountListItem = getSortedAccountsByPreference(
                    onLoadedAccountConfiguration = {
                        accountItemConfigurationMapper(
                            accountAddress = address,
                            accountDisplayName = getAccountDisplayName(address),
                            accountType = accountType,
                            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
                            accountPrimaryValue = getAccountTotalValue(address, true).primaryAccountValue,
                        )
                    },
                    onFailedAccountConfiguration = {
                        accountItemConfigurationMapper(
                            accountAddress = this,
                            accountDisplayName = getAccountDisplayName(this),
                            accountType = null,
                            accountIconDrawablePreview = getAccountIconDrawablePreview(this),
                            showWarningIcon = true
                        )
                    }
                )
                val generatedList = sortedAccountListItem.map { accountListItem ->
                    val isAccountFiltered = notificationFilterList.any {
                        it.publicKey == accountListItem.itemConfiguration.accountAddress
                    }
                    AccountNotificationOption(accountListItem.itemConfiguration, isAccountFiltered)
                }
                notificationFilterListStateFlow.emit(generatedList)
            }
        }
    }

    fun startFilterOperation(publicKey: String, isFiltered: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationFilterOperation.value = Resource.Loading
            notificationFilterOperation.value = setNotificationFilter(publicKey, isFiltered).use(
                onSuccess = {
                    Resource.Success(Unit)
                },
                onFailed = { exception, _ ->
                    Resource.Error.Api(exception)
                }
            )
        }
    }

    fun isPushNotificationsEnabled(): Boolean {
        return sharedPref.isNotificationActivated()
    }

    fun setPushNotificationPreference(notificationPreference: Boolean) {
        sharedPref.setNotificationPreference(notificationPreference)
    }
}
