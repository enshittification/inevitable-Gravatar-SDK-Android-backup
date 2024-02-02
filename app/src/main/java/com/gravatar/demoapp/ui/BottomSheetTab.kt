package com.gravatar.demoapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gravatar.R
import com.gravatar.ui.GravatarBottomSheet

@Composable
fun BottomSheetTab() {
    var openBottomSheet by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { openBottomSheet = true }) {
            Text(stringResource(R.string.open_bottomsheet_button_label))
        }
        GravatarBottomSheet(
            openBottomSheet = openBottomSheet,
            onBottomSheetDismiss = { openBottomSheet = false },
            onContinueClicked = { openBottomSheet = false },
        )
    }
}
