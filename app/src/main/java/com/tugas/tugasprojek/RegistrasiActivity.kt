package com.tugas.tugasprojek

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tugas.tugasprojek.databinding.ActivityRegistrasiBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrasiBinding

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val nama = binding.etNama.text.toString()
        val email = binding.etEmail.text.toString()
        val pass = binding.etPassword.text.toString()

        if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid

                val user = hashMapOf(
                    "nama" to nama,
                    "email" to email
                )

                db.collection("users").document(uid).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
