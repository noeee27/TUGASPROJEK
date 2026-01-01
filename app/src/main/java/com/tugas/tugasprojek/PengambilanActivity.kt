package com.tugas.tugasprojek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tugas.tugasprojek.databinding.ActivityPengambilanBinding
import android.net.Uri


class PengambilanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengambilanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengambilanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLokasiToko.setOnClickListener {
            val latitude = -7.8333998
            val longitude = 110.3830414

            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Universitas Ahmad Dahlan Kampus 4)")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
