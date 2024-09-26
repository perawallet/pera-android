package com.algorand.android.module.account.core.component.domain.model

import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.parity.domain.model.ParityValue
import java.math.*

sealed class BaseAccountAssetData {

    abstract val id: Long
    abstract val name: String?
    abstract val shortName: String?
    abstract val isAlgo: Boolean
    abstract val decimals: Int
    abstract val creatorPublicKey: String?
    abstract val usdValue: BigDecimal?
    abstract val verificationTier: VerificationTier?
    abstract val optedInAtRound: Long?

    sealed class BaseOwnedAssetData : BaseAccountAssetData() {
        abstract val amount: BigInteger
        abstract val formattedAmount: String
        abstract val formattedCompactAmount: String
        abstract val parityValueInSelectedCurrency: ParityValue
        abstract val parityValueInSecondaryCurrency: ParityValue
        abstract val isAmountInSelectedCurrencyVisible: Boolean
        abstract val prismUrl: String?

        fun getSelectedCurrencyParityValue(): ParityValue {
            return if (isAlgo && parityValueInSelectedCurrency.selectedCurrencySymbol == Currency.ALGO.symbol) {
                parityValueInSecondaryCurrency
            } else {
                parityValueInSelectedCurrency
            }
        }

        data class OwnedAssetData(
            override val id: Long,
            override val name: String?,
            override val shortName: String?,
            override val isAlgo: Boolean,
            override val decimals: Int,
            override val creatorPublicKey: String?,
            override val usdValue: BigDecimal?,
            override val amount: BigInteger,
            override val formattedAmount: String,
            override val formattedCompactAmount: String,
            override val isAmountInSelectedCurrencyVisible: Boolean,
            override val parityValueInSelectedCurrency: ParityValue,
            override val parityValueInSecondaryCurrency: ParityValue,
            override val prismUrl: String?,
            override val verificationTier: VerificationTier,
            override val optedInAtRound: Long?
        ) : BaseOwnedAssetData()

        sealed class BaseOwnedCollectibleData : BaseOwnedAssetData() {

            abstract val collectibleName: String?
            abstract val collectionName: String?

            val isOwnedByTheUser: Boolean get() = amount.compareTo(BigInteger.ZERO) == 1

            override val verificationTier: VerificationTier?
                get() = null

            data class OwnedCollectibleImageData(
                override val id: Long,
                override val name: String?,
                override val shortName: String?,
                override val isAlgo: Boolean,
                override val decimals: Int,
                override val creatorPublicKey: String?,
                override val usdValue: BigDecimal?,
                override val amount: BigInteger,
                override val formattedAmount: String,
                override val formattedCompactAmount: String,
                override val isAmountInSelectedCurrencyVisible: Boolean,
                override val collectibleName: String?,
                override val collectionName: String?,
                override val parityValueInSelectedCurrency: ParityValue,
                override val parityValueInSecondaryCurrency: ParityValue,
                override val prismUrl: String?,
                override val optedInAtRound: Long?
            ) : BaseOwnedCollectibleData()

            data class OwnedCollectibleVideoData(
                override val id: Long,
                override val name: String?,
                override val shortName: String?,
                override val isAlgo: Boolean,
                override val decimals: Int,
                override val creatorPublicKey: String?,
                override val usdValue: BigDecimal?,
                override val amount: BigInteger,
                override val formattedAmount: String,
                override val formattedCompactAmount: String,
                override val isAmountInSelectedCurrencyVisible: Boolean,
                override val collectibleName: String?,
                override val collectionName: String?,
                override val parityValueInSelectedCurrency: ParityValue,
                override val parityValueInSecondaryCurrency: ParityValue,
                override val prismUrl: String?,
                override val optedInAtRound: Long?,
            ) : BaseOwnedCollectibleData()

            data class OwnedCollectibleAudioData(
                override val id: Long,
                override val name: String?,
                override val shortName: String?,
                override val isAlgo: Boolean,
                override val decimals: Int,
                override val creatorPublicKey: String?,
                override val usdValue: BigDecimal?,
                override val amount: BigInteger,
                override val formattedAmount: String,
                override val formattedCompactAmount: String,
                override val isAmountInSelectedCurrencyVisible: Boolean,
                override val collectibleName: String?,
                override val collectionName: String?,
                override val parityValueInSelectedCurrency: ParityValue,
                override val parityValueInSecondaryCurrency: ParityValue,
                override val prismUrl: String?,
                override val optedInAtRound: Long?,
            ) : BaseOwnedCollectibleData()

