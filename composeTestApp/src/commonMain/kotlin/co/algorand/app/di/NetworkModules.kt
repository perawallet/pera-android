package co.algorand.app.di

import org.koin.dsl.module

val provideHttpClientModules =
    module {
//        single {
//            HttpClient {
//                install(ContentNegotiation) {
//                    json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
//                }
//            }
//        }
        // single { NfdApi(get()) }
    }