package com.warith.app.data.repository

import android.content.Context
import com.warith.app.data.model.Entry
import com.warith.app.data.model.Source
import com.warith.app.util.HistoryManager
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

class LocalSourceRepository(
    private val context: Context,
    private val historyManager: HistoryManager
) : SourceRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val sources: List<Source> by lazy { loadSourcesFromAssets() }

    private fun loadSourcesFromAssets(): List<Source> {
        val sourceList = mutableListOf<Source>()
        try {
            val files = context.assets.list("sources")?.sortedArray() ?: emptyArray()
            for (fileName in files) {
                if (fileName.endsWith(".json")) {
                    val inputStream = context.assets.open("sources/$fileName")
                    val reader = InputStreamReader(inputStream)
                    val content = reader.readText()
                    val source = json.decodeFromString<Source>(content)
                    sourceList.add(source)
                    reader.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sourceList
    }

    override fun getAllSources(): List<Source> = sources

    override fun getSourceById(sourceId: String): Source? = sources.find { it.sourceId == sourceId }

    override fun getNextEntry(sourceId: String): Entry? {
        getSourceById(sourceId) ?: return null
        val unseen = getRandomUnseenEntry(sourceId)
        if (unseen != null) return unseen

        // If all seen, reset and try again
        resetHistory(sourceId)
        return getRandomUnseenEntry(sourceId)
    }

    override fun getRandomUnseenEntry(sourceId: String): Entry? {
        val source = getSourceById(sourceId) ?: return null
        val shownIds = historyManager.getShownIds(sourceId)
        val unseenEntries = source.entries.filter { it.id !in shownIds }

        if (unseenEntries.isEmpty()) return null

        val selected = unseenEntries.random()
        historyManager.markAsShown(sourceId, selected.id)
        historyManager.setCurrentEntryId(sourceId, selected.id)
        return selected
    }

    override fun resetHistory(sourceId: String) {
        historyManager.resetHistory(sourceId)
    }

    override fun resetAllHistory() {
        historyManager.resetAllHistory()
    }
}
