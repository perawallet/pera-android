package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.modules.accounts.ui.model.InboxButtonLabel
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate
import com.algorand.wallet.inbox.asset.domain.usecase.GetAssetInboxRequestCountFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxLastOpenedTime
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

internal class GetTotalInboxCountUseCase @Inject constructor(
    private val getAssetInboxRequestCountFlow: GetAssetInboxRequestCountFlow,
    private val getInboxMessages: GetInboxMessages,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val getInboxLastOpenedTime: GetInboxLastOpenedTime
) : GetTotalInboxCount {

    override suspend fun invoke(): GetTotalInboxCount.InboxResult {
        return resolveJointInboxResult()
            ?: resolveAssetInboxResult()
            ?: GetTotalInboxCount.InboxResult()
    }

    private suspend fun resolveJointInboxResult(): GetTotalInboxCount.InboxResult? {
        if (!isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)) return null
        val inboxMessages = getInboxMessages() ?: return null
        val lastOpenedTime = getInboxLastOpenedTime()

        val importDates = inboxMessages.jointAccountImportRequests?.mapNotNull { it.creationDatetime }.orEmpty()
        val signDates = inboxMessages.jointAccountSignRequests?.mapNotNull { it.creationDatetime }.orEmpty()
        val candidates = listOf(
            importDates to InboxButtonLabel.JointAccountRequest,
            signDates to InboxButtonLabel.SignTxnRequest
        )
        return candidates.firstNotNullOfOrNull { (dates, label) ->
            dates.takeIf { it.isNotEmpty() }?.let {
                GetTotalInboxCount.InboxResult(label, hasUnseenItems(it, lastOpenedTime))
            }
        }
    }

    private suspend fun resolveAssetInboxResult(): GetTotalInboxCount.InboxResult? {
        val asaCount = getAssetInboxRequestCountFlow().first()
        if (asaCount > 0) return GetTotalInboxCount.InboxResult(InboxButtonLabel.Inbox, true)
        return null
    }

    private fun hasUnseenItems(creationDates: List<String>, lastOpenedTime: ZonedDateTime?): Boolean {
        if (creationDates.isEmpty()) return false
        if (lastOpenedTime == null) return true
        val formatter = getAlgorandMobileDateFormatter()
        return creationDates.any { it.parseFormattedDate(formatter)?.isAfter(lastOpenedTime) == true }
    }
}
