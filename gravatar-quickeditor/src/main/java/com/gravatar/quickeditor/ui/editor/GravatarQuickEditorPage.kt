package com.gravatar.quickeditor.ui.editor

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.navigation.QuickEditorPage
import com.gravatar.quickeditor.ui.oauth.OAuthPage
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.quickeditor.ui.splash.SplashPage
import com.gravatar.types.Email

/**
 * Raw composable component for the Quick Editor.
 * This can be used to show the Quick Editor in Activity, Fragment or BottomSheet.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param oAuthParams The OAuth parameters.
 * @param onAvatarSelected The callback for the avatar update result, check [AvatarUpdateResult].
 *                       Can be invoked multiple times while the Quick Editor is open
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@Composable
internal fun GravatarQuickEditorPage(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    oAuthParams: OAuthParams,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = QuickEditorPage.SPLASH.name,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(QuickEditorPage.SPLASH.name) {
            SplashPage(email = gravatarQuickEditorParams.email) { isAuthorized ->
                if (isAuthorized) {
                    navController.navigate(QuickEditorPage.EDITOR.name)
                } else {
                    navController.navigate(QuickEditorPage.OAUTH.name)
                }
            }
        }
        composable(QuickEditorPage.OAUTH.name, enterTransition = { EnterTransition.None }) {
            OAuthPage(
                appName = gravatarQuickEditorParams.appName,
                oAuthParams = oAuthParams,
                email = gravatarQuickEditorParams.email,
                onAuthError = { onDismiss(GravatarQuickEditorDismissReason.OauthFailed) },
                onAuthSuccess = { navController.navigate(QuickEditorPage.EDITOR.name) },
            )
        }
        composable(QuickEditorPage.EDITOR.name) {
            AvatarPicker(gravatarQuickEditorParams.email, onAvatarSelected)
        }
    }
}

@Preview
@Composable
private fun ProfileQuickEditorPagePreview() {
    val oAuthParams = OAuthParams {
        clientSecret = "clientSecret"
        clientId = "clientId"
        redirectUri = "redirectUri"
    }
    val gravatarQuickEditorParams = GravatarQuickEditorParams {
        appName = "FancyMobileApp"
        email = Email("email")
    }
    GravatarQuickEditorPage(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        oAuthParams = oAuthParams,
        onAvatarSelected = {},
        onDismiss = {},
    )
}