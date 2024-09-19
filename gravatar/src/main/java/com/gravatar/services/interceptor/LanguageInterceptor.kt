package com.gravatar.services.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class LanguageInterceptor(private val languages: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addLanguageHeader()
                .build(),
        )
    }

    private fun Request.Builder.addLanguageHeader(): Request.Builder {
        return addHeader("Accept-Language", languages)
    }
}
