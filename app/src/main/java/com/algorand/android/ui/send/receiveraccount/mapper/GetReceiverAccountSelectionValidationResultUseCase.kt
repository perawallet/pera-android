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

package com.algorand.android.ui.send.receiveraccount.mapper

import com.algorand.android.R
import com.algorand.android.SendAlgoNavigationDirections
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.models.AnnotatedString
import com.algorand.android.nft.ui.model.RequestOptInConfirmationArgs
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.CloseTransactionToSameAccount
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.NetworkError
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.NewAccountBalanceNotValid
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.ReceiverNotOptedInToAsset
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.SenderNotOptedInToAsset
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.SendingLessAmountThanRequired
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.SendingMaxAmountToSameAccount
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.Valid
import com.algorand.android.transaction.domain.model.ValidateReceiverAccountPayload
import com.algorand.android.module.transaction.ui.sendasset.domain.GetAssetTransferTargetUser
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser
import com.algorand.android.ui.send.receiveraccount.ReceiverAccountSelectionFragmentDirections
import com.algorand.android.utils.Resource
import com.algorand.android.utils.formatAsAlgoString
import java.math.BigInteger
import javax.inject.Inject

class GetReceiverAccountSelectionValidationResultUseCase @Inject constructor(
    private val getAsset: GetAsset,
    private val getAssetTransferTargetUser: GetAssetTransferTargetUser
) : GetReceiverAccountSelectionValidationResult {

    override suspend fun invoke(
        payload: ValidateReceiverAccountPayload,
        validationResult: ReceiverAccountValidationResult,
        nftDomainAddress: String?,
        nftDomainServiceLogoUrl: String?
    ): Resource<AssetTransferTargetUser> {
        return when (validationResult) {
            ReceiverAccountValidationResult.InvalidAlgorandAddress -> mapToInvalidAlgorandAddressResult()
            is NetworkError -> mapToNetworkError()
            is SenderNotOptedInToAsset -> mapToNetworkError()
            is ReceiverNotOptedInToAsset -> mapToReceiverNotOptedInError(payload)
            is SendingLessAmountThanRequired -> {
                mapToSendingLessAmountThanRequiredError(validationResult.requiredMinSendingAmount)
            }
            is SendingMaxAmountToSameAccount -> mapToSendingMaxAmountToSameAccountError()
            is CloseTransactionToSameAccount -> mapToCloseTransactionToSameAccountError()
            is NewAccountBalanceNotValid -> mapToNewAccountBalanceNotValidError()
            is Valid -> mapToValidResult(payload, nftDomainAddress, nftDomainServiceLogoUrl)
        }
    }

    private fun mapToInvalidAlgorandAddressResult(): Resource<AssetTransferTargetUser> {
        return Resource.Error.Warning(R.string.warning, AnnotatedString(R.string.key_not_valid))
    }

    private fun mapToNetworkError(): Resource<AssetTransferTargetUser> {
        return Resource.Error.Api(Exception())
    }

    private suspend fun mapToReceiverNotOptedInError(
        payload: ValidateReceiverAccountPayload
    ): Resource<AssetTransferTargetUser> {
        val nextDirection = ReceiverAccountSelectionFragmentDirections
            .actionReceiverAccountSelectionFragmentToRequestOptInConfirmationNavigation(
                RequestOptInConfirmationArgs(
                    payload.senderAddress,
                    payload.receiverAddress,
                    payload.assetId,
                    getAsset(payload.assetId)?.fullName
                )
            )
        return Resource.Error.Navigation(nextDirection)
    }

    private fun mapToSendingLessAmountThanRequiredError(
        minRequiredAmount: BigInteger
    ): Resource<AssetTransferTargetUser> {
        val warningBodyMessage = AnnotatedString(
            R.string.you_re_trying_to_send,
            listOf("amount" to minRequiredAmount.formatAsAlgoString())
        )
        return Resource.Error.Warning(R.string.warning, warningBodyMessage)
    }

    private fun mapToSendingMaxAmountToSameAccountError(): Resource<AssetTransferTargetUser> {
        return Resource.Error.GlobalWarning(annotatedString = AnnotatedString(R.string.you_can_not_send_your))
    }

    private fun mapToCloseTransactionToSameAccountError(): Resource<AssetTransferTargetUser> {
        return Resource.Error.GlobalWarning(annotatedString = AnnotatedString(R.string.you_can_not_send_your))
    }

    private fun mapToNewAccountBalanceNotValidError(): Resource<AssetTransferTargetUser> {
        val navDirection = SendAlgoNavigationDirections.actionGlobalSingleButtonBottomSheet(
            titleAnnotatedString = AnnotatedString(R.string.minimum_amount_required),
            descriptionAnnotatedString = AnnotatedString(R.string.this_is_the_first_transaction),
            buttonStringResId = R.string.i_understand,
            drawableResId = R.drawable.ic_info,
            drawableTintResId = R.color.error_tint_color
        )
        return Resource.Error.Navigation(navDirection)
    }

    private suspend fun mapToValidResult(
        payload: ValidateReceiverAccountPayload,
        nfDomainAddress: String?,
        nfDomainServiceLogoUrl: String?
    ): Resource<AssetTransferTargetUser> {
        return Resource.Success(
            getAssetTransferTargetUser(
                payload.receiverAddress,
                nfDomainAddress,
                nfDomainServiceLogoUrl
            )
        )
    }
}
