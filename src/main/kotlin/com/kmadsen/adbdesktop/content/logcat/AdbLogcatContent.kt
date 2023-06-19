package com.kmadsen.adbdesktop.content.logcat

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmadsen.adbdesktop.AppContext
import kotlinx.coroutines.launch

@Composable
fun AdbLogcatContent(appContext: AppContext) {
    val logEntries: MutableList<String> = remember { mutableStateListOf() }
    val autoScroll: MutableState<Boolean> = remember { mutableStateOf(true) }
    val filterText = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect("adb logcat") {
        appContext.adb.logcat().collect { line ->
            logEntries.add(line)
        }
    }

    Column {
        AdbLogcatControlPanel(
            autoScroll = autoScroll,
            filterText = filterText,
            onClearLogs = {
                coroutineScope.launch {
                    appContext.adb.logcatClear()
                    logEntries.clear()
                }
            }
        )

        val entries = logEntries
            .filter { line -> filterText.value.isEmpty() || line.contains(filterText.value, ignoreCase = true) }
            .mapNotNull(::mapToAdbLogEntry)
        AdbLogcatEntries(appContext, entries, autoScroll.value)
    }
}

@Composable
fun AdbLogcatEntries(appContext: AppContext, logEntries: List<AdbLogEntry>, autoScroll: Boolean) {
    val scrollState: LazyListState = rememberLazyListState()

    LaunchedEffect(autoScroll, logEntries.size) {
        if (autoScroll && logEntries.isNotEmpty()) {
            scrollState.scrollToItem(logEntries.lastIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            items(logEntries.size) { index ->
                AdbLogEntryItem(appContext, logEntries[index])
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState),
        )
    }
}

@Composable
fun AdbLogEntryItem(appContext: AppContext, entry: AdbLogEntry) {
    val labelBlock = MaterialTheme.typography.labelSmall.copy(
        textAlign = TextAlign.Center,
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = entry.date,
            modifier = Modifier.width(45.dp),
            style = labelBlock,
            maxLines = 1,
        )
        Text(
            text = entry.time,
            modifier = Modifier.width(90.dp),
            style = labelBlock,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .width(20.dp)
                .background(appContext.themeManager.logLevelColor(entry.logLevel)
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = entry.logLevel.tag,
                style = labelBlock.copy(color = Color.Black),
                maxLines = 1,
            )
        }
        Text(
            text = "${entry.processId}-${entry.threadId}",
            modifier = Modifier.width(55.dp),
            style = labelBlock,
            maxLines = 1,
        )
        Text(
            text = entry.tag,
            modifier = Modifier.wrapContentWidth(),
            style = labelBlock.copy(textAlign = TextAlign.Start),
            maxLines = 1,
        )
        Text(
            text = entry.message,
            modifier = Modifier.weight(1f),
            style = labelBlock.copy(textAlign = TextAlign.Start),
        )
    }
}

fun mapToAdbLogEntry(line: String): AdbLogEntry? {
    // limit to 7 so the message is taken as a whole
    val parts = line.split(Regex("\\s+"), limit = 7)
    if (parts.size != 7) {
        println("Ignoring line as it does not map to an entry: $line")
        return null
    }
    return AdbLogEntry(
        date = parts[0],
        time = parts[1],
        processId = parts[2].toInt(),
        threadId = parts[3].toInt(),
        logLevel = AdbLogcatLevel.fromString(parts[4]),
        tag = parts[5],
        message = parts[6]
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdbLogcatControlPanel(
    autoScroll: MutableState<Boolean>,
    filterText: MutableState<String>,
    onClearLogs: () -> Unit)
{
    Row(
        Modifier.fillMaxWidth().padding(bottom = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            onClick = onClearLogs,
        ) {
            Text(text = "Clear")
        }

        TextField(
            value = filterText.value,
            onValueChange = { filterText.value = it },
            label = { Text("Filter") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Autoscroll",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.labelMedium
                .copy(textAlign = TextAlign.End),
        )
        Checkbox(
            checked = autoScroll.value,
            modifier = Modifier.align(Alignment.CenterVertically),
            onCheckedChange = { autoScroll.value = it }
        )
    }
}