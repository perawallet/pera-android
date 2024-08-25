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

package com.algorand.android.foundation.ui

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

fun getCustomClickableSpan(clickableColor: Int, onClick: (View) -> Unit): ClickableSpan {
    return object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = clickableColor
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {
            onClick.invoke(widget)
        }
    }
}

fun getCustomLongClickableSpan(clickableColor: Int, onLongClick: (View) -> Unit): LongClickableSpan {
    return object : LongClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = clickableColor
            ds.isUnderlineText = false
        }

        override fun onLongClick(widget: View) {
            onLongClick.invoke(widget)
        }
    }
}
