package com.tugas.tugasprojek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.tugas.tugasprojek.databinding.ActivityPemesananBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPemesananBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var barangId = ""
    private var hargaSatuan = 0
    private var stok = 0
    private var jumlah = 1

    private var tanggalPinjam: Timestamp? = null
    private var tanggalKembali: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ambilDataIntent()
        setupJumlahButton()
        setupTanggalPicker()
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

    // ================= TANGGAL =================

    private fun setupTanggalPicker() {
        binding.tvTanggalPinjam.setOnClickListener {
            showDatePicker { ts ->
                tanggalPinjam = ts
                binding.tvTanggalPinjam.text = formatTanggal(ts)
            }
        }

        binding.tvTanggalKembali.setOnClickListener {
            showDatePicker { ts ->
                tanggalKembali = ts
                binding.tvTanggalKembali.text = formatTanggal(ts)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Timestamp) -> Unit) {
        val calendar = Calendar.getInstance()

        val dialog = android.app.DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day, 0, 0, 0)
                val timestamp = Timestamp(calendar.time)
                onDateSelected(timestamp)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun formatTanggal(ts: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
        return sdf.format(ts.toDate())
    }

    // ================= TOTAL =================

    private fun updateTotal() {
        binding.tvJumlah.text = jumlah.toString()
        binding.tvTotalHarga.text = formatRupiah(jumlah * hargaSatuan)
    }

    // ================= SIMPAN =================

    private fun setupSimpanButton() {
        binding.btnSimpan.setOnClickListener {

            if (tanggalPinjam == null || tanggalKembali == null) {
                Toast.makeText(this, "Pilih tanggal pinjam & kembali", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tanggalKembali!!.toDate().before(tanggalPinjam!!.toDate())) {
                Toast.makeText(this, "Tanggal kembali tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSimpan.isEnabled = false
            prosesPesanan(userId)
        }
    }

    // ================= FIRESTORE =================

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
                "tanggalPinjam" to tanggalPinjam,
                "tanggalKembali" to tanggalKembali,
                "timestamp" to Timestamp.now()
            )

            db.collection("riwayat_pesanan").add(pesanan)

            Toast.makeText(this, "Pesanan berhasil!", Toast.LENGTH_LONG).show()

            startActivity(Intent(this, PengambilanActivity::class.java))
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
