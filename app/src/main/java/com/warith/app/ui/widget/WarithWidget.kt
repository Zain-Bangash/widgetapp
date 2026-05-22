package com.warith.app.ui.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.material3.ColorProviders
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.warith.app.data.model.Entry
import com.warith.app.data.model.Source
import com.warith.app.data.repository.LocalSourceRepository
import com.warith.app.data.repository.SourceRepository
import com.warith.app.util.HistoryManager

class WarithWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent(context)
            }
        }
    }

    @Composable
    private fun WidgetContent(@Suppress("UNUSED_PARAMETER") context: Context) {
        val context = LocalContext.current
        val historyManager = HistoryManager(context)
        val repository = LocalSourceRepository(context, historyManager)
        val sources = repository.getAllSources()

        if (sources.isEmpty()) {
            Box(modifier = GlanceModifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = "No sources found", style = TextStyle(color = ColorProvider(android.R.color.white)))
            }
            return
        }

        var currentIndex = historyManager.getCurrentSourceIndex()
        if (currentIndex >= sources.size) {
            currentIndex = 0
            historyManager.setCurrentSourceIndex(0)
        }

        val source = sources[currentIndex]
        val entryId = historyManager.getCurrentEntryId(source.sourceId)

        val entry = if (entryId != null) {
            source.entries.find { it.id == entryId } ?: repository.getNextEntry(source.sourceId)
        } else {
            repository.getNextEntry(source.sourceId)
        }

        WidgetLayout(
            context = context,
            source = source,
            entry = entry,
            showArrows = sources.size > 1
        )
    }

    @Composable
    private fun WidgetLayout(
        context: Context,
        source: Source,
        entry: Entry?,
        showArrows: Boolean
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp)
        ) {
            // Top Row: Source Name and Refresh
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = source.sourceName,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )

                Image(
                    provider = ImageProvider(com.warith.app.R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier
                        .size(24.dp)
                        .clickable(actionRunCallback<RefreshAction>())
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Content Area
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
            ) {
                if (entry != null) {
                    Text(
                        text = entry.text,
                        modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 16.sp
                        ),
                        maxLines = 10
                    )

                    if (!entry.narrator.isNullOrBlank()) {
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "— ${entry.narrator}",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                } else {
                    Text(text = "No entries found", style = TextStyle(color = GlanceTheme.colors.onSurface))
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Bottom Row: Navigation and Reference
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                if (showArrows) {
                    Image(
                        provider = ImageProvider(com.warith.app.R.drawable.ic_arrow_left),
                        contentDescription = "Previous Source",
                        modifier = GlanceModifier
                            .size(24.dp)
                            .clickable(actionRunCallback<NavigateAction>(actionParametersOf(NavigateAction.directionKey to -1)))
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Image(
                        provider = ImageProvider(com.warith.app.R.drawable.ic_arrow_right),
                        contentDescription = "Next Source",
                        modifier = GlanceModifier
                            .size(24.dp)
                            .clickable(actionRunCallback<NavigateAction>(actionParametersOf(NavigateAction.directionKey to 1)))
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                if (entry != null) {
                    Text(
                        text = entry.reference,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.clickable(
                            actionRunCallback<OpenLinkAction>(
                                actionParametersOf(OpenLinkAction.linkKey to entry.link)
                            )
                        )
                    )
                }
            }
        }
    }
}

class WarithWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WarithWidget()
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val historyManager = HistoryManager(context)
        val repository = LocalSourceRepository(context, historyManager)
        val sources = repository.getAllSources()
        val currentIndex = historyManager.getCurrentSourceIndex()

        if (currentIndex < sources.size) {
            val source = sources[currentIndex]
            repository.getNextEntry(source.sourceId)
            WarithWidget().update(context, glanceId)
        }
    }
}

class NavigateAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val direction = parameters[directionKey] ?: 0
        val historyManager = HistoryManager(context)
        val repository = LocalSourceRepository(context, historyManager)
        val sources = repository.getAllSources()

        if (sources.isNotEmpty()) {
            var newIndex = (historyManager.getCurrentSourceIndex() + direction) % sources.size
            if (newIndex < 0) newIndex += sources.size
            historyManager.setCurrentSourceIndex(newIndex)
            WarithWidget().update(context, glanceId)
        }
    }

    companion object {
        val directionKey = ActionParameters.Key<Int>("direction")
    }
}

class OpenLinkAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val link = parameters[linkKey] ?: return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    companion object {
        val linkKey = ActionParameters.Key<String>("link")
    }
}
