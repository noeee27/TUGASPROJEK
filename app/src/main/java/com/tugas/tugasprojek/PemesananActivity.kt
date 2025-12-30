package com.tugas.tugasprojek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tugas.tugasprojek.databinding.ActivityPemesananBinding
import java.text.NumberFormat
import java.util.Locale

class PemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPemesananBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
            val userId = auth.currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSimpan.isEnabled = false
            prosesPesanan(userId)
        }
    }

    // 🔥 TRANSAKSI + SIMPAN RIWAYAT
    private fun prosesPesanan(userId: String) {
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

            val pesanan = hashMapOf(
                "userId" to userId,
                "barangId" to barangId,
                "namaBarang" to binding.tvNamaBarang.text.toString(),
                "jumlah" to jumlah,
                "hargaSatuan" to hargaSatuan,
                "totalHarga" to jumlah * hargaSatuan,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("riwayat_pesanan").add(pesanan)

            Toast.makeText(this, "Pesanan berhasil!", Toast.LENGTH_LONG).show()

            val intent = Intent(this, PengambilanActivity::class.java)
            intent.putExtra("nama", binding.tvNamaBarang.text.toString())
            intent.putExtra("jumlah", jumlah)
            intent.putExtra("total", jumlah * hargaSatuan)
            startActivity(intent)
            finish()

        }.addOnFailureListener {
            binding.btnSimpan.isEnabled = true
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatRupiah(value: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(value)
    }
}
