package com.example.firebasecomplaint

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasecomplaint.databinding.FormComplaintBinding
import com.google.firebase.firestore.FirebaseFirestore

class FormActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val complaintCollectionRef = firestore.collection("complaints")
    private lateinit var binding: FormComplaintBinding
    private var updateId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FormComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data yang ada dari intent
        val selectedComplaint = intent.getSerializableExtra("selectedComplaint") as? Complaint

        with(binding) {
            if (selectedComplaint != null) {
                // Jika ada data complaint, set nilai awal untuk form editing
                nameComplaint.setText(selectedComplaint.name_complaint)
                titleComplaint.setText(selectedComplaint.title_complaint)
                contentComplaint.setText(selectedComplaint.content_complaint)

                // Simpan ID untuk keperluan update
                updateId = selectedComplaint.id
            }

            btnAdd.setOnClickListener {
                // Ambil data dari form
                val name = nameComplaint.text.toString()
                val title = titleComplaint.text.toString()
                val content = contentComplaint.text.toString()

                // Buat objek Complaint
                val complaint = Complaint(name_complaint = name, title_complaint = title, content_complaint = content)

                if (updateId.isNotEmpty()) {
                    // Jika ada updateId, berarti mode editing
                    complaint.id = updateId
                    updateComplaint(complaint)
                } else {
                    // Jika tidak ada updateId, berarti mode penambahan baru
                    addComplaint(complaint)
                }

                // Kirim data ke halaman sebelumnya
                val intent = Intent(this@FormActivity, MainActivity::class.java)
                intent.putExtra("complaintData", complaint)
                startActivity(intent)
            }
        }
    }

    private fun addComplaint(complaint: Complaint) {
        // Add the complaint to Firestore
        complaintCollectionRef.add(complaint)
            .addOnSuccessListener { documentReference ->
                val createdComplaintId = documentReference.id
                complaint.id = createdComplaintId
                documentReference.set(complaint)
                    .addOnFailureListener {
                        Log.d("FormActivity", "Error updating complaint ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("FormActivity", "Error adding complaint: ", it)
            }
    }

    private fun updateComplaint(complaint: Complaint) {
        // Update complaint di Firestore berdasarkan ID
        complaintCollectionRef.document(complaint.id).set(complaint)
            .addOnFailureListener {
                Log.d("FormActivity", "Error updating complaint: ", it)
            }
    }
}
