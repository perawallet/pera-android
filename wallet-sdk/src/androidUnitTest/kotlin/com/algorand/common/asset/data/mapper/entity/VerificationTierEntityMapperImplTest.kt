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

package com.algorand.common.asset.data.mapper.entity

import com.algorand.common.asset.data.database.model.VerificationTierEntity
import com.algorand.common.asset.data.model.VerificationTierResponse
import org.junit.Assert.assertEquals
import org.junit.Test

internal class VerificationTierEntityMapperImplTest {

    private val sut = VerificationTierEntityMapperImpl()

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val responseList = listOf(
            VerificationTierResponse.VERIFIED,
            VerificationTierResponse.UNVERIFIED,
            VerificationTierResponse.TRUSTED,
            VerificationTierResponse.SUSPICIOUS,
            VerificationTierResponse.UNKNOWN,
            null
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            VerificationTierEntity.VERIFIED,
            VerificationTierEntity.UNVERIFIED,
            VerificationTierEntity.TRUSTED,
            VerificationTierEntity.SUSPICIOUS,
            VerificationTierEntity.UNKNOWN,
            VerificationTierEntity.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
