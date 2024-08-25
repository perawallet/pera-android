package com.algorand.android.shared_db.converters

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity

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
