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

package com.algorand.wallet.account.info.data.mapper.entity

import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity
import com.algorand.wallet.account.info.domain.model.AssetStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AssetStatusEntityMapperImplTest {

    private lateinit var sut: AssetStatusEntityMapperImpl

    @Before
    fun setup() {
        sut = AssetStatusEntityMapperImpl()
    }

    @Test
    fun `EXPECT PENDING_FOR_REMOVAL entity WHEN AssetStatus is PENDING_FOR_REMOVAL`() {
        val result = sut.invoke(AssetStatus.PENDING_FOR_REMOVAL)

        assertEquals(AssetStatusEntity.PENDING_FOR_REMOVAL, result)
    }

    @Test
    fun `EXPECT PENDING_FOR_ADDITION entity WHEN AssetStatus is PENDING_FOR_ADDITION`() {
        val result = sut.invoke(AssetStatus.PENDING_FOR_ADDITION)

        assertEquals(AssetStatusEntity.PENDING_FOR_ADDITION, result)
    }

    @Test
    fun `EXPECT OWNED_BY_ACCOUNT entity WHEN AssetStatus is OWNED_BY_ACCOUNT`() {
        val result = sut.invoke(AssetStatus.OWNED_BY_ACCOUNT)

        assertEquals(AssetStatusEntity.OWNED_BY_ACCOUNT, result)
    }
}
