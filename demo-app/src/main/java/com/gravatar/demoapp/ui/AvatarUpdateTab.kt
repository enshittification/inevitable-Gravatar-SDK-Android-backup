package com.gravatar.demoapp.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R
import com.gravatar.demoapp.ui.activity.QuickEditorTestActivity
import com.gravatar.demoapp.ui.components.GravatarEmailInput
import com.gravatar.demoapp.ui.components.GravatarPasswordInput
import com.gravatar.quickeditor.GravatarQuickEditor
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.types.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AvatarUpdateTab(modifier: Modifier = Modifier) {
    var userEmail by remember { mutableStateOf(BuildConfig.DEMO_EMAIL) }
    var userToken by remember { mutableStateOf(BuildConfig.DEMO_BEARER_TOKEN) }
    var useToken by remember { mutableStateOf(false) }
    var tokenVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var avatarUrl: String? by remember { mutableStateOf(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var pickerContentLayout: AvatarPickerContentLayout by rememberSaveable(
        stateSaver = AvatarPickerContentLayoutSaver,
    ) {
        mutableStateOf(AvatarPickerContentLayout.Horizontal)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GravatarEmailInput(email = userEmail, onValueChange = { userEmail = it }, Modifier.fillMaxWidth())
            Row {
                GravatarPasswordInput(
                    password = userToken,
                    passwordIsVisible = tokenVisible,
                    enabled = useToken,
                    onValueChange = { value -> userToken = value },
                    onVisibilityChange = { visible -> tokenVisible = visible },
                    label = { Text(text = "Bearer token") },
                    modifier = Modifier.weight(1f),
                )
                Checkbox(checked = useToken, onCheckedChange = { useToken = it })
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RadioButton(
                    selected = pickerContentLayout == AvatarPickerContentLayout.Horizontal,
                    onClick = { pickerContentLayout = AvatarPickerContentLayout.Horizontal },
                )
                Text(
                    text = "Horizontal",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { pickerContentLayout = AvatarPickerContentLayout.Horizontal },
                )
                RadioButton(
                    selected = pickerContentLayout == AvatarPickerContentLayout.Vertical,
                    onClick = { pickerContentLayout = AvatarPickerContentLayout.Vertical },
                )
                Text(
                    text = "Vertical",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { pickerContentLayout = AvatarPickerContentLayout.Vertical },
                )
            }
            UpdateAvatarComposable(
                modifier = Modifier.clickable {
                    keyboardController?.hide()
                    showBottomSheet = true
                },
                isUploading = false,
                avatarUrl = avatarUrl,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    scope.launch {
                        GravatarQuickEditor.logout(Email(userEmail))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            ) {
                Text(text = "Logout user")
            }
            if (BuildConfig.DEBUG) {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, QuickEditorTestActivity::class.java))
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                ) {
                    Text(text = "Test with Activity without Compose")
                }
            }
        }
    }
    if (showBottomSheet) {
        val applicationName = stringResource(id = R.string.app_name)
        val authenticationMethod = if (useToken) {
            AuthenticationMethod.Bearer(userToken)
        } else {
            AuthenticationMethod.OAuth(
                OAuthParams {
                    clientId = BuildConfig.DEMO_WORDPRESS_CLIENT_ID
                    clientSecret = BuildConfig.DEMO_WORDPRESS_CLIENT_SECRET
                    redirectUri = BuildConfig.DEMO_WORDPRESS_REDIRECT_URI
                },
            )
        }
        GravatarQuickEditorBottomSheet(
            gravatarQuickEditorParams = GravatarQuickEditorParams {
                appName = applicationName
                email = Email(userEmail)
                avatarPickerContentLayout = pickerContentLayout
            },
            authenticationMethod = authenticationMethod,
            onAvatarSelected = remember {
                { result ->
                    avatarUrl = result.avatarUri.toString()
                }
            },
            onDismiss = remember {
                {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    showBottomSheet = false
                }
            },
        )
    }
}

@Composable
private fun UpdateAvatarComposable(isUploading: Boolean, avatarUrl: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (isUploading) {
            CircularProgressIndicator()
        } else {
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar Image",
                    modifier = Modifier
                        .size(128.dp)
                        .padding(8.dp)
                        .clip(CircleShape),
                )
            } else {
                Icon(
                    Icons.Rounded.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(128.dp),
                )
            }
            Text(text = stringResource(R.string.update_avatar_button_label))
        }
    }
}

private val AvatarPickerContentLayoutSaver: Saver<AvatarPickerContentLayout, String> = run {
    val horizontalKey = "horizontal"
    val verticalKey = "vertical"
    Saver(
        save = { value ->
            when (value) {
                AvatarPickerContentLayout.Horizontal -> horizontalKey
                AvatarPickerContentLayout.Vertical -> verticalKey
            }
        },
        restore = { value ->
            when (value) {
                horizontalKey -> AvatarPickerContentLayout.Horizontal
                else -> AvatarPickerContentLayout.Vertical
            }
        },
    )
}

@Preview
@Composable
private fun UpdateAvatarComposablePreview() = UpdateAvatarComposable(false, null)

@Preview
@Composable
private fun UpdateAvatarLoadingComposablePreview() = UpdateAvatarComposable(true, null)

@Preview
@Composable
private fun AvatarUpdateTabPreview() = AvatarUpdateTab()
