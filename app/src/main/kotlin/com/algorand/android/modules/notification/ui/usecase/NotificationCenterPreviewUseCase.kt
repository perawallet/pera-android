package com.algorand.android.modules.notification.ui.usecase

import com.algorand.android.modules.notification.ui.mapper.NotificationCenterPreviewMapper
import com.algorand.android.modules.notification.ui.model.NotificationCenterPreview
import com.algorand.android.modules.notification.ui.model.NotificationListItem
import com.algorand.android.repository.NotificationRepository
import com.algorand.android.utils.Event
import com.algorand.android.utils.orNow
import com.algorand.android.utils.parseFormattedDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NotificationCenterPreviewUseCase @Inject constructor(
    private val notificationCenterPreviewMapper: NotificationCenterPreviewMapper,
    private val notificationRepository: NotificationRepository
) {

    fun setLastRefreshedDateTime(zonedDateTime: ZonedDateTime) {
        val lastRefreshedZonedDateTimeAsString = zonedDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
        notificationRepository.saveLastRefreshedDateTime(lastRefreshedZonedDateTimeAsString)
    }

    fun getLastRefreshedDateTime(): ZonedDateTime {
        val lastRefreshedZonedDateTimeAsString = notificationRepository.getLastRefreshedDateTime()
        return lastRefreshedZonedDateTimeAsString.parseFormattedDate(DateTimeFormatter.ISO_DATE_TIME).orNow()
    }

    fun onNotificationClickEvent(notificationListItem: NotificationListItem): Flow<NotificationCenterPreview> = flow {
        if (notificationListItem.isFailed) return@flow
        notificationListItem.uri?.let {
            emit(notificationCenterPreviewMapper.mapTo(onNotificationClickedEvent = Event(it)))
        }
    }
}
