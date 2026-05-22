package com.warith.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.warith.app.data.repository.LocalSourceRepository
import com.warith.app.ui.widget.WarithWidgetReceiver
import com.warith.app.util.HistoryManager
import com.warith.app.util.MidnightRotationWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val historyManager = remember { HistoryManager(context) }
    val repository = remember { LocalSourceRepository(context, historyManager) }
    val sources = remember { repository.getAllSources() }

    var autoRotationEnabled by remember { mutableStateOf(historyManager.isAutoRotationEnabled()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Warith",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Loaded Sources",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(sources) { source ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = source.sourceName, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "${source.entries.size} entries",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Daily Auto-rotation", modifier = Modifier.weight(1f))
            Switch(
                checked = autoRotationEnabled,
                onCheckedChange = {
                    autoRotationEnabled = it
                    historyManager.setAutoRotationEnabled(it)
                    if (it) {
                        MidnightRotationWorker.schedule(context)
                    } else {
                        MidnightRotationWorker.cancel(context)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                val myProvider = ComponentName(context, WarithWidgetReceiver::class.java)

                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                    appWidgetManager.requestPinAppWidget(myProvider, null, null)
                } else {
                    Toast.makeText(context, "Pinning not supported on this device", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Widget to Home Screen")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                historyManager.resetAllHistory()
                Toast.makeText(context, "History Reset", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset All History")
        }
    }
}
