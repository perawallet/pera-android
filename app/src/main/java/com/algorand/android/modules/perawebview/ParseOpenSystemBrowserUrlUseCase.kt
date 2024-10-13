package com.algorand.android.modules.perawebview

import com.algorand.android.discover.common.ui.model.OpenSystemBrowserRequest
import com.algorand.android.modules.peraserializer.PeraSerializer
import javax.inject.Inject

class ParseOpenSystemBrowserUrlUseCase @Inject constructor(
    private val peraSerializer: PeraSerializer
) : ParseOpenSystemBrowserUrl {

    override fun invoke(jsonPayload: String): String? {
        return peraSerializer.fromJson(jsonPayload, OpenSystemBrowserRequest::class.java)?.url
    }
}
