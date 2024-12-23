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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.data.database.model.VerificationTierEntity
import com.algorand.common.asset.data.model.VerificationTierResponse
import com.algorand.common.asset.data.model.VerificationTierResponse.SUSPICIOUS
import com.algorand.common.asset.data.model.VerificationTierResponse.TRUSTED
import com.algorand.common.asset.data.model.VerificationTierResponse.UNKNOWN
import com.algorand.common.asset.data.model.VerificationTierResponse.UNVERIFIED
import com.algorand.common.asset.data.model.VerificationTierResponse.VERIFIED
import com.algorand.common.asset.domain.model.VerificationTier
import org.junit.Assert.assertEquals
import org.junit.Test

internal class VerificationTierMapperImplTest {

    private val sut = VerificationTierMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val responseList = listOf<VerificationTierResponse>(
            VERIFIED,
            UNVERIFIED,
            TRUSTED,
            SUSPICIOUS,
            UNKNOWN
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            VerificationTier.VERIFIED,
            VerificationTier.UNVERIFIED,
            VerificationTier.TRUSTED,
            VerificationTier.SUSPICIOUS,
            VerificationTier.UNKNOWN
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        val entityList = listOf<VerificationTierEntity>(
            VerificationTierEntity.VERIFIED,
            VerificationTierEntity.UNVERIFIED,
            VerificationTierEntity.TRUSTED,
            VerificationTierEntity.SUSPICIOUS,
            VerificationTierEntity.UNKNOWN
        )

        val result = entityList.map { sut(it) }

        val expected = listOf(
            VerificationTier.VERIFIED,
            VerificationTier.UNVERIFIED,
            VerificationTier.TRUSTED,
            VerificationTier.SUSPICIOUS,
            VerificationTier.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
