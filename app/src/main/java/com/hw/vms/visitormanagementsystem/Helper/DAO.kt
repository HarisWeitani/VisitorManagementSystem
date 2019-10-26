package com.hw.rms.roommanagementsystem.Helper

import com.hw.vms.visitormanagementsystem.DataSet.ResponseBooking
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetVisitorNumber
import com.hw.vms.visitormanagementsystem.Helper.SettingsData
import java.io.File

/**
 * DATA ACCESS OBJECT
 */
class DAO {
    companion object{
        /**
         * API Object
         */

        var responseBooking : ResponseBooking? = null
        var responseGetHost : ResponseGetHost? = null
        var responseGetVisitorNumber : ResponseGetVisitorNumber? = null

        /**
         * Not API Object
         */
        var settingsData : SettingsData? = null

        var name : String? = ""
        var company : String? = ""
        var phone : String? = ""
        var email : String? = ""
        var host : String? = ""
        var image : File? = null
    }
}