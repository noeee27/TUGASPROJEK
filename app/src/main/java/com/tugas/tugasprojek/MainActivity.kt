package com.tugas.tugasprojek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

import com.tugas.tugasprojek.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var barangAdapter: BarangAdapter

    private val db = FirebaseFirestore.getInstance()

    private val dataBarang = mutableListOf<Barang>()
    private val filteredBarang = mutableListOf<Barang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchFunction()
        setupBottomNavigation()
        setupProfileCard()
        loadDataBarang()
    }

    //  AMBIL DATA BARANG DARI FIREBASE
    private fun loadDataBarang() {
        db.collection("barang")
            .get()
            .addOnSuccessListener { result ->
                dataBarang.clear()
                for (doc in result) {
                    val barang = doc.toObject(Barang::class.java)
                    barang.id = doc.id
                    dataBarang.add(barang)
                }
                filterBarang("")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView() {
        barangAdapter = BarangAdapter(
            list = filteredBarang,

            // klik card
            onItemClick = { barang ->
                Toast.makeText(
                    this,
                    barang.deskripsi,
                    Toast.LENGTH_SHORT
                ).show()
            },

            // klik favorite
            onFavoriteClick = { barang ->
                barang.isFavorite = !barang.isFavorite
                barangAdapter.notifyDataSetChanged()
            },

            //  klik PESAN → PINDAH HALAMAN
            onPesanClick = { barang ->
                if (barang.stok <= 0) {
                    Toast.makeText(this, "Stok habis", Toast.LENGTH_SHORT).show()
                    return@BarangAdapter
                }

                val intent = Intent(this, PemesananActivity::class.java)
                intent.putExtra("id", barang.id)
                intent.putExtra("nama", barang.nama)
                intent.putExtra("harga", barang.harga)
                intent.putExtra("stok", barang.stok)
                startActivity(intent)
            }
        )

        binding.rvBarang.layoutManager = GridLayoutManager(this, 2)
        binding.rvBarang.adapter = barangAdapter
    }

    private fun setupSearchFunction() {
        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            filterBarang(binding.searchEditText.text.toString())
            false
        }
    }

    private fun filterBarang(query: String) {
        filteredBarang.clear()

        if (query.isEmpty()) {
            filteredBarang.addAll(dataBarang)
        } else {
            filteredBarang.addAll(
                dataBarang.filter {
                    it.nama.contains(query, true) ||
                            it.deskripsi.contains(query, true)
                }
            )
        }
        barangAdapter.notifyDataSetChanged()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    filterBarang("")
                    binding.textRekomendasi.text = "Barang Rekomendasi"
                    true
                }

                R.id.nav_favorite -> {
                    filteredBarang.clear()
                    filteredBarang.addAll(dataBarang.filter { it.isFavorite })
                    barangAdapter.notifyDataSetChanged()
                    binding.textRekomendasi.text = "Barang Favorit"
                    true
                }

                R.id.nav_riwayat -> {
                    val intent = Intent(this, RiwayatActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chat -> {
                    val intent = Intent(this, ChatAdminActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }


    private fun setupProfileCard() {
        binding.cardProfile.setOnClickListener {
            Toast.makeText(this, "Profil diklik", Toast.LENGTH_SHORT).show()
        }

        binding.btnLainnya.setOnClickListener {
            Toast.makeText(this, "Menampilkan semua kategori", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔄 refresh stok setelah kembali dari pemesanan
    override fun onResume() {
        super.onResume()
        loadDataBarang()
    }
}
