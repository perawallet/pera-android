package com.algorand.android.module.shareddb.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.algorand.android.module.shareddb.assetdetail.model.VerificationTierEntity

@ProvidedTypeConverter
internal object VerificationTierTypeConverter {

    @TypeConverter
    fun verificationTierToString(input: VerificationTierEntity): String {
        return input.toString()
    }

    @TypeConverter
    fun stringToVerificationTier(input: String): VerificationTierEntity {
        return VerificationTierEntity.valueOf(input)
    }
}
