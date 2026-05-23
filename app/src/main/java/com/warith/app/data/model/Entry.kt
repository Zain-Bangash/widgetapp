package com.warith.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val id: Int,
    val text: String,
    val narrator: String? = null,
    val reference: String,
    val link: String
)
