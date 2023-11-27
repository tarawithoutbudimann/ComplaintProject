package com.example.firebasecomplaint

import java.io.Serializable

data class Complaint(
    var id: String = "",
    var name_complaint: String = "",
    var title_complaint: String = "",
    var content_complaint: String = ""
) : Serializable
