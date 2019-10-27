package com.hw.vms.visitormanagementsystem.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.AdminLoginActivity
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetVisitorNumber
import com.hw.vms.visitormanagementsystem.Helper.GlobalVal
import com.hw.vms.visitormanagementsystem.Helper.NetworkHelper
import com.hw.vms.visitormanagementsystem.Helper.SettingsData
import com.hw.vms.visitormanagementsystem.Helper.SharedPreference
import com.hw.vms.visitormanagementsystem.R
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NameActivity : AppCompatActivity() {

    /***
     * Permission
     */
    var INTERNET_REQUEST = 1
    var CAMERA_REQUEST = 2
    var isPermitted: Boolean = false
    
    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView

    lateinit var et_name : EditText
    lateinit var btn_next : Button
    var firstInstall : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        val sharepref = SharedPreference(this)
        firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)
        DAO.settingsData = Gson().fromJson(sharepref.getValueString(GlobalVal.SETTINGS_DATA_KEY), SettingsData::class.java)

    }

    private fun initApp(){
        initHeader()
        initView()

        val networkHelper = NetworkHelper()
        networkHelper.getAllHost(this)
        networkHelper.getVisitorNumber(this)
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermitted){
            checkPermission()
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            isPermitted = true
        }
        if(firstInstall){
            startActivity(
                Intent(this@NameActivity,
                    AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }else if (isPermitted){
            initApp()
        }
    }

    private fun checkPermission() {
        var internet = false
        var camera = false

        if( ContextCompat.checkSelfPermission(this@NameActivity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this@NameActivity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET),INTERNET_REQUEST)
        }else{
            internet = true
        }
        if( ContextCompat.checkSelfPermission(this@NameActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this@NameActivity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA),CAMERA_REQUEST)
        }else{
            camera = true
        }

        isPermitted = internet && camera

        if( isPermitted ){

        }
    }

    private fun initView(){
        et_name = findViewById(R.id.et_name)
        btn_next = findViewById(R.id.btn_next)

        btn_next.setOnClickListener {
            if( !et_name.text.isNullOrEmpty() ){
                DAO.name = et_name.text.toString()
                startActivity<CompanyActivity>()
            }else{
                Toast.makeText(this@NameActivity,"Name Cannot Be Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initHeader(){
        iv_logo = findViewById(R.id.iv_logo)
        tv_visitor_number = findViewById(R.id.tv_visitor_number)
        tv_date_now = findViewById(R.id.tv_date_now)

        initDate()
        initVisitorNumber()
    }
    private fun initDate(){
        val date = Date()
        val df = SimpleDateFormat("dd MMMM yyyy")
        runOnUiThread {
            tv_date_now.text = "Date : ${df.format(date)}"
        }
    }
    private fun initVisitorNumber(){
        var res = 0
        try {
            res = DAO.responseGetVisitorNumber?.visitor_number!!
        }catch (e:Exception){}
        tv_visitor_number.text = "Visitor Number : $res"
    }
    override fun onBackPressed() {

    }

}
