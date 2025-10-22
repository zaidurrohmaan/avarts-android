package com.zaidu.avarts.ui.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.zaidu.avarts.data.database.entities.ActivitySummary
import com.zaidu.avarts.ui.theme.AvartsTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ListActivity : ComponentActivity() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvartsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ListScreen(viewModel: ListViewModel) {
    val summaries = viewModel.summaries.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(summaries.value) { summary ->
            ActivityCard(summary) {
                val file = File(summary.gpxFilePath)
                val uri = FileProvider.getUriForFile(context, "com.zaidu.avarts.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/gpx+xml")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ActivityCard(summary: ActivitySummary, onViewGpxClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = summary.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = summary.location, style = MaterialTheme.typography.bodySmall)
            Text(text = formatDate(summary.timestamp), style = MaterialTheme.typography.bodySmall)
            if (summary.caption?.isNotBlank() == true) {
                Text(text = summary.caption, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Distance: %.2f km".format(summary.distance / 1000))
            Text(text = "Moving Time: ${formatMovingTime(summary.movingTime)}")
            Text(text = "Avg. Pace: ${formatPace(summary.avgPace)} /km")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onViewGpxClick) {
                Text(text = "View GPX")
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
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
