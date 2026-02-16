/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.addaccount.joint.transaction.viewmodel

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DefaultJointAccountTransactionProcessorTest {

    private val processor = DefaultJointAccountTransactionProcessor()

    @Test
    fun `EXPECT null WHEN signRequestId is null in validateConfirmTransaction`() {
        val preview = createTestPreview()

        val result = processor.validateConfirmTransaction(preview, null)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN rawTransactions is empty in validateConfirmTransaction`() {
        val preview = createTestPreview(rawTransactions = emptyList())

        val result = processor.validateConfirmTransaction(preview, "request_id")

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN no unsigned accounts exist`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = emptyList(),
            unsignedLedgerParticipantAddresses = emptyList()
        )

        val result = processor.validateConfirmTransaction(preview, "request_id")

        assertNull(result)
    }

    @Test
    fun `EXPECT ConfirmTransactionData WHEN validation passes with local accounts`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = listOf("ADDR1"),
            unsignedLedgerParticipantAddresses = emptyList()
        )

        val result = processor.validateConfirmTransaction(preview, "request_id")

        assertNotNull(result)
        assertEquals("request_id", result?.requestId)
        assertTrue(result?.hasUnsignedLocalAccounts == true)
        assertTrue(result?.hasUnsignedLedgerAccounts == false)
    }

    @Test
    fun `EXPECT ConfirmTransactionData WHEN validation passes with ledger accounts`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = emptyList(),
            unsignedLedgerParticipantAddresses = listOf("LEDGER_ADDR")
        )

        val result = processor.validateConfirmTransaction(preview, "request_id")

        assertNotNull(result)
        assertTrue(result?.hasUnsignedLocalAccounts == false)
        assertTrue(result?.hasUnsignedLedgerAccounts == true)
    }

    @Test
    fun `EXPECT updated preview WHEN signers are signed`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Pending),
            createTestSignerItem("ADDR2", JointAccountSignatureStatus.Pending)
        )
        val preview = createTestPreview(
            signedCount = 0,
            requiredSignatureCount = 2,
            signerAccounts = signerAccounts
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, listOf("ADDR1"))

        assertEquals(1, result.signedCount)
        assertEquals(JointAccountSignatureStatus.Signed, result.signerAccounts[0].signatureStatus)
        assertEquals(JointAccountSignatureStatus.Pending, result.signerAccounts[1].signatureStatus)
        assertTrue(result.hasCurrentUserAlreadySigned)
    }

    @Test
    fun `EXPECT unsigned local list to keep not signed addresses`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = listOf("ADDR1", "ADDR2")
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, listOf("ADDR1"))

        assertEquals(listOf("ADDR2"), result.unsignedLocalParticipantAddresses)
    }

    @Test
    fun `EXPECT unsigned local list unchanged WHEN signing fails`() {
        val preview = createTestPreview(
            signedCount = 0,
            unsignedLocalParticipantAddresses = listOf("ADDR1", "ADDR2")
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, emptyList())

        assertEquals(listOf("ADDR1", "ADDR2"), result.unsignedLocalParticipantAddresses)
        assertEquals(0, result.signedCount)
    }

    @Test
    fun `EXPECT Completed state WHEN all signatures are collected`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Pending)
        )
        val preview = createTestPreview(
            signedCount = 1,
            requiredSignatureCount = 2,
            signerAccounts = signerAccounts
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, listOf("ADDR1"))

        assertEquals(JointAccountTransactionState.Completed, result.transactionState)
    }

    @Test
    fun `EXPECT PendingSignatures state WHEN not all signatures collected`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Pending)
        )
        val preview = createTestPreview(
            signedCount = 0,
            requiredSignatureCount = 3,
            signerAccounts = signerAccounts
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, listOf("ADDR1"))

        assertEquals(JointAccountTransactionState.PendingSignatures, result.transactionState)
    }

    @Test
    fun `EXPECT first local address WHEN finding decline participant`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = listOf("LOCAL_ADDR"),
            unsignedLedgerParticipantAddresses = listOf("LEDGER_ADDR")
        )

        val result = processor.findDeclineParticipantAddress(preview)

        assertEquals("LOCAL_ADDR", result)
    }

    @Test
    fun `EXPECT first ledger address WHEN no local addresses for decline`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = emptyList(),
            unsignedLedgerParticipantAddresses = listOf("LEDGER_ADDR")
        )

        val result = processor.findDeclineParticipantAddress(preview)

        assertEquals("LEDGER_ADDR", result)
    }

    @Test
    fun `EXPECT null WHEN no addresses available for decline`() {
        val preview = createTestPreview(
            unsignedLocalParticipantAddresses = emptyList(),
            unsignedLedgerParticipantAddresses = emptyList()
        )

        val result = processor.findDeclineParticipantAddress(preview)

        assertNull(result)
    }

    @Test
    fun `EXPECT LedgerSignData WHEN ledger signer available`() {
        val signerAccounts = listOf(
            createTestSignerItem(
                address = "LEDGER_ADDR",
                status = JointAccountSignatureStatus.Pending,
                isLedger = true,
                ledgerBluetoothAddress = "AA:BB:CC:DD",
                ledgerAccountIndex = 0
            )
        )
        val preview = createTestPreview(signerAccounts = signerAccounts)

        val result = processor.createLedgerSignData("request_id", listOf("raw_tx"), preview)

        assertNotNull(result)
        assertEquals("request_id", result?.signRequestId)
        assertEquals("LEDGER_ADDR", result?.accountAddress)
        assertEquals("AA:BB:CC:DD", result?.ledgerBluetoothAddress)
        assertEquals(0, result?.ledgerAccountIndex)
    }

    @Test
    fun `EXPECT null WHEN no ledger signer available`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Pending, isLedger = false)
        )
        val preview = createTestPreview(signerAccounts = signerAccounts)

        val result = processor.createLedgerSignData("request_id", listOf("raw_tx"), preview)

        assertNull(result)
    }

    @Test
    fun `EXPECT Completed state WHEN processing loaded preview with enough signatures`() {
        val preview = createTestPreview(
            signedCount = 2,
            requiredSignatureCount = 2,
            transactionState = JointAccountTransactionState.PendingSignatures
        )

        val result = processor.processLoadedPreview(preview)

        assertEquals(JointAccountTransactionState.Completed, result.transactionState)
    }

    @Test
    fun `EXPECT showProgress false on pending signers WHEN processLoadedPreview completes transaction`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Signed),
            createTestSignerItem("ADDR2", JointAccountSignatureStatus.Pending)
        )
        val preview = createTestPreview(
            signedCount = 2,
            requiredSignatureCount = 2,
            signerAccounts = signerAccounts,
            transactionState = JointAccountTransactionState.PendingSignatures
        )

        val result = processor.processLoadedPreview(preview)

        assertEquals(false, result.signerAccounts[1].showProgress)
    }

    @Test
    fun `EXPECT showProgress false on pending signers WHEN all signatures collected after signing`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Signed),
            createTestSignerItem("ADDR2", JointAccountSignatureStatus.Pending),
            createTestSignerItem("ADDR3", JointAccountSignatureStatus.Pending)
        )
        val preview = createTestPreview(
            signedCount = 1,
            requiredSignatureCount = 2,
            signerAccounts = signerAccounts
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, listOf("ADDR2"))

        assertEquals(JointAccountTransactionState.Completed, result.transactionState)
        assertEquals(false, result.signerAccounts[2].showProgress)
    }

    @Test
    fun `EXPECT showProgress true on pending signers WHEN transaction not completed after signing`() {
        val signerAccounts = listOf(
            createTestSignerItem("ADDR1", JointAccountSignatureStatus.Pending),
            createTestSignerItem("ADDR2", JointAccountSignatureStatus.Pending)
        )
        val preview = createTestPreview(
            signedCount = 0,
            requiredSignatureCount = 3,
            signerAccounts = signerAccounts
        )

        val result = processor.createUpdatedPreviewAfterSigning(preview, listOf("ADDR1"))

        assertEquals(JointAccountTransactionState.PendingSignatures, result.transactionState)
        assertEquals(true, result.signerAccounts[1].showProgress)
    }

    @Test
    fun `EXPECT same state WHEN processing loaded preview without enough signatures`() {
        val preview = createTestPreview(
            signedCount = 1,
            requiredSignatureCount = 2,
            transactionState = JointAccountTransactionState.PendingSignatures
        )

        val result = processor.processLoadedPreview(preview)

        assertEquals(JointAccountTransactionState.PendingSignatures, result.transactionState)
    }

    @Test
    fun `EXPECT ShowPendingSignatures WHEN transaction completed in post signing action`() {
        val preview = createTestPreview(signedCount = 2, requiredSignatureCount = 2)
        val data = JointAccountTransactionProcessor.ConfirmTransactionData(
            requestId = "request_id",
            preview = preview,
            hasUnsignedLocalAccounts = false,
            hasUnsignedLedgerAccounts = true
        )

        val result = processor.determinePostSigningAction(data, preview, "request_id")

        assertTrue(result is JointAccountTransactionProcessor.PostSigningAction.ShowPendingSignatures)
    }

    @Test
    fun `EXPECT TriggerLedgerSigning WHEN ledger accounts available and not completed`() {
        val signerAccounts = listOf(
            createTestSignerItem(
                address = "LEDGER_ADDR",
                status = JointAccountSignatureStatus.Pending,
                isLedger = true,
                ledgerBluetoothAddress = "AA:BB:CC:DD",
                ledgerAccountIndex = 0
            )
        )
        val preview = createTestPreview(
            signedCount = 1,
            requiredSignatureCount = 3,
            signerAccounts = signerAccounts
        )
        val data = JointAccountTransactionProcessor.ConfirmTransactionData(
            requestId = "request_id",
            preview = preview,
            hasUnsignedLocalAccounts = false,
            hasUnsignedLedgerAccounts = true
        )

        val result = processor.determinePostSigningAction(data, preview, "request_id")

        assertTrue(result is JointAccountTransactionProcessor.PostSigningAction.TriggerLedgerSigning)
    }

    private fun createTestPreview(
        rawTransactions: List<String> = listOf("raw_tx_1"),
        signedCount: Int = 1,
        requiredSignatureCount: Int = 2,
        unsignedLocalParticipantAddresses: List<String> = listOf("ADDR1"),
        unsignedLedgerParticipantAddresses: List<String> = emptyList(),
        signerAccounts: List<JointAccountSignerItem> = emptyList(),
        transactionState: JointAccountTransactionState = JointAccountTransactionState.PendingSignatures
    ): JointAccountTransactionViewState {
        return JointAccountTransactionViewState(
            jointAccountDisplayName = AccountDisplayName(
                accountAddress = "JOINT_ADDR",
                primaryDisplayName = "Joint Account",
                secondaryDisplayName = null
            ),
            jointAccountIconPreview = createMockIconDrawablePreview(),
            recipientAddress = "RECIPIENT_ADDR",
            recipientShortAddress = "RECIP...ADDR",
            amount = "10.00",
            convertedAmount = "$100.00",
            transactionFee = "0.001",
            transactionState = transactionState,
            signerAccounts = signerAccounts,
            signedCount = signedCount,
            requiredSignatureCount = requiredSignatureCount,
            hasCurrentUserAlreadySigned = false,
            shouldShowPendingSignaturesDirectly = false,
            rawTransactions = rawTransactions,
            unsignedLocalParticipantAddresses = unsignedLocalParticipantAddresses,
            unsignedLedgerParticipantAddresses = unsignedLedgerParticipantAddresses
        )
    }

    private fun createTestSignerItem(
        address: String,
        status: JointAccountSignatureStatus,
        isLedger: Boolean = false,
        ledgerBluetoothAddress: String? = null,
        ledgerAccountIndex: Int? = null
    ): JointAccountSignerItem {
        return JointAccountSignerItem(
            accountAddress = address,
            accountDisplayName = AccountDisplayName(
                accountAddress = address,
                primaryDisplayName = address,
                secondaryDisplayName = null
            ),
            accountIconDrawablePreview = createMockIconDrawablePreview(),
            imageUri = null,
            signatureStatus = status,
            showProgress = status == JointAccountSignatureStatus.Pending,
            isLedgerAccount = isLedger,
            ledgerBluetoothAddress = ledgerBluetoothAddress,
            ledgerAccountIndex = ledgerAccountIndex
        )
    }

    private fun createMockIconDrawablePreview(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = android.R.color.black,
            iconTintResId = android.R.color.white,
            iconResId = android.R.drawable.ic_menu_add
        )
    }
}
