package com.algorand.android.shared_db

import androidx.room.*
import com.algorand.android.shared_db.AccountSharedDatabase.Companion.DATABASE_VERSION
import com.algorand.android.shared_db.accountinformation.dao.*
import com.algorand.android.shared_db.accountinformation.model.*
import com.algorand.android.shared_db.accountsorting.dao.AccountIndexDao
import com.algorand.android.shared_db.accountsorting.model.AccountIndexEntity
import com.algorand.android.shared_db.asb.dao.AlgorandSecureBackUpDao
import com.algorand.android.shared_db.asb.model.AlgorandSecureBackUpEntity
import com.algorand.android.shared_db.assetdetail.dao.*
import com.algorand.android.shared_db.assetdetail.model.*
import com.algorand.android.shared_db.contact.dao.ContactDao
import com.algorand.android.shared_db.contact.model.ContactEntity
import com.algorand.android.shared_db.converters.*
import com.algorand.android.shared_db.custominfo.dao.CustomInfoDao
import com.algorand.android.shared_db.notification.dao.NotificationFilterDao
import com.algorand.android.shared_db.notification.model.NotificationFilterEntity

@Database(
    entities = [
        CustomInfoEntity::class,
        AccountInformationEntity::class,
        AssetHoldingEntity::class,
        AssetDetailEntity::class,
        CollectibleEntity::class,
        CollectibleMediaEntity::class,
        CollectibleTraitEntity::class,
        AlgorandSecureBackUpEntity::class,
        AccountIndexEntity::class,
        ContactEntity::class,
        NotificationFilterEntity::class
    ],
    version = DATABASE_VERSION
)
@TypeConverters(
    BigIntegerTypeConverter::class,
    BigDecimalTypeConverter::class,
    CollectibleMediaTypeTypeConverter::class,
    CollectibleStandardTypeTypeConverter::class,
    VerificationTierTypeConverter::class,
    CollectibleMediaTypeExtensionTypeConverter::class
)
internal abstract class AccountSharedDatabase : RoomDatabase() {

    abstract fun customInfoDao(): CustomInfoDao
    abstract fun accountInformationDao(): AccountInformationDao
    abstract fun assetHoldingDao(): AssetHoldingDao
    abstract fun assetDetailDao(): AssetDetailDao
    abstract fun collectibleDao(): CollectibleDao
    abstract fun collectibleMediaDao(): CollectibleMediaDao
    abstract fun collectibleTraitDao(): CollectibleTraitDao
    abstract fun algorandSecureBackUpDao(): AlgorandSecureBackUpDao
    abstract fun accountIndexDao(): AccountIndexDao
    abstract fun contactDao(): ContactDao
    abstract fun notificationFilterDao(): NotificationFilterDao

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "account_shared_database"
    }
}
