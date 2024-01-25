package com.gravatar.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme
import com.gravatar.emailAddressToGravatarUrl

@Composable
fun DemoGravatarApp() {
    var email by remember { mutableStateOf("gravatarMailHere@gravatar.com") }
    var gravatarUrl by remember { mutableStateOf("") }
    var avatarSize by remember { mutableStateOf<Int?>(null) }

    GravatarDemoAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GravatarImageSettings(
                email = email,
                size = avatarSize,
                onEmailChanged = { email = it },
                onSizeChange = { avatarSize = it },
                onLoadGravatarClicked = { gravatarUrl = emailAddressToGravatarUrl(email = email, size = avatarSize) },
            )

            if (gravatarUrl.isNotEmpty()) {
                GravatarDivider()

                GravatarGeneratedUrl(gravatarUrl = gravatarUrl)

                GravatarDivider()

                GravatarImage(gravatarUrl = gravatarUrl)
            }
        }
    }
}

@Composable
fun GravatarDivider() {
    Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun GravatarGeneratedUrl(gravatarUrl: String) {
    Text(
        text = stringResource(R.string.gravatar_generated_url_label),
        fontWeight = FontWeight.Bold,
    )
    Text(text = gravatarUrl)
}

@Composable
fun GravatarImage(gravatarUrl: String) {
    AsyncImage(
        model = gravatarUrl,
        contentDescription = null,
    )
}

@Composable
fun GravatarImageSettings(
    email: String,
    size: Int? = null,
    onEmailChanged: (String) -> Unit,
    onSizeChange: (Int?) -> Unit,
    onLoadGravatarClicked: () -> Unit,
) {
    GravatarEmailInput(email = email, onValueChange = onEmailChanged, Modifier.fillMaxWidth())
    Row(Modifier.padding(vertical = 8.dp)) {
        TextField(
            value = size?.toString() ?: "",
            onValueChange = { value -> onSizeChange(value.toIntOrNull()) },
            label = { Text(stringResource(R.string.gravatar_size_input_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
    Button(onClick = onLoadGravatarClicked) {
        Text(text = "Load Gravatar")
    }
}

@Composable
fun GravatarEmailInput(email: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.gravatar_email_input_label)) },
        maxLines = 1,
        modifier = modifier,
    )
}
