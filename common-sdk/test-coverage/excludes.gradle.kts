val excludedClasses = listOf(
    // android
    "*.R",
    "*.R$*",
    "*.BuildConfig",
    "*.Manifest*",
    "android.*.*.*",
    // dagger
    "*.*_MembersInjector",
    "*.Dagger*Component",
    "*.Dagger*Component\$Builder",
    "*.*Module_*Factory",
    "*.di.module.*",
    "*.*_Factory*",
    "*.*Module*",
    "*.*Dagger*",
    "*.*Hilt*",
    "*.*GeneratedInjector*",
    "*.codegen.*",
    "*.*_Impl*",
    // kotlin
    "*.*Component*",
    "*.*BR*",
    "*.*\$Lambda$*",
    "*.*Companion*",
    "*.*MembersInjector*",
    "*.*_Provide*Factory*",
    "*.*Extensions*",
    // Pera
    "*.di.*",
    "*.domain.model.*",
    "*.data.model.*",
    "*.data.service.*",
    "*.database.model.*",
    "*.database.dao.*",
    "*PeraResult*"
)

val excludedPackages = listOf(
    "com.algorand.wallet.viewmodel"
)

extra["excludedClasses"] = excludedClasses
extra["excludedPackages"] = excludedPackages
