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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.algorand.android.utils.walletconnect.getRandomPeerMetaIconResId
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

fun ImageView.loadPeerMetaIcon(url: String?) {
    Glide.with(this)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                val errorDrawable = AppCompatResources.getDrawable(context, getRandomPeerMetaIconResId())
                setImageDrawable(errorDrawable)
                return true
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .circleCrop()
        .into(this)
}

fun ImageView.loadCircularImage(uri: Uri) {
    Glide.with(this)
        .load(uri)
        .circleCrop()
        .into(this)
}

fun Context.loadImage(uri: String, onResourceReady: (Drawable) -> Unit, onLoadFailed: (() -> Unit)? = null) {
    Glide.with(this)
        .load(uri)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFailed?.invoke()
                return true
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                if (resource != null) {
                    onResourceReady(resource)
                } else {
                    onLoadFailed?.invoke()
                }
                return true
            }
        })
        .preload()
}

fun Context.loadImageWithCachedFirst(
    uri: String,
    cachedUri: String,
    onCachedResourceReady: (Drawable) -> Unit,
    onResourceReady: (Drawable) -> Unit,
    onCachedLoadFailed: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null
) {
    Glide.with(this)
        .load(cachedUri)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFailed?.invoke()
                return true
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                if (resource != null) {
                    onCachedResourceReady(resource)
                    this@loadImageWithCachedFirst.loadImage(uri, onResourceReady, onLoadFailed)
                } else {
                    onCachedLoadFailed?.invoke()
                }
                return true
            }
        })
        .preload()
}

fun ImageView.loadImage(
    uri: String,
    placeHolder: Drawable?,
    onResourceReady: (Drawable) -> Unit,
    onLoadFailed: (Drawable?) -> Unit
) {
    Glide.with(this)
        .load(uri)
        .placeholder(placeHolder)
        .listener(
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadFailed.invoke(placeHolder)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    onResourceReady(resource)
                    target.onResourceReady(resource, null)
                    return true
                }
            }
        )
        .into(this)
}

fun Context.copyImageToClipboard(imageUrl: String) {
    Glide.with(this)
        .asBitmap()
        .load(imageUrl)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                copyBitmapToClipboard(this@copyImageToClipboard, resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Handle if needed when the load is cleared
            }
        })
}

fun copyBitmapToClipboard(context: Context, bitmap: Bitmap) {
    try {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val uri = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Copied Image", null)
        val clip = ClipData.newUri(context.contentResolver, "Image", Uri.parse(uri))
        clipboardManager.setPrimaryClip(clip)
    } catch (e: Exception) {
        // Handle exception
    }
}
