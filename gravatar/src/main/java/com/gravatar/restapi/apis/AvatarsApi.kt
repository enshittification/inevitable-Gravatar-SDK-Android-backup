/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */
package com.gravatar.restapi.apis

import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.SetEmailAvatarRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AvatarsApi {
    /**
     * List avatars
     * Retrieves a list of available avatars for the authenticated user.
     * Responses:
     *  - 200: Successful retrieval of avatars
     *
     * @param selectedEmail The email address used to determine which avatar is selected. The &#39;selected&#39; attribute in the avatar list will be set to &#39;true&#39; for the avatar associated with this email. (optional, default to "null")
     * @return [kotlin.collections.List<Avatar>]
     */
    @GET("me/avatars")
    suspend fun getAvatars(
        @Query("selected_email") selectedEmail: kotlin.String? = "null",
    ): Response<kotlin.collections.List<Avatar>>

    /**
     * Set avatar for the hashed email
     * Sets the avatar for the provided email hash.
     * Responses:
     *  - 204: Avatar successfully set
     *
     * @param imageId Image ID of the avatar to set as the provided hashed email avatar.
     * @param setEmailAvatarRequest Avatar selection details
     * @return [Unit]
     */
    @POST("me/avatars/{imageId}/email")
    suspend fun setEmailAvatar(
        @Path("imageId") imageId: kotlin.String,
        @Body setEmailAvatarRequest: SetEmailAvatarRequest,
    ): Response<Unit>

    /**
     * Upload new avatar image
     * Uploads a new avatar image for the authenticated user.
     * Responses:
     *  - 200: Avatar uploaded successfully
     *  - 400: Invalid request
     *
     * @param `data` The avatar image file
     * @return [Avatar]
     */
    @Multipart
    @POST("me/avatars")
    suspend fun uploadAvatar(
        @Part `data`: MultipartBody.Part,
    ): Response<Avatar>
}
