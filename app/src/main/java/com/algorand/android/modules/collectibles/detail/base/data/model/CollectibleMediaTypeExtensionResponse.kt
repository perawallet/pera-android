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

package com.algorand.android.modules.collectibles.detail.base.data.model

import com.google.gson.annotations.SerializedName

enum class CollectibleMediaTypeExtensionResponse(val extension: String) {

    @SerializedName(".aac")
    AAC(".aac"),

    @SerializedName(".adts")
    ADTS(".adts"),

    @SerializedName(".aif")
    AIF(".aif"),

    @SerializedName(".aifc")
    AIFC(".aifc"),

    @SerializedName(".aiff")
    AIFF(".aiff"),

    @SerializedName(".ass")
    ASS(".ass"),

    @SerializedName(".au")
    AU(".au"),

    @SerializedName(".gif")
    GIF(".gif"),

    @SerializedName(".jpg")
    JPG(".jpg"),

    @SerializedName(".jpeg")
    JPEG(".jpeg"),

    @SerializedName(".loas")
    LOAS(".loas"),

    @SerializedName(".mid")
    MID(".mid"),

    @SerializedName(".midi")
    MIDI(".midi"),

    @SerializedName(".mp2")
    MP2(".mp2"),

    @SerializedName(".mp3")
    MP3(".mp3"),

    @SerializedName(".mp4")
    MP4(".mp4"),

    @SerializedName(".opus")
    OPUS(".opus"),

    @SerializedName(".png")
    PNG(".png"),

    @SerializedName(".ra")
    RA(".ra"),

    @SerializedName(".snd")
    SND(".snd"),

    @SerializedName(".3gp")
    THREE_GP(".3gp"),

    @SerializedName(".3gpp")
    THREE_GPP(".3gpp"),

    @SerializedName(".3g2")
    THREE_G2(".3g2"),

    @SerializedName(".3gpp2")
    THREE_GPP2(".3gpp2"),

    @SerializedName(".wav")
    WAV(".wav"),

    @SerializedName(".webp")
    WEBP(".webp"),

    UNKNOWN(".unknown")
}
