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

package com.algorand.android.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.requestFocusAndShowKeyboard() {
    post {
        requestFocus()
        showKeyboard()
    }
}

fun addKeyboardToggleListener(
    rootView: View,
    onKeyboardToggleAction: (shown: Boolean) -> Unit
): KeyboardToggleListener {
    val listener = KeyboardToggleListener(rootView, onKeyboardToggleAction)
    rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    return listener
}

fun KeyboardToggleListener.removeKeyboardToggleListener(rootView: View) {
    return rootView.viewTreeObserver.removeOnGlobalLayoutListener(this@removeKeyboardToggleListener)
}
