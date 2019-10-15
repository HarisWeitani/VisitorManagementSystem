package com.hw.rms.roommanagementsystem.Helper

import com.hw.vms.visitormanagementsystem.DataSet.ResponseBooking
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetVisitorNumber
import com.hw.vms.visitormanagementsystem.Helper.SettingsData

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
    }
}