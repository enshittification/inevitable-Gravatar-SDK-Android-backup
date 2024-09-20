package com.gravatar.quickeditor.data.models

import com.squareup.moshi.Json

internal data class WordPressOAuthToken(
    @Json(name = "access_token") val token: String,
    @Json(name = "token_type") val type: String,
)
