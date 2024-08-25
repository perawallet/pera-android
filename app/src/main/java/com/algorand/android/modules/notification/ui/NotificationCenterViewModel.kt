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

package com.algorand.android.modules.notification.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.notification.domain.usecase.NotificationStatusUseCase
import com.algorand.android.modules.notification.ui.mapper.NotificationListItemMapper
import com.algorand.android.modules.notification.ui.model.NotificationListItem
import com.algorand.android.notification.PeraNotificationManager
import com.algorand.android.notification.domain.usecase.GetNotificationHistoryPagingFlow
import com.algorand.android.notification.domain.usecase.GetNotificationLastRefreshDateTime
import com.algorand.android.notification.domain.usecase.SetNotificationLastRefreshDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationCenterViewModel @Inject constructor(
    private val peraNotificationManager: PeraNotificationManager,
    private val notificationStatusUseCase: NotificationStatusUseCase,
    private val getNotificationLastRefreshDateTime: GetNotificationLastRefreshDateTime,
    private val setNotificationLastRefreshDateTime: SetNotificationLastRefreshDateTime,
    private val getNotificationHistoryPagingFlow: GetNotificationHistoryPagingFlow,
    private val notificationListItemMapper: NotificationListItemMapper
) : BaseViewModel() {

    val notificationHistoryPagingFlow = getNotificationHistoryItemFlow()
        ?.cachedIn(viewModelScope)
        ?.shareIn(viewModelScope, SharingStarted.Lazily)

    fun refreshNotificationData(refreshDateTime: ZonedDateTime? = null) {
        if (refreshDateTime != null) {
            setLastRefreshedDateTime(refreshDateTime)
        }
        getNotificationHistoryPagingFlow.refresh()
    }

    fun getLastRefreshedDateTime(): ZonedDateTime {
        return getNotificationLastRefreshDateTime()
    }

    fun setLastRefreshedDateTime(zonedDateTime: ZonedDateTime) {
        setNotificationLastRefreshDateTime(zonedDateTime)
    }

    fun isRefreshNeededLiveData(): LiveData<Boolean> {
        var newNotificationCount = 0
        return peraNotificationManager.newNotificationLiveData.map {
            newNotificationCount++
            return@map newNotificationCount > 1
        }
    }

    fun updateLastSeenNotification(notificationListItem: NotificationListItem?) {
        viewModelScope.launch {
            notificationStatusUseCase.updateLastSeenNotificationId(
                notificationListItem = notificationListItem ?: return@launch
            )
        }
    }

    private fun getNotificationHistoryItemFlow(): Flow<PagingData<NotificationListItem>>? {
        return getNotificationHistoryPagingFlow()?.map { pagingData ->
            pagingData.map { notificationListItemMapper.map(it) }
        }
    }
}
