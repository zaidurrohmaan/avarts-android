package com.zaidu.avarts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.zaidu.avarts.ui.theme.AvartsTheme

class RecordActivity : ComponentActivity() {

    private val viewModel: RecordViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.hasPermission = isGranted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.hasPermission = true
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        setContent {
            AvartsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecordScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun RecordScreen(viewModel: RecordViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "00:00:00", fontSize = 72.sp)
        Text(text = "Moving time", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "--:--", fontSize = 72.sp)
        Text(text = "Split avg. (/km)", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "0.00", fontSize = 72.sp)
        Text(text = "Distance (/km)", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (viewModel.recordingState) {
                RecordingState.STOPPED -> {
                    Button(onClick = { viewModel.onStartClick(context) }) {
                        Text(text = "Start")
                    }
                    Button(onClick = { /* TODO: Go to List Activity */ }) {
                        Text(text = "List")
                    }
                }
                RecordingState.RECORDING -> {
                    Button(onClick = { viewModel.onPauseClick(context) }) {
                        Text(text = "Pause")
                    }
                }
                RecordingState.PAUSED -> {
                    Button(onClick = { viewModel.onResumeClick(context) }) {
                        Text(text = "Resume")
                    }
                    Button(onClick = { viewModel.onFinishClick(context) }) {
                        Text(text = "Finish")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    AvartsTheme {
        RecordScreen(RecordViewModel())
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordingPreview() {
    val viewModel = RecordViewModel()
    viewModel.onStartClick(LocalContext.current)
    AvartsTheme {
        RecordScreen(viewModel)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PausedPreview() {
    val viewModel = RecordViewModel()
    viewModel.onStartClick(LocalContext.current)
    viewModel.onPauseClick(LocalContext.current)
    AvartsTheme {
        RecordScreen(viewModel)
    }
}
