package com.gravatar.services

import com.gravatar.logger.Logger
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Identity
import com.gravatar.restapi.models.SelectAvatar
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import com.gravatar.di.container.GravatarSdkContainer.Companion.instance as GravatarSdkDI

public class IdentityService(okHttpClient: OkHttpClient? = null) {
    private companion object {
        const val LOG_TAG = "IdentityService"
    }

    private val service = GravatarSdkDI.getGravatarV3ServiceWithoutApiKey(okHttpClient)

    public suspend fun getAvatars(oauthToken: String): Result<List<Avatar>, ErrorType> = runCatchingService {
        withContext(GravatarSdkDI.dispatcherIO) {
            val response = service.getAvatars("Bearer $oauthToken")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Result.Success(data)
                } else {
                    Result.Failure(ErrorType.UNKNOWN)
                }
            } else {
                // Log the response body for debugging purposes if the response is not successful
                Logger.w(
                    LOG_TAG,
                    "Network call unsuccessful trying to get Gravatar avatars: $response.body",
                )
                Result.Failure(errorTypeFromHttpCode(response.code()))
            }
        }
    }

    public suspend fun getIdentities(email: String, oauthToken: String): Result<Identity, ErrorType> =
        runCatchingService {
            withContext(GravatarSdkDI.dispatcherIO) {
                val response = service.getIdentity(email, "Bearer $oauthToken")
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        Result.Success(data)
                    } else {
                        Result.Failure(ErrorType.UNKNOWN)
                    }
                } else {
                    // Log the response body for debugging purposes if the response is not successful
                    Logger.w(
                        LOG_TAG,
                        "Network call unsuccessful trying to get Gravatar Identities: $response.body",
                    )
                    Result.Failure(errorTypeFromHttpCode(response.code()))
                }
            }
        }

    public suspend fun setAvatar(email: String, avatarId: SelectAvatar, oauthToken: String): Result<Unit, ErrorType> =
        runCatchingService {
            withContext(GravatarSdkDI.dispatcherIO) {
                val response = service.setIdentityAvatar(
                    emailHash = email,
                    authorization = "Bearer $oauthToken",
                    selectAvatar = avatarId,
                )
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        Result.Success(data)
                    } else {
                        Result.Failure(ErrorType.UNKNOWN)
                    }
                } else {
                    // Log the response body for debugging purposes if the response is not successful
                    Logger.w(
                        LOG_TAG,
                        "Network call unsuccessful trying to get Gravatar Identities: $response.body",
                    )
                    Result.Failure(errorTypeFromHttpCode(response.code()))
                }
            }
        }
}
