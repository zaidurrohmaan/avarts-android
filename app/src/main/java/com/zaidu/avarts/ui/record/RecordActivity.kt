package com.zaidu.avarts.ui.record

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.zaidu.avarts.R
import com.zaidu.avarts.ui.record.RecordViewModelFactory
import com.zaidu.avarts.ui.theme.AvartsTheme
import java.util.concurrent.TimeUnit

class RecordActivity : ComponentActivity() {

    private val viewModel: RecordViewModel by viewModels {
        RecordViewModelFactory(application)
    }

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
        Text(text = formatMovingTime(viewModel.movingTime), fontSize = 72.sp)
        Text(text = "Moving time", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(32.dp))

        val pace = if (viewModel.recordingState == RecordingState.PAUSED) viewModel.averagePace else viewModel.splitPace
        Text(text = formatPace(pace), fontSize = 72.sp)
        if (viewModel.recordingState == RecordingState.PAUSED) {
            Text(text = "Avg. pace (/km)", fontSize = 18.sp)
        } else {
            Text(text = "Split avg. (/km)", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = String.format("%.2f", viewModel.distance / 1000), fontSize = 72.sp)
        Text(text = "Distance (/km)", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (viewModel.recordingState) {
                RecordingState.STOPPED -> {
                    CircularButton(
                        icon = R.drawable.ic_run,
                        size = 64.dp,
                        onClick = {  }
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    CircularButton(
                        icon = R.drawable.ic_play,
                        onClick = { viewModel.onStartClick(context) }
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    CircularButton(
                        icon = R.drawable.ic_list,
                        size = 64.dp,
                        onClick = {  }
                    )
                }
                RecordingState.RECORDING -> {
                    CircularButton(
                        icon = R.drawable.ic_pause,
                        onClick = { viewModel.onPauseClick(context) }
                    )
                }
                RecordingState.PAUSED -> {
                    CircularButton(
                        icon = R.drawable.ic_resume,
                        onClick = { viewModel.onResumeClick(context) }
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    CircularButton(
                        icon = R.drawable.ic_finish,
                        onClick = { viewModel.onFinishClick(context) }
                    )
                }
            }
        }
    }
}

@Composable
fun CircularButton(icon: Int, size: Dp = 100.dp, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .border(1.dp, Color.Gray, CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),

        ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}

private fun formatMovingTime(time: Long): String {
    val hours = TimeUnit.SECONDS.toHours(time)
    val minutes = TimeUnit.SECONDS.toMinutes(time) % 60
    val seconds = time % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

private fun formatPace(pace: Float): String {
    if (pace == 0.0f) return "--:--"
    val minutes = (pace / 60).toInt()
    val seconds = (pace % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    AvartsTheme {
        val application = LocalContext.current.applicationContext as Application
        RecordScreen(RecordViewModel(application))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordingPreview() {
    val application = LocalContext.current.applicationContext as Application
    val viewModel = RecordViewModel(application)
    viewModel.onStartClick(LocalContext.current)
    AvartsTheme {
        RecordScreen(viewModel)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PausedPreview() {
    val application = LocalContext.current.applicationContext as Application
    val viewModel = RecordViewModel(application)
    viewModel.onStartClick(LocalContext.current)
    viewModel.onPauseClick(LocalContext.current)
    AvartsTheme {
        RecordScreen(viewModel)
    }
}
