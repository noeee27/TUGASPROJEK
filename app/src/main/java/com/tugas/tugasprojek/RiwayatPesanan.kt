package com.tugas.tugasprojek

import com.google.firebase.Timestamp

data class RiwayatPesanan(
    val namaBarang: String = "",
    val jumlah: Int = 0,
    val hargaSatuan: Int = 0,
    val totalHarga: Int = 0,
    val tanggalPinjam: Timestamp? = null,
    val tanggalKembali: Timestamp? = null,
    val timestamp: Timestamp? = null
)
