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

package com.algorand.android.transactionhistoryui.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.algorand.android.designsystem.R
import com.algorand.android.designsystem.RecyclerListItem
import kotlinx.parcelize.Parcelize

sealed class BaseTransactionItem : RecyclerListItem, Parcelable {

    @Parcelize
    data class StringTitleItem(val title: String) : BaseTransactionItem(), Parcelable {
        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is StringTitleItem && title == other.title
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is StringTitleItem && other == this
        }
    }

    @Parcelize
    data class ResourceTitleItem(@StringRes val stringRes: Int) : BaseTransactionItem(), Parcelable {
        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is ResourceTitleItem && stringRes == other.stringRes
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is ResourceTitleItem && other == this
        }
    }

    sealed class TransactionItem : BaseTransactionItem(), Parcelable {

        abstract val id: String?
        abstract val signature: String?
        abstract val nameRes: Int?
        abstract val description: String?
        abstract val formattedAmount: String?
        abstract val isAmountVisible: Boolean
        abstract val isPending: Boolean
        abstract val amountColorRes: Int?

        abstract fun isSameTransaction(other: RecyclerListItem): Boolean

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return isSameTransaction(other)
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return isSameTransaction(other)
        }

        @Parcelize
        data class KeyRegItem(
            override val id: String?,
            override val signature: String?,
            override val isPending: Boolean,
        ) : TransactionItem() {

            override val formattedAmount: String?
                get() = null

            override val description: String?
                get() = null

            override val amountColorRes: Int?
                get() = null

            override val isAmountVisible: Boolean
                get() = false

            override val nameRes: Int
                get() = R.string.key_reg

            override fun isSameTransaction(other: RecyclerListItem): Boolean {
                return other is KeyRegItem && other.signature == signature
            }
        }

        sealed class PayItem : TransactionItem(), Parcelable {
            @Parcelize
            data class PaySendItem(
                override val id: String?,
                override val signature: String?,
                override val nameRes: Int? = R.string.send,
                override val description: String?,
                override val formattedAmount: String?,
                override val isAmountVisible: Boolean = true,
                override val isPending: Boolean,
                override val amountColorRes: Int? = R.color.transaction_amount_negative_color,
            ) : PayItem() {

                override fun isSameTransaction(other: RecyclerListItem): Boolean {
                    val transaction = other as? PaySendItem ?: return false
                    return signature != null && signature == transaction.signature
                }
            }

            @Parcelize
            data class PayReceiveItem(
                override val id: String?,
                override val signature: String?,
                override val nameRes: Int? = R.string.receive,
                override val description: String?,
                override val formattedAmount: String?,
                override val isAmountVisible: Boolean = true,
                override val isPending: Boolean,
                override val amountColorRes: Int? = R.color.transaction_amount_positive_color,
            ) : PayItem() {

                override fun isSameTransaction(other: RecyclerListItem): Boolean {
                    val transaction = other as? PayReceiveItem ?: return false
                    return signature != null && signature == transaction.signature
                }
            }

            @Parcelize
            data class PaySelfItem(
                override val id: String?,
                override val signature: String?,
                override val nameRes: Int? = R.string.self_transfer,
                override val description: String?,
                override val formattedAmount: String?,
                override val isAmountVisible: Boolean = true,
                override val isPending: Boolean,
                override val amountColorRes: Int? = null
            ) : PayItem() {

                override fun isSameTransaction(other: RecyclerListItem): Boolean {
                    val transaction = other as? PaySendItem ?: return false
                    return signature != null && signature == transaction.signature
                }
            }
        }

        sealed class AssetTransferItem : TransactionItem(), Parcelable {

            sealed class BaseAssetSendItem : AssetTransferItem() {
                @Parcelize
                data class AssetSendItem(
                    override val id: String?,
                    override val signature: String?,
                    override val nameRes: Int? = R.string.send,
                    override val description: String?,
                    override val formattedAmount: String?,
                    override val isAmountVisible: Boolean = true,
                    override val isPending: Boolean,
                    override val amountColorRes: Int? = R.color.transaction_amount_negative_color,
                ) : BaseAssetSendItem() {
                    override fun isSameTransaction(other: RecyclerListItem): Boolean {
                        val transaction = other as? AssetSendItem ?: return false
                        return signature != null && signature == transaction.signature
                    }
                }

                @Parcelize
                data class AssetSendOptOutItem(
                    override val id: String?,
                    override val signature: String?,
                    override val nameRes: Int? = R.string.opt_out,
                    override val description: String?,
                    override val formattedAmount: String?,
                    override val isAmountVisible: Boolean = true,
                    override val isPending: Boolean,
                    override val amountColorRes: Int? = R.color.transaction_amount_negative_color,
                ) : BaseAssetSendItem() {
                    override fun isSameTransaction(other: RecyclerListItem): Boolean {
                        val transaction = other as? AssetSendOptOutItem ?: return false
                        return signature != null && signature == transaction.signature
                    }
                }
            }

            sealed class BaseReceiveItem : AssetTransferItem() {

                @Parcelize
                data class AssetReceiveItem(
                    override val id: String?,
                    override val signature: String?,
                    override val nameRes: Int? = R.string.receive,
                    override val description: String?,
                    override val formattedAmount: String?,
                    override val isAmountVisible: Boolean = true,
                    override val isPending: Boolean,
                    override val amountColorRes: Int? = R.color.transaction_amount_positive_color,
                ) : BaseReceiveItem() {
                    override fun isSameTransaction(other: RecyclerListItem): Boolean {
                        val transaction = other as? AssetReceiveItem ?: return false
                        return signature != null && signature == transaction.signature
                    }
                }

                @Parcelize
                data class AssetReceiveOptOutItem(
                    override val id: String?,
                    override val signature: String?,
                    override val nameRes: Int? = R.string.receive_opt_out,
                    override val description: String?,
                    override val formattedAmount: String?,
                    override val isAmountVisible: Boolean = true,
                    override val isPending: Boolean,
                    override val amountColorRes: Int? = R.color.transaction_amount_positive_color,
                ) : BaseReceiveItem() {
                    override fun isSameTransaction(other: RecyclerListItem): Boolean {
                        val transaction = other as? AssetReceiveOptOutItem ?: return false
                        return signature != null && signature == transaction.signature
                    }
                }
            }

            @Parcelize
            data class AssetOptOutItem(
                override val id: String?,
                override val signature: String?,
                override val nameRes: Int? = R.string.opt_out,
                override val description: String?,
                override val formattedAmount: String?,
                override val isAmountVisible: Boolean = true,
                override val isPending: Boolean,
                override val amountColorRes: Int? = R.color.text_main,
                val closeToAddress: String
            ) : AssetTransferItem() {
                override fun isSameTransaction(other: RecyclerListItem): Boolean {
                    val transaction = other as? AssetOptOutItem ?: return false
                    return signature != null && signature == transaction.signature
                }
            }

            sealed class BaseSelfItem : AssetTransferItem() {

                @Parcelize
                data class AssetSelfItem(
                    override val id: String?,
                    override val signature: String?,
                    override val nameRes: Int? = R.string.self_transfer,
                    override val description: String?,
                    override val formattedAmount: String?,
                    override val isAmountVisible: Boolean = true,
                    override val isPending: Boolean,
                    override val amountColorRes: Int? = null
                ) : BaseSelfItem() {
                    override fun isSameTransaction(other: RecyclerListItem): Boolean {
                        val transaction = other as? AssetSelfItem ?: return false
                        return signature != null && signature == transaction.signature
                    }
                }

                @Parcelize
                data class AssetSelfOptInItem(
                    override val id: String?,
                    override val signature: String?,
                    override val nameRes: Int? = R.string.opt_in_self_transfer,
                    override val description: String?,
                    override val formattedAmount: String?,
                    override val isAmountVisible: Boolean = true,
                    override val isPending: Boolean,
                    override val amountColorRes: Int? = null
                ) : BaseSelfItem() {
                    override fun isSameTransaction(other: RecyclerListItem): Boolean {
                        val transaction = other as? AssetSelfOptInItem ?: return false
                        return signature != null && signature == transaction.signature
                    }
                }
            }
        }

        @Parcelize
        data class AssetConfigurationItem(
            override val id: String?,
            override val signature: String?,
            override val nameRes: Int? = R.string.asset_configuration,
            override val description: String? = null,
            override val formattedAmount: String? = null,
            override val isAmountVisible: Boolean = false,
            override val isPending: Boolean,
            override val amountColorRes: Int? = null,
            val assetId: Long?
        ) : TransactionItem() {

            override fun isSameTransaction(other: RecyclerListItem): Boolean {
                val transaction = other as? AssetConfigurationItem ?: return false
                return signature != null && signature == transaction.signature
            }
        }

        @Parcelize
        data class ApplicationCallItem(
            override val id: String?,
            override val signature: String?,
            override val nameRes: Int = R.string.app_call,
            override val description: String?,
            override val formattedAmount: String? = null,
            override val isAmountVisible: Boolean = false,
            override val isPending: Boolean,
            override val amountColorRes: Int? = null,
            val innerTransactionCount: Int,
            val applicationId: Long?
        ) : TransactionItem() {

            override fun isSameTransaction(other: RecyclerListItem): Boolean {
                val transaction = other as? ApplicationCallItem ?: return false
                return signature != null && signature == transaction.signature
            }
        }

        @Parcelize
        data class UndefinedItem(
            override val id: String? = null,
            override val signature: String? = null,
            override val nameRes: Int? = R.string.undefined,
            override val description: String? = null,
            override val formattedAmount: String? = null,
            override val isAmountVisible: Boolean = false,
            override val isPending: Boolean = false,
            override val amountColorRes: Int? = null
        ) : TransactionItem() {

            override fun isSameTransaction(other: RecyclerListItem): Boolean {
                val transaction = other as? UndefinedItem ?: return false
                return signature != null && signature == transaction.signature
            }
        }
    }
}
