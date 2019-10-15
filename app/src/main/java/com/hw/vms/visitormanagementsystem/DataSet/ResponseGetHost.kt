package com.hw.vms.visitormanagementsystem.DataSet

data class ResponseGetHost(
    var data: List<DataHost?>? = listOf(),
    var message: String? = "",
    var ok: Int? = 0
)
data class DataHost(
    var created_by: String? = "",
    var created_date: String? = "",
    var edited_by: String? = "",
    var edited_date: String? = "",
    var host_division: String? = "",
    var host_email: String? = "",
    var host_id: String? = "",
    var host_image: String? = "",
    var host_name: String? = "",
    var host_phone: String? = "",
    var host_status: String? = ""
)