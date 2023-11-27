package com.example.firebasecomplaint

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.MutableLiveData
import com.example.firebasecomplaint.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val complaintList: ArrayList<Complaint> = ArrayList()
    private val complaintListLiveData: MutableLiveData<List<Complaint>> by lazy {
        MutableLiveData<List<Complaint>>()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the click listener for the "Tambah" button to open the Form activity
        binding.fabTambah.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        listView.adapter = adapter

        // Terima data dari intent
        val complaintData = intent.getSerializableExtra("complaintData") as? Complaint

        // Tampilkan data di halaman lain dan tambahkan ke list
        complaintData?.let {

            // Add the new complaint to the list
            complaintList.add(it)

            // Update the adapter with the new list
            updateAdapter()

            // Clear the intent data to avoid duplicates
            intent.removeExtra("complaintData")
        }
        // Observe changes in Firestore and update the list
        observeComplaints()

        // Set item click listener for the listView
        with(binding) {
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedComplaint = complaintList[position]

                // Intent untuk membuka ComplaintDetailActivity dengan data complaint yang dipilih
                val intent = Intent(this@MainActivity, DetailComplaintActivity::class.java)
                intent.putExtra("selectedComplaint", selectedComplaint)
                startActivity(intent)
            }
        }
    }



    private fun updateAdapter() {
        // Clear the adapter and add all items from the list
        adapter.clear()
        for (complaint in complaintList) {
            val displayText =
                "Name    :    ${complaint.name_complaint}\nTitle       :   ${complaint.title_complaint}\nContent:   ${complaint.content_complaint}"
            adapter.add(displayText)
        }
    }


    private fun observeComplaints() {
        val firestore = FirebaseFirestore.getInstance()
        val complaintCollectionRef = firestore.collection("complaints")

        complaintCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for complaint changes: ", error)
                return@addSnapshotListener
            }

            // Clear the existing list
            complaintList.clear()

            snapshots?.forEach { documentSnapshot ->
                val complaint = documentSnapshot.toObject(Complaint::class.java)
                if (complaint != null) {
                    complaintList.add(complaint)
                }
            }

            // Notify LiveData observer
            complaintListLiveData.postValue(complaintList)

            // Update the adapter with the new list
            updateAdapter()
        }
    }

}
