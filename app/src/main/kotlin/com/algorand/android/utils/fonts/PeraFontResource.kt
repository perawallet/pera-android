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

package com.algorand.android.utils.fonts

import com.algorand.android.utils.fonts.FontStyleIdentifier.BOLD
import com.algorand.android.utils.fonts.FontStyleIdentifier.MEDIUM

sealed class PeraFontResource {

    protected abstract val parentFontName: String

    abstract fun getFontStyleIdentifiers(): List<FontStyleIdentifier>

    sealed class DmMono : PeraFontResource() {

        override val parentFontName: String
            get() = PARENT_FONT_NAME

        object Medium : DmMono() {
            override fun getFontStyleIdentifiers(): List<FontStyleIdentifier> = listOf(MEDIUM)
        }

        companion object {
            private const val PARENT_FONT_NAME = "dmmono"
        }
    }

    sealed class DmSans : PeraFontResource() {

        override val parentFontName: String
            get() = PARENT_FONT_NAME

        object Bold : DmSans() {
            override fun getFontStyleIdentifiers(): List<FontStyleIdentifier> = listOf(BOLD)
        }

        object Medium : DmSans() {
            override fun getFontStyleIdentifiers(): List<FontStyleIdentifier> = listOf(MEDIUM)
        }

        companion object {
            private const val PARENT_FONT_NAME = "dmsans"
        }
    }
}
