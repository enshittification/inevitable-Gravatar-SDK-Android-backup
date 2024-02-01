package com.gravatar.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.R
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GravatarBottomSheet(
    openBottomSheet: Boolean,
    onBottomSheetDismiss: () -> Unit,
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    scope.launch {
        if (openBottomSheet) {
            bottomSheetState.expand()
        } else {
            bottomSheetState.hide()
        }
    }

    if (!openBottomSheet) return
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onBottomSheetDismiss,
        modifier = modifier,
    ) {
        Box(Modifier.navigationBarsPadding()) {
            Column {
                GravatarBottomSheetContent(
                    onContinueClicked,
                    Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp),
                )
                LearnMoreAboutGravatarProfiles(Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
private fun GravatarCharacteristicRow(
    iconPainter: Painter,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Column {
            Image(painter = iconPainter, contentDescription = "")
        }
        Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = description, fontSize = 13.sp)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GravatarCharacteristicRowPreview() {
    GravatarCharacteristicRow(
        iconPainter = painterResource(R.drawable.update_once_icon),
        title = stringResource(R.string.gravatar_bottom_sheet_update_once_title),
        description = stringResource(R.string.gravatar_bottom_sheet_update_once_description),
    )
}

@Composable
private fun GravatarBottomSheetContent(onContinueClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.gravatar_logo), contentDescription = "")
            Text("Gravatar", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
        }
        Text(
            stringResource(R.string.gravatar_bottom_sheet_security_info),
            modifier = Modifier.padding(top = 24.dp),
        )
        GravatarCharacteristicRow(
            iconPainter = painterResource(R.drawable.update_once_icon),
            title = stringResource(R.string.gravatar_bottom_sheet_update_once_title),
            description = stringResource(R.string.gravatar_bottom_sheet_update_once_description),
            modifier = Modifier.padding(top = 24.dp),
        )
        GravatarCharacteristicRow(
            iconPainter = painterResource(R.drawable.digital_identity_icon),
            title = stringResource(R.string.gravatar_bottom_sheet_digital_identity_title),
            description = stringResource(R.string.gravatar_bottom_sheet_digital_identity_description),
            modifier = Modifier.padding(top = 24.dp),
        )
        Button(
            onClick = onContinueClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            Text(stringResource(R.string.continue_label))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GravatarBottomSheetContentPreview() {
    GravatarBottomSheetContent({ })
}

@Composable
private fun LearnMoreAboutGravatarProfiles(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.LightGray.copy(alpha = 0.2f))
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.gravatar_logo),
                contentDescription = "",
                modifier = Modifier.size(20.dp),
            )
            Text(
                stringResource(R.string.gravatar_profiles_info_label),
                fontSize = 13.sp,
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LearnMoreAboutGravatarProfilesPreview() {
    LearnMoreAboutGravatarProfiles()
}
