package com.algorand.android.module.account.info.data.helper.querybuilder

internal enum class IndexerAccountFetchRequestExcludes(val query: String) {
    ALL("all"),
    ASSETS("assets"),
    CREATED_ASSETS("created-assets"),
    APPS_LOCAL_STATE("apps-local-state"),
    CREATED_APPS("created-apps"),
    NONE("none")
}
