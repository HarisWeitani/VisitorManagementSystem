package com.hw.vms.visitormanagementsystem.DataSet

data class ResponseBooking(
    var data: DataBooking? = DataBooking(),
    var message: String? = "",
    var ok: Int? = 0
)
data class DataBooking(
    var booking_id: String? = "",
    var guest_address: String? = "",
    var guest_company: String? = "",
    var guest_image: String? = "",
    var guest_name: String? = "",
    var guest_phone: String? = "",
    var host_division: String? = "",
    var host_email: String? = "",
    var host_image: String? = "",
    var host_name: String? = "",
    var host_phone: String? = ""
)