package com.warith.app

import com.warith.app.data.model.Entry
import com.warith.app.data.model.Source
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class SerializationTest {

    @Test
    fun testEntrySerialization() {
        val entry = Entry(1, "Text", "Narrator", "Ref", "Link")
        val jsonString = Json.encodeToString(Entry.serializer(), entry)
        val decoded = Json.decodeFromString(Entry.serializer(), jsonString)
        assertEquals(entry, decoded)
    }

    @Test
    fun testSourceSerialization() {
        val entry = Entry(1, "Text", "Narrator", "Ref", "Link")
        val source = Source("id", "Name", "narration", listOf(entry))
        val jsonString = Json.encodeToString(Source.serializer(), source)
        val decoded = Json.decodeFromString(Source.serializer(), jsonString)
        assertEquals(source, decoded)
    }
}