            data class OwnedCollectibleMixedData(
                override val id: Long,
                override val name: String?,
                override val shortName: String?,
                override val isAlgo: Boolean,
                override val decimals: Int,
                override val creatorPublicKey: String?,
                override val usdValue: BigDecimal?,
                override val amount: BigInteger,
                override val formattedAmount: String,
                override val formattedCompactAmount: String,
                override val isAmountInSelectedCurrencyVisible: Boolean,
                override val collectibleName: String?,
                override val collectionName: String?,
                override val parityValueInSelectedCurrency: ParityValue,
                override val parityValueInSecondaryCurrency: ParityValue,
                override val prismUrl: String?,
                override val optedInAtRound: Long?,
            ) : BaseOwnedCollectibleData()

            data class OwnedCollectibleUnsupportedData(
                override val id: Long,
                override val name: String?,
                override val shortName: String?,
                override val isAlgo: Boolean,
                override val decimals: Int,
                override val creatorPublicKey: String?,
                override val usdValue: BigDecimal?,
                override val amount: BigInteger,
                override val formattedAmount: String,
                override val formattedCompactAmount: String,
                override val isAmountInSelectedCurrencyVisible: Boolean,
                override val collectibleName: String?,
                override val collectionName: String?,
                override val parityValueInSelectedCurrency: ParityValue,
                override val parityValueInSecondaryCurrency: ParityValue,
                override val prismUrl: String?,
                override val optedInAtRound: Long?
            ) : BaseOwnedCollectibleData()
        }
    }

    sealed class PendingAssetData : BaseAccountAssetData() {

        override val optedInAtRound: Long? = null

        data class DeletionAssetData(
            override val id: Long,
            override val name: String?,
            override val shortName: String?,
            override val isAlgo: Boolean,
            override val decimals: Int,
            override val creatorPublicKey: String?,
            override val usdValue: BigDecimal?,
            override val verificationTier: VerificationTier
        ) : PendingAssetData()

        data class AdditionAssetData(
            override val id: Long,
            override val name: String?,
            override val shortName: String?,
            override val isAlgo: Boolean,
            override val decimals: Int,
            override val creatorPublicKey: String?,
            override val usdValue: BigDecimal?,
            override val verificationTier: VerificationTier
        ) : PendingAssetData()

        sealed class BasePendingCollectibleData : PendingAssetData() {

            abstract val collectibleName: String?
            abstract val collectionName: String?
            abstract val primaryImageUrl: String?

            val avatarDisplayText: String
                get() = collectibleName ?: name ?: shortName ?: id.toString()

            override val verificationTier: VerificationTier?
                get() = null

            sealed class PendingAdditionCollectibleData : BasePendingCollectibleData() {

                data class AdditionImageCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingAdditionCollectibleData()

                data class AdditionVideoCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingAdditionCollectibleData()

                data class AdditionAudioCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingAdditionCollectibleData()

                data class AdditionUnsupportedCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingAdditionCollectibleData()

                data class AdditionMixedCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingAdditionCollectibleData()
            }

            sealed class PendingDeletionCollectibleData : BasePendingCollectibleData() {

                data class DeletionImageCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingDeletionCollectibleData()

                data class DeletionVideoCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingDeletionCollectibleData()

                data class DeletionAudioCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingDeletionCollectibleData()

                data class DeletionUnsupportedCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingDeletionCollectibleData()

                data class DeletionMixedCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingDeletionCollectibleData()
            }

            sealed class PendingSendingCollectibleData : BasePendingCollectibleData() {

                data class SendingImageCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingSendingCollectibleData()

                data class SendingVideoCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingSendingCollectibleData()

                data class SendingAudioCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingSendingCollectibleData()

                data class SendingUnsupportedCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingSendingCollectibleData()

                data class SendingMixedCollectibleData(
                    override val id: Long,
                    override val name: String?,
                    override val shortName: String?,
                    override val isAlgo: Boolean,
                    override val decimals: Int,
                    override val creatorPublicKey: String?,
                    override val usdValue: BigDecimal?,
                    override val collectibleName: String?,
                    override val collectionName: String?,
                    override val primaryImageUrl: String?
                ) : PendingSendingCollectibleData()
            }
        }
    }
}
