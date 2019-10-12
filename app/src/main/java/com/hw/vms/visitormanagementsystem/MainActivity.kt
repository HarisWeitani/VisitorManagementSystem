package com.hw.vms.visitormanagementsystem

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest.permission
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.pm.PackageManager
import android.hardware.Camera
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    /***
     * Permission
     */
    var INTERNET_REQUEST = 1
    var CAMERA_REQUEST = 2
    var isPermitted: Boolean = false

    /***
     * View Variable
     */
    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView
    lateinit var et_name : EditText
    lateinit var et_host : EditText
    lateinit var et_address : EditText
    lateinit var et_phone_number : EditText
    lateinit var iv_profile_picture : ImageView
    lateinit var btn_start_camera : Button
    lateinit var btn_save : Button

    /***
     * Camera
     */
    private val pic_id = 123
    var isCameraInitialized : Boolean = false
    var camera : Camera? = null
    lateinit var cameraPreview: CameraPreview
    lateinit var preview : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView(){
        iv_logo = findViewById(R.id.iv_logo)
        tv_visitor_number = findViewById(R.id.tv_visitor_number)
        tv_date_now = findViewById(R.id.tv_date_now)
        et_name = findViewById(R.id.et_name)
        et_host = findViewById(R.id.et_host)
        et_address = findViewById(R.id.et_address)
        et_phone_number = findViewById(R.id.et_phone_number)
        iv_profile_picture = findViewById(R.id.iv_profile_picture)
        btn_start_camera = findViewById(R.id.btn_start_camera)
        btn_save = findViewById(R.id.btn_save)
        preview = findViewById(R.id.layout_camera)

        btn_start_camera.setOnClickListener {
//            val camera_intent = Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE
//            )
//            startActivityForResult(camera_intent, pic_id)
            camera = Camera.open()
            cameraPreview = CameraPreview(this,camera!!)
            preview.addView(cameraPreview)
        }

        btn_save.setOnClickListener {

        }

        initDate()
        initVisitorNumber()
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermitted){
            checkPermission()
        }
    }


    private fun checkPermission() {

        var internet = false
        var camera = false

        if( ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET),INTERNET_REQUEST)
        }else{
            internet = true
        }
        if( ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA),CAMERA_REQUEST)
        }else{
            camera = true
        }

        isPermitted = internet && camera

        if( isPermitted ){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pic_id) {

            try{
                // BitMap is data structure of image file
                // which stor the image in memory
                val photo = data?.extras?.get("data") as Bitmap

                // Set the image in imageview for display
                iv_profile_picture.setImageBitmap(photo)
            }catch (e:Exception){}
        }
    }

    private fun initDate(){
        val date = Date()
        val df = SimpleDateFormat("dd/MM/yyyy")
        runOnUiThread {
            tv_date_now.text = "Date : ${df.format(date)}"
        }
    }
    private fun initVisitorNumber(){
        tv_visitor_number.text = "Visitor Number : 222"
    }


}
