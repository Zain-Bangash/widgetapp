package com.warith.app.util

import android.content.Context
import android.content.SharedPreferences

class HistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("warith_prefs", Context.MODE_PRIVATE)

    fun markAsShown(sourceId: String, entryId: Int) {
        val shown = prefs.getStringSet("shown_$sourceId", emptySet())?.toMutableSet() ?: mutableSetOf()
        shown.add(entryId.toString())
        prefs.edit().putStringSet("shown_$sourceId", shown).apply()
    }

    fun getShownIds(sourceId: String): Set<Int> {
        return prefs.getStringSet("shown_$sourceId", emptySet())
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    fun resetHistory(sourceId: String) {
        prefs.edit().remove("shown_$sourceId").apply()
    }

    fun resetAllHistory() {
        // We only want to remove shown records, but also need to keep track of auto-rotation settings if any.
        // Actually the requirement says "only clear the already seen records".
        val allKeys = prefs.all.keys.filter { it.startsWith("shown_") }
        val editor = prefs.edit()
        allKeys.forEach { editor.remove(it) }
        editor.apply()
    }

    fun setAutoRotationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_rotation", enabled).apply()
    }

    fun isAutoRotationEnabled(): Boolean {
        return prefs.getBoolean("auto_rotation", false)
    }

    fun setCurrentSourceIndex(index: Int) {
        prefs.edit().putInt("current_source_index", index).apply()
    }

    fun getCurrentSourceIndex(): Int {
        return prefs.getInt("current_source_index", 0)
    }

    fun setCurrentEntryId(sourceId: String, entryId: Int) {
        prefs.edit().putInt("current_entry_$sourceId", entryId).apply()
    }

    fun getCurrentEntryId(sourceId: String): Int? {
        val id = prefs.getInt("current_entry_$sourceId", -1)
        return if (id == -1) null else id
    }
}
