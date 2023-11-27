package com.example.firebasecomplaint

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasecomplaint.databinding.DetailComplaintBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailComplaintActivity: AppCompatActivity() {

    private lateinit var tvDetailName: TextView
    private lateinit var tvDetailTitle: TextView
    private lateinit var tvDetailContent: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var binding: DetailComplaintBinding

    private lateinit var selectedComplaint: Complaint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvDetailName = findViewById(R.id.tvDetailName)
        tvDetailTitle = findViewById(R.id.tvDetailTitle)
        tvDetailContent = findViewById(R.id.tvDetailContent)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)

        // Terima data dari intent
        selectedComplaint = intent.getSerializableExtra("selectedComplaint") as Complaint

        // Tampilkan data pada layout detail
        tvDetailName.text = "Name: ${selectedComplaint.name_complaint}"
        tvDetailTitle.text = "Title      : ${selectedComplaint.title_complaint}"
        tvDetailContent.text = "Content: ${selectedComplaint.content_complaint}"

        // Atur click listener untuk tombol Edit
        btnEdit.setOnClickListener {
            // Intent untuk membuka FormActivity dengan mode edit
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("selectedComplaint", selectedComplaint)
            startActivity(intent)
        }

        // Atur click listener untuk tombol Delete
        btnDelete.setOnClickListener {


            // Misalnya, jika menggunakan Firebase Firestore
            val firestore = FirebaseFirestore.getInstance()
            val complaintCollectionRef = firestore.collection("complaints")

            // Hapus complaint dari Firestore berdasarkan ID atau data yang unik
            complaintCollectionRef.document(selectedComplaint.id).delete()
                .addOnSuccessListener {
                    // Jika penghapusan berhasil, kembali ke MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()  // Tutup activity saat ini
                }
                .addOnFailureListener { e ->
                    // Jika ada kesalahan dalam penghapusan, tampilkan pesan atau lakukan sesuatu
                    Log.w("ComplaintDetailActivity", "Error deleting document", e)
                }
        }
    }
}
