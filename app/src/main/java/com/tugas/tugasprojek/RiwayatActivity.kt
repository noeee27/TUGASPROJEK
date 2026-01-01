package com.tugas.tugasprojek

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tugas.tugasprojek.databinding.ActivityRiwayatBinding

class RiwayatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        ambilRiwayat()
    }

    private fun setupRecyclerView() {
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayat.setHasFixedSize(true)
    }

    private fun ambilRiwayat() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("RIWAYAT", "User belum login")
            return
        }

        db.collection("riwayat_pesanan")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->

                Log.d("RIWAYAT", "Jumlah data: ${snapshot.size()}")

                val list = snapshot.toObjects(RiwayatPesanan::class.java)

                binding.rvRiwayat.adapter = RiwayatAdapter(list)
            }
            .addOnFailureListener {
                Log.e("RIWAYAT", it.message.toString())
            }
    }
}
