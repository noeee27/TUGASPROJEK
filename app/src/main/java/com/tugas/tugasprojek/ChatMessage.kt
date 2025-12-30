package com.tugas.tugasprojek

import com.google.firebase.Timestamp

data class ChatMessage(
    val userId: String = "",
    val nama: String = "",
    val pesan: String = "",
    val timestamp: Timestamp? = null
)
