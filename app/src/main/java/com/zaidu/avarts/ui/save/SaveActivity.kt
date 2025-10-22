package com.zaidu.avarts.ui.save

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaidu.avarts.ui.theme.AvartsTheme

class SaveActivity : ComponentActivity() {

    private val viewModel: SaveViewModel by viewModels { SaveViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvartsTheme {
                SaveScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveScreen(viewModel: SaveViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("Title your activity") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.caption,
            onValueChange = { viewModel.caption = it },
            label = { Text("How'd it go? Share more about your activity") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onSaveClick() },
            enabled = viewModel.title.isNotBlank()
        ) {
            Text(text = "Save activity")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.onDiscardClick() }) {
            Text(text = "Discard activity")
        }

        if (viewModel.showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissDiscard() },
                title = { Text("Discard Activity") },
                text = { Text("Are you sure you want to discard this activity?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onConfirmDiscard() }) {
                        Text("Discard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onDismissDiscard() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}