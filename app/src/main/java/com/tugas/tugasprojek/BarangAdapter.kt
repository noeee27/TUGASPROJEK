package com.tugas.tugasprojek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BarangAdapter(
    private val list: List<Barang>,
    private val onItemClick: (Barang) -> Unit,
    private val onFavoriteClick: (Barang) -> Unit,
    private val onPesanClick: (Barang) -> Unit   //  tambah untuk stok
) : RecyclerView.Adapter<BarangAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgBarang)
        val nama: TextView = view.findViewById(R.id.tvNamaBarang)
        val rating: RatingBar = view.findViewById(R.id.ratingBar)
        val harga: TextView = view.findViewById(R.id.tvHarga)
        val favoriteIcon: ImageView = view.findViewById(R.id.imgFavorite)
        val btnPesan: TextView = view.findViewById(R.id.btnPesan) // tombol pesan
        val tvStok: TextView = view.findViewById(R.id.tvStok)
        val cardView: View = view.findViewById(R.id.cardBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val barang = list[position]

        holder.nama.text = barang.nama
        holder.rating.rating = barang.rating.toFloat()
        holder.harga.text = "Rp ${barang.harga}"
        holder.tvStok.text = "Stok: ${barang.stok}"

        val context = holder.itemView.context

        val imageName = barang.gambar
            .substringBefore(".")   // buang .jpg / .jpeg
            .lowercase()

        val imageResId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )

        if (imageResId != 0) {
            holder.img.setImageResource(imageResId)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_background)
        }

        // favorite icon
        holder.favoriteIcon.setImageResource(
            if (barang.isFavorite)
                R.drawable.ic_favorite_filled
            else
                R.drawable.ic_favorite_border
        )

        // disable tombol jika stok habis
        holder.btnPesan.isEnabled = barang.stok > 0
        holder.btnPesan.alpha = if (barang.stok > 0) 1f else 0.5f

        // klik item
        holder.cardView.setOnClickListener {
            onItemClick(barang)
        }

        // klik favorite
        holder.favoriteIcon.setOnClickListener {
            onFavoriteClick(barang)
        }

        // 🔥 klik pesan → kurangi stok Firebase
        holder.btnPesan.setOnClickListener {
            onPesanClick(barang)
        }
    }

    override fun getItemCount(): Int = list.size
}
