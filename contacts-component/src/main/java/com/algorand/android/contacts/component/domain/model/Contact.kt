package com.algorand.android.contacts.component.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val address: String,
    val name: String,
    val imageUri: String?
) : Parcelable
