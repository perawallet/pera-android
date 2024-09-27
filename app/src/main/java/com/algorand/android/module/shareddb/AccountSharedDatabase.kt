package com.algorand.android.module.shareddb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.algorand.android.module.shareddb.AccountSharedDatabase.Companion.DATABASE_VERSION
import com.algorand.android.module.shareddb.accountinformation.dao.AccountInformationDao
import com.algorand.android.module.shareddb.accountinformation.dao.AssetHoldingDao
import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity
import com.algorand.android.module.shareddb.accountinformation.model.AssetHoldingEntity
import com.algorand.android.module.shareddb.accountsorting.dao.AccountIndexDao
import com.algorand.android.module.shareddb.accountsorting.model.AccountIndexEntity
import com.algorand.android.module.shareddb.asb.dao.AlgorandSecureBackUpDao
import com.algorand.android.module.shareddb.asb.model.AlgorandSecureBackUpEntity
import com.algorand.android.module.shareddb.assetdetail.dao.AssetDetailDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleMediaDao
import com.algorand.android.module.shareddb.assetdetail.dao.CollectibleTraitDao
import com.algorand.android.module.shareddb.assetdetail.model.AssetDetailEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity
import com.algorand.android.module.shareddb.contact.dao.ContactDao
import com.algorand.android.module.shareddb.contact.model.ContactEntity
import com.algorand.android.module.shareddb.converters.BigDecimalTypeConverter
import com.algorand.android.module.shareddb.converters.BigIntegerTypeConverter
import com.algorand.android.module.shareddb.converters.CollectibleMediaTypeExtensionTypeConverter
import com.algorand.android.module.shareddb.converters.CollectibleMediaTypeTypeConverter
import com.algorand.android.module.shareddb.converters.CollectibleStandardTypeTypeConverter
import com.algorand.android.module.shareddb.converters.VerificationTierTypeConverter
import com.algorand.android.module.shareddb.custominfo.dao.CustomInfoDao
import com.algorand.android.module.shareddb.notification.dao.NotificationFilterDao
import com.algorand.android.module.shareddb.notification.model.NotificationFilterEntity

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
