package com.gravatar.quickeditor.ui.avatarpicker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.data.repository.IdentityAvatars
import com.gravatar.quickeditor.ui.components.EmailLabel
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.components.SelectableAvatar
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.bottomsheet.DEFAULT_PAGE_HEIGHT
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState
import java.time.Instant

@Composable
internal fun AvatarPicker(
    email: Email,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    viewModel: AvatarPickerViewModel = viewModel(factory = AvatarPickerViewModelFactory(email)),
) {
    val uiState by viewModel.uiState.collectAsState()

    AvatarPicker(uiState = uiState, onAvatarSelected = onAvatarSelected)
}

@Composable
internal fun AvatarPicker(uiState: AvatarPickerUiState, onAvatarSelected: (AvatarUpdateResult) -> Unit) {
    GravatarTheme {
        Surface(Modifier.fillMaxWidth()) {
            Column {
                EmailLabel(
                    email = uiState.email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                )
                ProfileCard(
                    profile = uiState.profile,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                when {
                    uiState.isLoading -> Box(
                        modifier = Modifier
                            .height(DEFAULT_PAGE_HEIGHT)
                            .fillMaxWidth(),
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    uiState.error -> Text(text = "There was an error loading avatars", textAlign = TextAlign.Center)
                    uiState.avatars != null ->
                        AvatarsSection(
                            uiState.avatars,
                            onAvatarSelected,
                            Modifier.padding(horizontal = 16.dp),
                        )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AvatarsSection(
    avatars: List<AvatarUi>,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(16.dp),
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.avatar_picker_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.avatar_picker_description),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 4.dp),
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 24.dp)) {
                items(items = avatars, key = { it.avatarId }) { avatarModel ->
                    when (avatarModel) {
                        is AvatarUi.Uploaded -> SelectableAvatar(
                            imageUrl = avatarModel.avatar.fullUrl,
                            isSelected = avatarModel.isSelected,
                            onAvatarClicked = {
                                onAvatarSelected(AvatarUpdateResult(avatarModel.avatar.fullUrl.toUri()))
                            },
                            modifier = Modifier.size(96.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun AvatarPickerPreview() {
    AvatarPicker(
        uiState = AvatarPickerUiState(
            email = Email("henry.a.wallace@example.com"),
            profile = ComponentState.Loaded(
                defaultProfile(
                    hash = "tetet",
                    displayName = "Henry Wallace",
                    location = "London, UK",
                ),
            ),
            identityAvatars = IdentityAvatars(
                avatars = listOf(
                    Avatar {
                        imageUrl = "/image/url"
                        format = 0
                        imageId = "1"
                        rating = "G"
                        altText = "alt"
                        isCropped = true
                        updatedDate = Instant.now()
                    },
                ),
                selectedAvatarId = "1",
            ),
        ),
        onAvatarSelected = { },
    )
}

@Composable
@PreviewLightDark
private fun AvatarPickerLoadingPreview() {
    AvatarPicker(
        uiState = AvatarPickerUiState(
            email = Email("henry.a.wallace@example.com"),
            profile = ComponentState.Loading,
            isLoading = true,
            identityAvatars = null,
        ),
        onAvatarSelected = { },
    )
}

@Composable
@Preview(showBackground = true)
private fun AvatarSectionPreview() {
    GravatarTheme {
        AvatarsSection(
            onAvatarSelected = { },
            avatars = listOf(
                AvatarUi.Uploaded(
                    avatar = Avatar {
                        imageUrl = "/image/url"
                        format = 0
                        imageId = "1"
                        rating = "G"
                        altText = "alt"
                        isCropped = true
                        updatedDate = Instant.now()
                    },
                    isSelected = true,
                ),
            ),
        )
    }
}

private val Avatar.fullUrl: String
    get() = "https://www.gravatar.com$imageUrl?size=200"
