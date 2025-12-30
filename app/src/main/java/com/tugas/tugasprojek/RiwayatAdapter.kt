package com.tugas.tugasprojek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.tugas.tugasprojek.databinding.ItemRiwayatBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatAdapter(
    private val listRiwayat: List<RiwayatPesanan>
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRiwayatBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listRiwayat[position]

        holder.binding.tvNamaBarang.text = data.namaBarang
        holder.binding.tvJumlah.text = "Jumlah: ${data.jumlah}"
        holder.binding.tvTotal.text = formatRupiah(data.totalHarga)

        // 🔥 TANGGAL
        holder.binding.tvTanggalPinjam.text =
            "Pinjam: ${formatTanggal(data.tanggalPinjam)}"

        holder.binding.tvTanggalKembali.text =
            "Kembali: ${formatTanggal(data.tanggalKembali)}"
    }

    override fun getItemCount(): Int = listRiwayat.size

    private fun formatRupiah(value: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(value)
    }

    private fun formatTanggal(timestamp: Timestamp?): String {
        if (timestamp == null) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
        return sdf.format(timestamp.toDate())
    }
}
