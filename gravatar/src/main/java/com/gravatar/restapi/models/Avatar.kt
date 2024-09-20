/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */
package com.gravatar.restapi.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Objects

/**
 * An avatar that the user has already uploaded to their Gravatar account.
 *
 * @param imageId Unique identifier for the image.
 * @param imageUrl Image URL
 * @param rating Rating associated with the image.
 * @param updatedDate Date and time when the image was last updated.
 * @param altText Alternative text description of the image.
 * @param selected Whether the image is currently selected as the provided selected email's avatar.
 */

public class Avatar private constructor(
    // Unique identifier for the image.
    @Json(name = "image_id")
    public val imageId: kotlin.String,
    // Image URL
    @Json(name = "image_url")
    public val imageUrl: kotlin.String,
    // Rating associated with the image.
    @Json(name = "rating")
    public val rating: Avatar.Rating,
    // Date and time when the image was last updated.
    @Json(name = "updated_date")
    public val updatedDate: String,
    // Alternative text description of the image.
    @Json(name = "alt_text")
    public val altText: kotlin.String,
    // Whether the image is currently selected as the provided selected email's avatar.
    @Json(name = "selected")
    public val selected: kotlin.Boolean? = null,
) {
    /**
     * Rating associated with the image.
     *
     * Values: G,PG,R,X
     */
    @JsonClass(generateAdapter = false)
    public enum class Rating(public val value: kotlin.String) {
        @Json(name = "G")
        G("G"),

        @Json(name = "PG")
        PG("PG"),

        @Json(name = "R")
        R("R"),

        @Json(name = "X")
        X("X"),
    }

    override fun toString(): String = "Avatar(imageId=$imageId, imageUrl=$imageUrl, rating=$rating, updatedDate=$updatedDate, altText=$altText, selected=$selected)"

    override fun equals(other: Any?): Boolean = other is Avatar &&
        imageId == other.imageId &&
        imageUrl == other.imageUrl &&
        rating == other.rating &&
        updatedDate == other.updatedDate &&
        altText == other.altText &&
        selected == other.selected

    override fun hashCode(): Int = Objects.hash(imageId, imageUrl, rating, updatedDate, altText, selected)

    public class Builder {
        // Unique identifier for the image.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var imageId: kotlin.String? = null

        // Image URL
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var imageUrl: kotlin.String? = null

        // Rating associated with the image.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var rating: Avatar.Rating? = null

        // Date and time when the image was last updated.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var updatedDate: String? = null

        // Alternative text description of the image.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var altText: kotlin.String? = null

        // Whether the image is currently selected as the provided selected email's avatar.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var selected: kotlin.Boolean? = null

        public fun setImageId(imageId: kotlin.String?): Builder = apply { this.imageId = imageId }

        public fun setImageUrl(imageUrl: kotlin.String?): Builder = apply { this.imageUrl = imageUrl }

        public fun setRating(rating: Avatar.Rating?): Builder = apply { this.rating = rating }

        public fun setUpdatedDate(updatedDate: String?): Builder = apply { this.updatedDate = updatedDate }

        public fun setAltText(altText: kotlin.String?): Builder = apply { this.altText = altText }

        public fun setSelected(selected: kotlin.Boolean?): Builder = apply { this.selected = selected }

        public fun build(): Avatar = Avatar(imageId!!, imageUrl!!, rating!!, updatedDate!!, altText!!, selected)
    }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun Avatar(initializer: Avatar.Builder.() -> Unit): Avatar {
    return Avatar.Builder().apply(initializer).build()
}
