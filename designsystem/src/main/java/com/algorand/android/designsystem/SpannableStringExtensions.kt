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

package com.algorand.android.designsystem

import android.content.Context
import android.graphics.Typeface
import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.inSpans

fun Appendable.appendSpace() {
    append(" ")
}

fun SpannableStringBuilder.addAlgorandIcon(context: Context) {
    inSpans(CenteredImageSpan(context, R.drawable.ic_algo_sign)) {
        appendSpace()
    }
    appendSpace()
}

fun TextView.setXmlStyledString(
    @StringRes stringResId: Int,
    @ColorRes colorResId: Int = R.color.black,
    onUrlClick: ((String) -> Unit)? = null
) {
    val xmlText = context.resources.getText(stringResId)
    if (xmlText !is SpannedString) {
        text = xmlText
        return
    }

    val spannableString = SpannableStringBuilder(xmlText)
    xmlText.getSpans(0, xmlText.length, Annotation::class.java).forEach { annotation ->
        when (annotation.key) {
            "type" -> {
                val span: CharacterStyle? = when (annotation.value) {
                    "bold" -> StyleSpan(Typeface.BOLD)
                    "color" -> ForegroundColorSpan(ContextCompat.getColor(context, colorResId))
                    "underline" -> UnderlineSpan()
                    "verified-icon" -> CenteredImageSpan(context, R.drawable.ic_shield_check_small)
                    else -> null
                }
                if (span != null) {
                    // This is an empty annotation tag body check. Annotation must contain at least one character.
                    val spanStartIndex = spannableString.getSpanStart(annotation)
                    val spanEndIndex = spannableString.getSpanEnd(annotation)
                    if (spanStartIndex in 0..spannableString.length && spanEndIndex in 0..spannableString.length) {
                        spannableString.setSpan(span, spanStartIndex, spanEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        Unit // TODO recordException(InvalidAnnotationException(xmlText.toString(), annotation.value))
                    }
                }
            }
            "url" -> {
                setOnClickListener { onUrlClick?.invoke(annotation.value) }
            }
        }
    }
    text = spannableString
}

fun Context.getXmlStyledString(annotatedString: AnnotatedString): CharSequence {
    with(annotatedString) {
        return getXmlStyledString(stringResId, replacementList, customAnnotationList)
    }
}

fun Context.getXmlStyledString(
    @StringRes stringResId: Int,
    replacementList: List<Pair<CharSequence, CharSequence>> = emptyList(),
    customAnnotations: List<Pair<CharSequence, Any>> = emptyList()
): CharSequence {
    val xmlText = resources.getText(stringResId)
    return replaceAnnotatedStringsWithTheirReplacements(this, xmlText, replacementList, customAnnotations)
}

private fun SpannableStringBuilder.replaceAnnotation(
    annotation: Annotation,
    replacementValue: CharSequence
) {
    replace(
        getSpanStart(annotation),
        getSpanEnd(annotation),
        replacementValue
    )
}

private fun SpannableStringBuilder.replaceIconAnnotation(
    context: Context,
    annotation: Annotation,
    replacementIconResIdValue: CharSequence
) {
    setSpan(
        CenteredImageSpan(context, replacementIconResIdValue.toString().toInt()),
        getSpanStart(annotation),
        getSpanEnd(annotation),
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
}

private fun SpannableStringBuilder.applySpan(span: Any, annotation: Annotation) {
    setSpan(span, getSpanStart(annotation), getSpanEnd(annotation), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

private fun SpannableStringBuilder.applyFontAnnotation(context: Context, annotation: Annotation) {
    val fontName = annotation.value
    val typeface = ResourcesCompat.getFont(
        context,
        context.resources.getIdentifier(fontName, "font", context.packageName)
    )
    if (typeface != null) {
        applySpan(CustomTypefaceSpan(typeface), annotation)
    }
}

fun SpannableStringBuilder.setFont(fontTypeface: Typeface?) {
    if (fontTypeface != null) {
        setSpan(CustomTypefaceSpan(fontTypeface), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun SpannableStringBuilder.setColor(textColor: Int) {
    setSpan(ForegroundColorSpan(textColor), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setTextSize(textSize: Int) {
    setSpan(AbsoluteSizeSpan(textSize), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun Context.getXmlStyledPluralString(pluralAnnotatedString: PluralAnnotatedString): CharSequence {
    with(pluralAnnotatedString) {
        return getXmlStyledPluralString(pluralStringResId, quantity, replacementList, customAnnotationList)
    }
}

fun Context.getXmlStyledPluralString(
    @PluralsRes stringResId: Int,
    quantity: Int,
    replacementList: List<Pair<CharSequence, CharSequence>> = emptyList(),
    customAnnotations: List<Pair<CharSequence, Any>> = emptyList()
): CharSequence {
    val xmlText: CharSequence = resources.getQuantityString(stringResId, quantity, quantity)
    return replaceAnnotatedStringsWithTheirReplacements(this, xmlText, replacementList, customAnnotations)
}

private fun replaceAnnotatedStringsWithTheirReplacements(
    context: Context,
    xmlText: CharSequence,
    replacementList: List<Pair<CharSequence, CharSequence>> = emptyList(),
    customAnnotations: List<Pair<CharSequence, Any>> = emptyList()
): CharSequence {
    if (xmlText !is SpannedString) {
        return xmlText
    }
    val spannableString = SpannableStringBuilder(xmlText)
    xmlText.getSpans(0, xmlText.length, Annotation::class.java).forEach { annotation ->
        when (annotation.key) {
            "type" -> {
                when (annotation.value) {
                    "bold" -> StyleSpan(Typeface.BOLD)
                    "underline" -> UnderlineSpan()
                    // a string resource supports only one `color` annotation
                    "color" -> {
                        val colorResId = customAnnotations.find { (key, _) -> key == annotation.value }?.second
                        ForegroundColorSpan(ContextCompat.getColor(context, colorResId.toString().toInt()))
                    }

                    "text_size" -> {
                        // a string resource supports only one `text_size` annotation
                        val textSizeResId = customAnnotations.find { (key, _) -> key == annotation.value }?.second
                        AbsoluteSizeSpan(context.resources.getDimensionPixelSize(textSizeResId.toString().toInt()))
                    }

                    else -> null
                }?.let { span ->
                    spannableString.applySpan(span, annotation)
                }
            }
            "font" -> {
                spannableString.applyFontAnnotation(context, annotation)
            }
            "replacement" -> {
                replacementList.find { (key, _) ->
                    key == annotation.value
                }?.let { (_, replacementValue) ->
                    spannableString.replaceAnnotation(annotation, replacementValue)
                }
            }
            "iconReplacement" -> {
                replacementList.find { (key, _) ->
                    key == annotation.value
                }?.let { (_, replacementValue) ->
                    spannableString.replaceIconAnnotation(context, annotation, replacementValue)
                }
            }
            "custom" -> {
                val customAnnotation = customAnnotations.find { it.first == annotation.value }
                if (customAnnotation != null) {
                    spannableString.applySpan(customAnnotation.second, annotation)
                }
            }
        }
    }
    return spannableString
}
