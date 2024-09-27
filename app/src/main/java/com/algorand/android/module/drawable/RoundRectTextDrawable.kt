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

package com.algorand.android.module.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape

class RoundRectTextDrawable(
    private val backgroundColor: Int,
    private val borderPaint: Paint? = null,
    private val radiusAsPx: Float = 0f,
    private val rectBackgroundColor: Int,
    text: String,
    textPaint: Paint,
    height: Int,
    width: Int
) : TextDrawable(text, textPaint, height, width, RectShape()) {

    override fun drawBorder(canvas: Canvas) {
        borderPaint?.run {
            val rect = RectF(bounds)
            canvas.drawRoundRect(rect, radiusAsPx, radiusAsPx, this)
        }
    }

    override fun drawBackground(canvas: Canvas) {
        val backgroundRectF = RectF(bounds)
        val backgroundPaint = Paint().apply {
            color = backgroundColor
        }
        canvas.drawRoundRect(backgroundRectF, radiusAsPx, radiusAsPx, backgroundPaint)
    }

    override fun onDraw(shape: Shape?, canvas: Canvas?, paint: Paint?) {
        super.onDraw(shape, canvas, paint)
        canvas?.drawColor(rectBackgroundColor)
    }
}
