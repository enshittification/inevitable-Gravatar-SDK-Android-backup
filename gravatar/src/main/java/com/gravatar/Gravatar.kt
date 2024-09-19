package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer

/**
 * Entry point for initializing the Gravatar SDK.
 */
public object Gravatar {

    /**
     * Initializes the Gravatar SDK with the given API key.
     *
     * @param gravatarApiKey The API key to use when making requests to the Gravatar backend.
     */
    @Deprecated(
        message = "",
        replaceWith = ReplaceWith("Gravatar.gravatarApiKey()")
    )
    public fun initialize(gravatarApiKey: String) {
        GravatarSdkContainer.instance.apiKey = gravatarApiKey
    }

    /**
     * Sets the Gravatar API key for REST API calls
     *
     * @param gravatarApiKey The API key to use when making requests to the Gravatar backend.
     */
    public fun gravatarApiKey(gravatarApiKey: String): Gravatar {
        GravatarSdkContainer.instance.apiKey = gravatarApiKey
        return this
    }

    /**
     * Sets the accepted languages list used for the REST API calls.
     * The languages should be represented as a comma separated list of languages tags, for example "pl-PL,en-US".
     * If you don't won't the responses to be localized you can skip this.
     *
     * @param languageTags A comma separated list of languages tags.
     */
    public fun acceptedLanguages(languageTags: String): Gravatar {
        GravatarSdkContainer.instance.acceptedLanguages = languageTags
        return this
    }
}
