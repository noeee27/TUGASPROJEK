package com.tugas.tugasprojek

data class Barang(
    var id: String = "",
    var nama: String = "",
    var rating: Double = 0.0,
    var gambar: String = "",
    var harga: Int = 0,
    var stok: Int = 0,
    var deskripsi: String = "",
    var isFavorite: Boolean = false
)
