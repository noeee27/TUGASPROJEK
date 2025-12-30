package com.tugas.tugasprojek

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tugas.tugasprojek.databinding.ActivityChatAdminBinding

class ChatAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatAdminBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter(chatList)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

        loadChat()
        setupSend()
    }

    private fun loadChat() {
        db.collection("chat_admin")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chatList.clear()
                    chatList.addAll(snapshot.toObjects(ChatMessage::class.java))
                    adapter.notifyDataSetChanged()
                    binding.rvChat.scrollToPosition(chatList.size - 1)
                }
            }
    }

    private fun setupSend() {
        binding.btnSend.setOnClickListener {
            val text = binding.etPesan.text.toString()
            if (text.isEmpty()) return@setOnClickListener

            val chat = hashMapOf(
                "userId" to auth.currentUser?.uid,
                "nama" to "User",
                "pesan" to text,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            db.collection("chat_admin").add(chat)
            binding.etPesan.text.clear()
        }
    }
}
