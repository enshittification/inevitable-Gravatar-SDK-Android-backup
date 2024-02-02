package com.gravatar.demoapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.gravatar.DefaultAvatarImage
import com.gravatar.ImageRating
import com.gravatar.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme
import com.gravatar.demoapp.ui.model.SettingsState
import com.gravatar.emailAddressToGravatarUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GravatarUrlTab(innerPadding: PaddingValues, scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
    var gravatarUrl by remember { mutableStateOf("", neverEqualPolicy()) }
    val defaultErrorMessage = stringResource(R.string.snackbar_unknown_error_message)
    GravatarUrlUtilsContent(
        gravatarUrl,
        modifier = Modifier.padding(innerPadding),
        onGravatarUrlChanged = { gravatarUrl = it },
    ) { errorMessage, exception ->
        onError(scope, snackbarHostState, errorMessage, exception, defaultErrorMessage)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun GravatarUrlUtilsContent(
    gravatarUrl: String,
    modifier: Modifier = Modifier,
    onGravatarUrlChanged: (String) -> Unit,
    onError: (String?, Throwable?) -> Unit,
) {
    var settingsState by remember {
        mutableStateOf(
            SettingsState(
                email = "gravatar@automattic.com",
                size = null,
                defaultAvatarImageEnabled = false,
                selectedDefaultAvatar = DefaultAvatarImage.MONSTER,
                defaultAvatarOptions = DefaultAvatarImage.entries,
                forceDefaultAvatar = false,
                imageRatingEnabled = false,
                imageRating = ImageRating.General,
            ),
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GravatarImageSettings(
                settingsState = settingsState,
                onEmailChanged = { settingsState = settingsState.copy(email = it) },
                onSizeChange = { settingsState = settingsState.copy(size = it) },
                onLoadGravatarClicked = {
                    @Suppress("TooGenericExceptionCaught")
                    try {
                        keyboardController?.hide()
                        onGravatarUrlChanged(
                            emailAddressToGravatarUrl(
                                email = settingsState.email,
                                size = settingsState.size,
                                defaultAvatarImage = if (settingsState.defaultAvatarImageEnabled) {
                                    settingsState.selectedDefaultAvatar
                                } else {
                                    null
                                },
                                forceDefaultAvatarImage = if (settingsState.forceDefaultAvatar) true else null,
                                rating = if (settingsState.imageRatingEnabled) settingsState.imageRating else null,
                            ),
                        )
                    } catch (e: Exception) {
                        onError(null, e.fillInStackTrace())
                    }
                },
                onDefaultAvatarImageEnabledChanged = {
                    settingsState = settingsState.copy(defaultAvatarImageEnabled = it)
                },
                onDefaultAvatarImageChanged = { settingsState = settingsState.copy(selectedDefaultAvatar = it) },
                onForceDefaultAvatarChanged = { settingsState = settingsState.copy(forceDefaultAvatar = it) },
                onImageRatingChanged = { settingsState = settingsState.copy(imageRating = it) },
                onImageRatingEnabledChange = { settingsState = settingsState.copy(imageRatingEnabled = it) },
            )

            if (gravatarUrl.isNotEmpty()) {
                GravatarDivider()

                GravatarGeneratedUrl(gravatarUrl = gravatarUrl)

                GravatarDivider()

                GravatarImage(gravatarUrl = gravatarUrl, onError = onError)
            }
        }
    }
}

@Composable
private fun GravatarDivider() =
    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))

@Composable
private fun GravatarGeneratedUrl(gravatarUrl: String) {
    Text(
        text = stringResource(R.string.gravatar_generated_url_label),
        fontWeight = FontWeight.Bold,
    )
    Text(text = gravatarUrl)
}

@Composable
private fun GravatarImage(gravatarUrl: String, onError: (String?, Throwable?) -> Unit) {
    val forceRefresh = remember { mutableStateOf(0) }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gravatarUrl)
            .memoryCachePolicy(CachePolicy.WRITE_ONLY)
            .diskCachePolicy(CachePolicy.DISABLED)
            .setParameter("forceRefresh", forceRefresh)
            .size(Size.ORIGINAL)
            .build(),
        onState = { state ->
            if (state is AsyncImagePainter.State.Error) {
                onError(state.result.throwable.message, state.result.throwable)
                forceRefresh.value++
            }
        },
    )

    Image(
        painter = painter,
        contentDescription = "",
    )
}

