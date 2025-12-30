package com.tugas.tugasprojek

data class Barang(
    var id: String = "",          // documentId Firebase
    var nama: String = "",
    var rating: Double = 0.0,
    var gambar: String = "",      // URL / nama file gambar
    var harga: Int = 0,           // HARUS number
    var stok: Int = 0,            // INI PENTING
    var deskripsi: String = "",
    var isFavorite: Boolean = false
)
