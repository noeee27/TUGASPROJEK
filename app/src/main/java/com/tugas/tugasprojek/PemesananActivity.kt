package com.tugas.tugasprojek

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.tugas.tugasprojek.databinding.ActivityPemesananBinding
import java.text.NumberFormat
import java.util.Locale

class PemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPemesananBinding
    private val db = FirebaseFirestore.getInstance()

    private var barangId = ""
    private var hargaSatuan = 0
    private var stok = 0
    private var jumlah = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ambilDataIntent()
        setupJumlahButton()
        setupSimpanButton()
        updateTotal()
    }

    private fun ambilDataIntent() {
        barangId = intent.getStringExtra("id") ?: ""
        binding.tvNamaBarang.text = intent.getStringExtra("nama") ?: "-"

        hargaSatuan = intent.getIntExtra("harga", 0)
        stok = intent.getIntExtra("stok", 0)

        if (barangId.isEmpty() || hargaSatuan <= 0) {
            Toast.makeText(this, "Data barang tidak valid", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupJumlahButton() {
        binding.btnMinus.setOnClickListener {
            if (jumlah > 1) {
                jumlah--
                updateTotal()
            }
        }

        binding.btnPlus.setOnClickListener {
            if (jumlah < stok) {
                jumlah++
                updateTotal()
            } else {
                Toast.makeText(this, "Stok tidak mencukupi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        binding.tvJumlah.text = jumlah.toString()
        binding.tvTotalHarga.text = formatRupiah(jumlah * hargaSatuan)
    }

    private fun setupSimpanButton() {
        binding.btnSimpan.setOnClickListener {
            binding.btnSimpan.isEnabled = false
            prosesPesanan()
        }
    }

    // 🔥 TRANSAKSI FIRESTORE AMAN
    private fun prosesPesanan() {
        val docRef = db.collection("barang").document(barangId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val stokSekarang = snapshot.getLong("stok") ?: 0

            if (stokSekarang >= jumlah) {
                transaction.update(docRef, "stok", stokSekarang - jumlah)
            } else {
                throw Exception("Stok tidak cukup")
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "Pesanan berhasil!", Toast.LENGTH_LONG).show()

            val intent = Intent(this, PengambilanActivity::class.java)
            intent.putExtra("nama", binding.tvNamaBarang.text.toString())
            intent.putExtra("jumlah", jumlah)
            intent.putExtra("total", jumlah * hargaSatuan)

            startActivity(intent)
            finish()
        }

    }
    private fun formatRupiah(value: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(value)
    }
}
