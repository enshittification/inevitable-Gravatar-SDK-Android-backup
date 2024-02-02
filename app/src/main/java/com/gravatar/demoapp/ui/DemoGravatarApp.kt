package com.gravatar.demoapp.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.gravatar.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme

@Composable
fun DemoGravatarApp() {
    GravatarDemoAppTheme {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val titles = listOf(
            stringResource(R.string.tab_title_gravatar_url),
            stringResource(R.string.tab_title_bottomsheet),
        )
        var tabIndex by remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TabRow(selectedTabIndex = tabIndex) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->
            when (tabIndex) {
                0 -> {
                    GravatarUrlTab(innerPadding, scope, snackbarHostState)
                }

                else -> {
                    BottomSheetTab()
                }
            }
        }
    }
}
