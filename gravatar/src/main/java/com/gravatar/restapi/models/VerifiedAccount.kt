/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */
package com.gravatar.restapi.models

import com.squareup.moshi.Json
import java.util.Objects

/**
 * A verified account on a user's profile.
 *
 * @param serviceType The type of the service.
 * @param serviceLabel The name of the service.
 * @param serviceIcon The URL to the service's icon.
 * @param url The URL to the user's profile on the service.
 */

public class VerifiedAccount private constructor(
    // The type of the service.
    @Json(name = "service_type")
    public val serviceType: kotlin.String,
    // The name of the service.
    @Json(name = "service_label")
    public val serviceLabel: kotlin.String,
    // The URL to the service's icon.
    @Json(name = "service_icon")
    public val serviceIcon: java.net.URI,
    // The URL to the user's profile on the service.
    @Json(name = "url")
    public val url: java.net.URI,
) {
    override fun toString(): String = "VerifiedAccount(serviceType=$serviceType, serviceLabel=$serviceLabel, serviceIcon=$serviceIcon, url=$url)"

    override fun equals(other: Any?): Boolean = other is VerifiedAccount &&
        serviceType == other.serviceType &&
        serviceLabel == other.serviceLabel &&
        serviceIcon == other.serviceIcon &&
        url == other.url

    override fun hashCode(): Int = Objects.hash(serviceType, serviceLabel, serviceIcon, url)

    public class Builder {
        // The type of the service.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var serviceType: kotlin.String? = null

        // The name of the service.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var serviceLabel: kotlin.String? = null

        // The URL to the service's icon.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var serviceIcon: java.net.URI? = null

        // The URL to the user's profile on the service.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var url: java.net.URI? = null

        public fun setServiceType(serviceType: kotlin.String?): Builder = apply { this.serviceType = serviceType }

        public fun setServiceLabel(serviceLabel: kotlin.String?): Builder = apply { this.serviceLabel = serviceLabel }

        public fun setServiceIcon(serviceIcon: java.net.URI?): Builder = apply { this.serviceIcon = serviceIcon }

        public fun setUrl(url: java.net.URI?): Builder = apply { this.url = url }

        public fun build(): VerifiedAccount = VerifiedAccount(serviceType!!, serviceLabel!!, serviceIcon!!, url!!)
    }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun VerifiedAccount(initializer: VerifiedAccount.Builder.() -> Unit): VerifiedAccount {
    return VerifiedAccount.Builder().apply(initializer).build()
}
