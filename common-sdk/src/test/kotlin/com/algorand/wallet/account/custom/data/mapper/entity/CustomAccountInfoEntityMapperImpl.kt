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

package com.algorand.wallet.account.custom.data.mapper.entity

import com.algorand.wallet.account.custom.data.database.model.CustomAccountInfoEntity
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomAccountInfoEntityMapperImplTest {

    private lateinit var sut: CustomAccountInfoEntityMapperImpl

    @Before
    fun setup() {
        sut = CustomAccountInfoEntityMapperImpl()
    }

    @Test
    fun  `EXPECT correct entity WHEN domain model has valid data`() {
        val customAccountInfo = CustomAccountInfo(
            address = "TESTADDRESS123456789",
            customName = "Test Account",
            orderIndex = 3,
            isBackedUp = true
        )

        val result = sut.invoke(customAccountInfo)

        val expectedEntity = CustomAccountInfoEntity(
            algoAddress = "TESTADDRESS123456789",
            customName = "Test Account",
            orderIndex = 3,
            isBackedUp = true
        )
        assertEquals(expectedEntity, result)
    }
}