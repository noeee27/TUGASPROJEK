package com.tugas.tugasprojek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugas.tugasprojek.databinding.ItemChatBinding

class ChatAdapter(
    private val list: List<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChatBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvNama.text = data.nama
        holder.binding.tvPesan.text = data.pesan
    }

    override fun getItemCount(): Int = list.size
}