@Composable
private fun GravatarEmailInput(email: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.gravatar_email_input_label)) },
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun GravatarImageSettings(
    settingsState: SettingsState,
    onEmailChanged: (String) -> Unit,
    onSizeChange: (Int?) -> Unit,
    onLoadGravatarClicked: () -> Unit,
    onDefaultAvatarImageEnabledChanged: (Boolean) -> Unit,
    onDefaultAvatarImageChanged: (DefaultAvatarImage) -> Unit,
    onForceDefaultAvatarChanged: (Boolean) -> Unit,
    onImageRatingChanged: (ImageRating) -> Unit,
    onImageRatingEnabledChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GravatarEmailInput(email = settingsState.email, onValueChange = onEmailChanged, Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth()) {
            DropdownMenuWithCheckbox(
                enabled = settingsState.defaultAvatarImageEnabled,
                selectedOption = settingsState.selectedDefaultAvatar,
                onEnabledChanged = onDefaultAvatarImageEnabledChanged,
                onSelectedOptionChange = onDefaultAvatarImageChanged,
                options = settingsState.defaultAvatarOptions,
                labelForOption = { it.style },
                inputLabel = stringResource(R.string.default_avatar_image_label),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
            DropdownMenuWithCheckbox(
                enabled = settingsState.imageRatingEnabled,
                selectedOption = settingsState.imageRating,
                onEnabledChanged = onImageRatingEnabledChange,
                onSelectedOptionChange = onImageRatingChanged,
                options = ImageRating.entries,
                labelForOption = { it.rating },
                inputLabel = stringResource(R.string.image_rating_label),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            GravatarForceDefaultAvatarImage(
                enabled = settingsState.forceDefaultAvatar,
                onEnabledChanged = onForceDefaultAvatarChanged,
                modifier = Modifier.weight(1f),
            )
            TextField(
                value = settingsState.size?.toString() ?: "",
                onValueChange = { value -> onSizeChange(value.toIntOrNull()) },
                label = { Text(stringResource(R.string.gravatar_size_input_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f),
            )
        }
        Button(onClick = onLoadGravatarClicked) { Text(text = "Load Gravatar") }
    }
}

@Composable
fun GravatarForceDefaultAvatarImage(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = enabled,
            onCheckedChange = { onEnabledChanged(!enabled) },
        )
        Text(text = stringResource(R.string.force_default_avatar_image_label))
    }
}

@Preview
@Composable
fun GravatarImageSettingsPreview() {
    GravatarDemoAppTheme {
        GravatarImageSettings(
            settingsState = SettingsState(
                email = "gravatar@automattic.com",
                size = null,
                defaultAvatarImageEnabled = true,
                selectedDefaultAvatar = DefaultAvatarImage.BLANK,
                defaultAvatarOptions = DefaultAvatarImage.entries,
                forceDefaultAvatar = false,
                imageRatingEnabled = false,
                imageRating = ImageRating.General,
            ),
            onEmailChanged = {},
            onSizeChange = {},
            onLoadGravatarClicked = {},
            onDefaultAvatarImageEnabledChanged = {},
            onDefaultAvatarImageChanged = {},
            onForceDefaultAvatarChanged = {},
            onImageRatingChanged = {},
            onImageRatingEnabledChange = {},
        )
    }
}

private fun onError(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    errorMessage: String?,
    throwable: Throwable?,
    defaultErrorMessage: String,
) {
    Log.e("DemoGravatarApp", "${errorMessage.orEmpty()}\n${throwable?.stackTraceToString().orEmpty()}")
    scope.launch {
        snackbarHostState.showSnackbar(
            message = errorMessage ?: throwable?.message ?: defaultErrorMessage,
            duration = SnackbarDuration.Short,
        )
    }
}
