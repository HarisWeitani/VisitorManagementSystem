package com.hw.vms.visitormanagementsystem

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*


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
    var isCameraInitialized : Boolean = false
    var camera : Camera? = null
    lateinit var cameraPreview: CameraPreview
    lateinit var layout_camera : FrameLayout

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
        layout_camera = findViewById(R.id.layout_camera)

        btn_start_camera.setOnClickListener {
            if(isCameraInitialized) {
                captureImage()
            }else{
                initCamera()
            }
        }

        iv_profile_picture.setOnClickListener {
            initCamera()
        }

        btn_save.setOnClickListener {

        }

        initDate()
        initVisitorNumber()
    }

    private fun initCamera(){
        val cameraInfo = Camera.CameraInfo()
        val cameraCount = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    camera = Camera.open(camIdx)
                    cameraPreview = CameraPreview(this,camera!!)
                    layout_camera.addView(cameraPreview)
                    isCameraInitialized = true
                    layout_camera.visibility = View.VISIBLE
                    iv_profile_picture.visibility = View.GONE
                } catch (e: RuntimeException) {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermitted){
            checkPermission()
        }
        layout_camera.visibility = View.GONE
        iv_profile_picture.visibility = View.VISIBLE
    }

    override fun onPause() {
        isCameraInitialized = false
        super.onPause()
    }

    val pictureCallback : Camera.PictureCallback = object : Camera.PictureCallback{
        override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
            layout_camera.visibility = View.GONE

            val matrix = Matrix()
            matrix.preScale(-1.0f,1.0f)
            val oriBitmap = BitmapFactory.decodeByteArray(data,0,data!!.size)
            val invertBitmap = Bitmap.createBitmap(oriBitmap,0,0,oriBitmap.width,oriBitmap.height,matrix,true)
            iv_profile_picture.setImageBitmap(invertBitmap)
            iv_profile_picture.visibility = View.VISIBLE
        }
    }

    private fun captureImage(){
        if(camera!=null && isCameraInitialized) {
            try {
                camera!!.takePicture(null,null,pictureCallback)
                isCameraInitialized = false
            }catch (e : Exception){}
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

}
