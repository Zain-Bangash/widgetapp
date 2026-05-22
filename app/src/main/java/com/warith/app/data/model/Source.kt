package com.warith.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    @SerialName("source_id") val sourceId: String,
    @SerialName("source_name") val sourceName: String,
    val type: String,
    val entries: List<Entry>
)
