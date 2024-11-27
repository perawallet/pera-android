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

package com.algorand.common.deeplink.parser.query

import com.algorand.common.deeplink.PeraUriBuilder
import com.algorand.common.deeplink.model.NotificationGroupType
import kotlin.test.assertEquals
import org.junit.Test

class NotificationGroupTypeQueryParserTest {

    private val sut = NotificationGroupTypeQueryParser()

    @Test
    fun `EXPECT transaction WHEN deep link is for transaction`() {
        val uri = PeraUriBuilder.create(
            host = "asset",
            path = "transactions"
        )
        val result = sut.parseQuery(uri)

        assertEquals(NotificationGroupType.TRANSACTIONS, result)
    }

    @Test
    fun `EXPECT opt-in WHEN deep link is for opt-in`() {
        val uri = PeraUriBuilder.create(
            host = "asset",
            path = "opt-in"
        )
        val result = sut.parseQuery(uri)

        assertEquals(NotificationGroupType.OPT_IN, result)
    }

    @Test
    fun `EXPECT asset inbox WHEN deep link is for asset inbox`() {
        val uri = PeraUriBuilder.create(
            host = "asset-inbox"
        )
        val result = sut.parseQuery(uri)

        assertEquals(NotificationGroupType.ASSET_INBOX, result)
    }

    @Test
    fun `EXPECT null WHEN deep link is not for any notification group type`() {
        val uri = PeraUriBuilder.create(
            host = "asset",
            path = "opt-out"
        )
        val result = sut.parseQuery(uri)

        assertEquals(null, result)
    }
}
