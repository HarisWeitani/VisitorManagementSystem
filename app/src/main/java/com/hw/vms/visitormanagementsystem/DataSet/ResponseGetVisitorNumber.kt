package com.hw.vms.visitormanagementsystem.DataSet

data class ResponseGetVisitorNumber(
    var message: String? = "",
    var ok: Int? = 0,
    var visitor_number: Int? = 0
)