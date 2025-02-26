package com.gravatar.quickeditor.ui.oauth

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.components.ErrorSection
import com.gravatar.quickeditor.ui.editor.bottomsheet.DEFAULT_PAGE_HEIGHT
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun OAuthPage(
    email: Email,
    oAuthParams: OAuthParams,
    onAuthSuccess: () -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OAuthViewModel = viewModel(factory = OAuthViewModel.Factory),
) {
    val context = LocalContext.current
    val activity = context.findComponentActivity()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        OAuthAction.AuthorizationSuccess -> onAuthSuccess()
                        OAuthAction.AuthorizationFailure -> onAuthError()
                        OAuthAction.StartOAuth -> launchCustomTab(context, oAuthParams, email)
                    }
                }
            }
        }
    }

    if (activity != null) {
        DisposableEffect(Unit) {
            val listener = Consumer<Intent> { newIntent ->
                val code = newIntent.data?.getQueryParameter("code")
                if (code != null) {
                    viewModel.fetchAccessToken(
                        code = code,
                        oAuthParams = oAuthParams,
                        email = email,
                    )
                } else {
                    onAuthError()
                }
            }
            activity.addOnNewIntentListener(listener)
            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }
    }

    OauthPage(uiState, email, oAuthParams, modifier)
}

@Composable
internal fun OauthPage(uiState: OAuthUiState, email: Email, oAuthParams: OAuthParams, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    GravatarTheme {
        Surface {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(DEFAULT_PAGE_HEIGHT),
            ) {
                if (uiState.isAuthorizing) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    ErrorSection(
                        title = stringResource(R.string.login_required),
                        message = stringResource(R.string.login_required_message),
                        buttonText = stringResource(id = R.string.avatar_picker_session_error_cta),
                        onButtonClick = { launchCustomTab(context, oAuthParams, email) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center),
                    )
                }
            }
        }
    }
}

private fun launchCustomTab(context: Context, oauthParams: OAuthParams, email: Email) {
    val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
        .build()
    customTabIntent.launchUrl(
        context,
        Uri.parse(WordPressOauth.buildUrl(oauthParams.clientId, oauthParams.redirectUri, email)),
    )
}

private fun Context.findComponentActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findComponentActivity()
    else -> null
}

@Preview
@Composable
private fun OAuthPagePreview() {
    GravatarTheme {
        OauthPage(
            uiState = OAuthUiState(),
            email = Email("email"),
            oAuthParams = OAuthParams {
                clientId = "client_id"
                clientSecret = "client_secret"
                redirectUri = "redirect_uri"
            },
        )
    }
}
