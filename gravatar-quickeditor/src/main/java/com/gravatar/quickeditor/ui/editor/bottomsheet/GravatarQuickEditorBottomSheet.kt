package com.gravatar.quickeditor.ui.editor.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.components.QEDragHandle
import com.gravatar.quickeditor.ui.components.QETopBar
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorPage
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.launch

/**
 * ModalBottomSheet component for the Gravatar Quick Editor that enables the user to
 * modify their Avatar.
 *
 * The bottom sheet is configured to take 70% of the screen height and skips the partially expanded state.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param oAuthParams The OAuth parameters.
 * @param onAvatarSelected The callback for the avatar update result, check [AvatarUpdateResult].
 *                       Can be invoked multiple times while the Quick Editor is open.
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 * @param modalBottomSheetState The state of the bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    oAuthParams: OAuthParams,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    modalBottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val coroutineScope = rememberCoroutineScope()
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    GravatarTheme {
        ModalBottomSheet(
            modifier = Modifier
                .heightIn(min = DEFAULT_PAGE_HEIGHT, max = screenHeightDp.dp * 0.9f),
            onDismissRequest = { onDismiss(GravatarQuickEditorDismissReason.Finished) },
            sheetState = modalBottomSheetState,
            dragHandle = { QEDragHandle() },
        ) {
            Surface {
                Column(
                    modifier = Modifier.navigationBarsPadding(),
                ) {
                    QETopBar(
                        onDoneClick = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                onDismiss(GravatarQuickEditorDismissReason.Finished)
                            }
                        },
                    )
                    GravatarQuickEditorPage(
                        gravatarQuickEditorParams = gravatarQuickEditorParams,
                        oAuthParams = oAuthParams,
                        onDismiss = onDismiss,
                        onAvatarSelected = onAvatarSelected,
                    )
                }
            }
        }
    }
}

internal val DEFAULT_PAGE_HEIGHT = 250.dp