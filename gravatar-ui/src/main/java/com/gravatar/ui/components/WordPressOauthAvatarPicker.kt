package com.gravatar.ui.components

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.core.util.Consumer
import coil.compose.AsyncImage
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Identity
import com.gravatar.restapi.models.SelectAvatar
import com.gravatar.services.AvatarService
import com.gravatar.services.IdentityService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.GravatarImagePickerWrapperListener
import com.gravatar.ui.ImageEditionStyling
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File

@Composable
public fun WordPressImagePicker(
    content: @Composable () -> Unit,
    clientId: String,
    clientSecret: String,
    email: String,
    listener: GravatarImagePickerWrapperListener,
    modifier: Modifier = Modifier,
    imageEditionOptions: ImageEditionStyling = ImageEditionStyling(),
) {
    val context = LocalContext.current
    val activity = context.findComponentActivity()

    val coroutineScope = rememberCoroutineScope()
    var wordpressBearerToken by remember { mutableStateOf<String?>(null, neverEqualPolicy()) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null, neverEqualPolicy()) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
    }
    val uCropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.let { intentData ->
            UCrop.getOutput(intentData)?.let { croppedImageUri ->
                wordpressBearerToken?.let { token ->
                    listener.onAvatarUploadStarted()
                    coroutineScope.launch {
                        when (
                            val response = AvatarService().upload(
                                croppedImageUri.toFile(),
                                Email(email),
                                token,
                            )
                        ) {
                            is Result.Success -> listener.onSuccess(Unit)
                            is Result.Failure -> listener.onError(response.error)
                        }
                    }
                }
            }
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    if (activity != null) {
        DisposableEffect(Unit) {
            val listener = Consumer<Intent> {
                val code = it?.data?.getQueryParameter("code")
                if (code != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        handleAuthorizationCode(code, clientId, clientSecret)?.let { token ->
                            wordpressBearerToken = token
                            showBottomSheet = true
//                            imagePickerLauncher.launch(IMAGE_MIME_TYPE)
                        }
                    }
                } else {
                    // handle error
                    Log.d("WordPressOauthPicker", "Error: ${it?.data?.getQueryParameter("error")}")
                }
            }
            activity.addOnNewIntentListener(listener)
            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }
    }

    Surface(
        modifier = modifier.clickable {
            val responseType = "code"
            val scope = "auth"
            val redirectUri = "wp-oauth-test://authorization-callback"
            val url =
                "https://public-api.wordpress.com/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUri&response_type=$responseType&scope=$scope"
            val customTabIntent: CustomTabsIntent = Builder()
                .build()

            customTabIntent.launchUrl(context, Uri.parse(url))
        },
    ) {
        content()
    }

    if (showBottomSheet) {
        wordpressBearerToken?.let {
            AvatarPickerBottomSheet(email, it) {
                showBottomSheet = false
            }
        }
    }

    selectedImageUri?.let { image ->
        selectedImageUri = null
        launchAvatarCrop(image, uCropLauncher, LocalContext.current, imageEditionOptions)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvatarPickerBottomSheet(email: String, wordPressToken: String, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var avatars by remember { mutableStateOf(emptyList<Avatar>()) }
    var selectedIdentity by remember { mutableStateOf<Identity?>(null) }
    val identityService = IdentityService()

    val onAvatarClicked: (Avatar) -> Unit = { avatar ->
        scope.launch {
            identityService.setAvatar((Email(email).hash().toString()), SelectAvatar {avatar.imageId }, wordPressToken)
            selectedIdentity = identityService.getIdentities(Email(email).hash().toString(), wordPressToken).let {
                when (it) {
                    is Result.Success -> it.value
                    is Result.Failure -> null
                }
            }
        }
    }

    DisposableEffect(Unit) {
        scope.launch {
            avatars = identityService.getAvatars(wordPressToken).let {
                when (it) {
                    is Result.Success -> it.value
                    is Result.Failure -> emptyList()
                }
            }
        }

        scope.launch {
            selectedIdentity = identityService.getIdentities(Email(email).hash().toString(), wordPressToken).let {
                when (it) {
                    is Result.Success -> it.value
                    is Result.Failure -> null
                }
            }
        }

        onDispose {
            // cleanup
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        AvatarPicker(avatars, selectedIdentity, onAvatarClicked)
    }
}

@Composable
private fun AvatarPicker(avatars: List<Avatar>, selectedIdentity: Identity?, onAvatarClicked: (Avatar) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(avatars.size) { index ->
            AsyncImage(
                model = "https://www.gravatar.com${avatars[index].imageUrl}",
                contentDescription = "Translated description of what the image contains",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .fillMaxWidth()
                    .then(
                        if (selectedIdentity?.imageId == avatars[index].imageId) {
                            Modifier.border(4.dp, Color.Blue, RoundedCornerShape(14.dp))
                        } else {
                            Modifier
                        },
                    )
                    .clickable {
                        onAvatarClicked(avatars[index])
                    },
            )
        }
    }
}

public fun Context.findComponentActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findComponentActivity()
    else -> null
}

public suspend fun handleAuthorizationCode(code: String, clientId: String, clientSecret: String): String? {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://public-api.wordpress.com")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    val service: WordPressOAuthApi = retrofit.create(WordPressOAuthApi::class.java)
    val result = service.getToken(
        clientId,
        "wp-oauth-test://authorization-callback",
        clientSecret,
        code,
        "authorization_code",
    )

    Log.d("MainActivity", "Token: ${result.body()?.token}")
    return result.body()?.token
}

internal interface WordPressOAuthApi {
    @POST("/oauth2/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String?,
        @Field("redirect_uri") redirectUri: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("code") user: String?,
        @Field("grant_type") grantType: String?,
    ): Response<TokenModel>
}

internal class TokenModel(
    @SerializedName("access_token") val token: String,
    @SerializedName("token_type") val type: String,
)

private fun launchAvatarCrop(
    image: Uri,
    uCropLauncher: ActivityResultLauncher<Intent>,
    context: Context,
    imageEditionOptions: ImageEditionStyling,
) {
    val options = UCrop.Options().apply {
        with(imageEditionOptions) {
            toolbarColor?.let { setToolbarColor(it) }
            statusBarColor?.let { setStatusBarColor(it) }
            toolbarWidgetColor?.let { setToolbarWidgetColor(it) }
        }
        setShowCropGrid(false)
        setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.NONE)
        setCompressionQuality(UCROP_COMPRESSION_QUALITY)
        setCircleDimmedLayer(true)
    }
    uCropLauncher.launch(
        UCrop.of(image, Uri.fromFile(File(context.cacheDir, "cropped_for_gravatar.jpg")))
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .getIntent(context),
    )
}

private const val IMAGE_MIME_TYPE = "image/*"
private const val UCROP_COMPRESSION_QUALITY = 100
