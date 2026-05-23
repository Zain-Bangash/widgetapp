package com.warith.app

import android.app.Application
import com.warith.app.data.repository.LocalSourceRepository
import com.warith.app.data.repository.SourceRepository
import com.warith.app.util.HistoryManager

class WarithApp : Application() {

    val historyManager: HistoryManager by lazy {
        HistoryManager(this)
    }

    val repository: SourceRepository by lazy {
        LocalSourceRepository(this, historyManager)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: WarithApp
            private set
    }
}
