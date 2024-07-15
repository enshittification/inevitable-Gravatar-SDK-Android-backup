package com.gravatar.restapi.models

import com.google.gson.annotations.SerializedName

public class Avatar(
    @SerializedName("image_id")
    public val image_id: String,
    @SerializedName("image_url")
    public val image_url: String,
    @SerializedName("is_cropped")
    public val is_cropped: Boolean,
    @SerializedName("format")
    public val format: Int,
    @SerializedName("rating")
    public val rating: String,
    @SerializedName("updated_date")
    public val updated_date: String,
    @SerializedName("altText")
    public val altText: String,
)
