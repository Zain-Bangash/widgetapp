package com.warith.app.data.repository

import com.warith.app.data.model.Entry
import com.warith.app.data.model.Source

interface SourceRepository {
    fun getAllSources(): List<Source>
    fun getSourceById(sourceId: String): Source?
    fun getNextEntry(sourceId: String): Entry?
    fun getRandomUnseenEntry(sourceId: String): Entry?
    fun resetHistory(sourceId: String)
    fun resetAllHistory()
}
