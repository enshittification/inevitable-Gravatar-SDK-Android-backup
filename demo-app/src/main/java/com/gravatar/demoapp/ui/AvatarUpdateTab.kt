package com.gravatar.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R
import com.gravatar.demoapp.ui.components.GravatarEmailInput
import com.gravatar.services.ErrorType
import com.gravatar.ui.GravatarImagePickerWrapperListener
import com.gravatar.ui.components.WordPressImagePicker


@Composable
fun AvatarUpdateTab(showSnackBar: (String?, Throwable?) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(BuildConfig.DEMO_EMAIL) }
    var isUploading by remember { mutableStateOf(false) }
    val imagePickerWrapperListener = object : GravatarImagePickerWrapperListener {
        override fun onAvatarUploadStarted() {
            isUploading = true
        }

        override fun onSuccess(response: Unit) {
            isUploading = false
            showSnackBar(context.getString(R.string.avatar_update_upload_success_toast), null)
        }

        override fun onError(errorType: ErrorType) {
            isUploading = false
            showSnackBar(context.getString(R.string.avatar_update_upload_failed_toast, errorType), null)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GravatarEmailInput(email = email, onValueChange = { email = it }, Modifier.fillMaxWidth())

        WordPressImagePicker(
            content = {
                UpdateAvatarComposable(isUploading)
            },
            clientId = BuildConfig.DEMO_WORDPRESS_CLIENT_ID,
            clientSecret = BuildConfig.DEMO_WORDPRESS_CLIENT_SECRET,
            email = email, listener =
            imagePickerWrapperListener
        )
    }
}

@Composable
private fun UpdateAvatarComposable(isUploading: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isUploading) {
            CircularProgressIndicator()
        } else {
            Icon(
                Icons.Rounded.AccountCircle,
                contentDescription = "",
                modifier = Modifier.size(128.dp),
            )
            Text(text = stringResource(R.string.update_avatar_button_label))
        }
    }
}

@Preview
@Composable
private fun UpdateAvatarComposablePreview() = UpdateAvatarComposable(false)

@Preview
@Composable
private fun UpdateAvatarLoadingComposablePreview() = UpdateAvatarComposable(true)

@Preview
@Composable
private fun AvatarUpdateTabPreview() = AvatarUpdateTab(showSnackBar = { _, _ -> })
