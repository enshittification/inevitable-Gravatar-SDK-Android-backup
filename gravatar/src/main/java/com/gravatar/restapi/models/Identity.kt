package com.gravatar.restapi.models

import com.google.gson.annotations.SerializedName

public class Identity(
    @SerializedName("id")
    public val id: String,
    @SerializedName("email")
    public val email: String,
    @SerializedName("email_hash")
    public val email_hash: String,
    @SerializedName("format")
    public val format: Int,
    @SerializedName("rating")
    public val rating: String,
    @SerializedName("image_id")
    public val image_id: String,
    @SerializedName("image_url")
    public val image_url: String,
)
