package com.algorand.android.module.account.info.data.helper.querybuilder

internal class IndexerAccountFetchRequestExcludesQueryBuilder private constructor() {

    private val excludes = mutableListOf<String>()

    fun addExclude(exclude: IndexerAccountFetchRequestExcludes): IndexerAccountFetchRequestExcludesQueryBuilder {
        excludes.add(exclude.query)
        return this
    }

    fun build(): String {
        return excludes.joinToString(",")
    }

    companion object {
        fun newBuilder(): IndexerAccountFetchRequestExcludesQueryBuilder {
            return IndexerAccountFetchRequestExcludesQueryBuilder()
        }
    }
}
