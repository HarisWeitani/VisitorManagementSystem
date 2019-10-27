package com.hw.vms.visitormanagementsystem.Activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.Adapter.HostAdapter
import com.hw.vms.visitormanagementsystem.CameraPreview
import com.hw.vms.visitormanagementsystem.DataSet.DataHost
import com.hw.vms.visitormanagementsystem.R
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class PhotoActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView
    lateinit var btn_back : Button
    lateinit var btn_next : Button

    /***
     * Camera
     */
    var isCameraInitialized : Boolean = false
    var camera : Camera? = null
    lateinit var cameraPreview: CameraPreview
    lateinit var layout_camera : FrameLayout
    var imagePhotoBitmap : Bitmap? = null
    lateinit var btn_start_camera : Button
    lateinit var iv_profile_picture : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        initView()
        initHeader()
    }

    private fun initView() {
        btn_next = findViewById(R.id.btn_next)
        btn_back = findViewById(R.id.btn_back)

        btn_next.setOnClickListener {
            if( imagePhotoBitmap!= null){
                DAO.imageBmp = imagePhotoBitmap
                startActivity<SummaryActivity>()
            }else{
                Toast.makeText(this@PhotoActivity,"Please Take A Photo", Toast.LENGTH_SHORT).show()
            }

        }
        btn_back.setOnClickListener {
            super.onBackPressed()
        }

        btn_start_camera = findViewById(R.id.btn_start_camera)
        iv_profile_picture = findViewById(R.id.iv_profile_picture)
        layout_camera = findViewById(R.id.layout_camera)

        btn_start_camera.text = "Camera"
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
    }

    val pictureCallback : Camera.PictureCallback = object : Camera.PictureCallback{
        override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
            layout_camera.visibility = View.GONE

            val matrix = Matrix()
            matrix.preScale(-1.0f,1.0f)
            val oriBitmap = BitmapFactory.decodeByteArray(data,0,data!!.size)
            val invertBitmap = Bitmap.createBitmap(oriBitmap,0,0,oriBitmap.width,oriBitmap.height,matrix,true)
            imagePhotoBitmap = invertBitmap

            iv_profile_picture.setImageBitmap(invertBitmap)
            iv_profile_picture.visibility = View.VISIBLE
        }
    }
    private fun captureImage(){
        if(camera!=null && isCameraInitialized) {
            try {
                camera!!.takePicture(null,null,pictureCallback)
                isCameraInitialized = false
                btn_start_camera.text = "Camera"
            }catch (e : Exception){}
        }
    }
    private fun initCamera(){
        val cameraInfo = Camera.CameraInfo()
        val cameraCount = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    camera = Camera.open(camIdx)
                    cameraPreview = CameraPreview(this, camera!!)
                    layout_camera.addView(cameraPreview)
                    isCameraInitialized = true
                    layout_camera.visibility = View.VISIBLE
                    iv_profile_picture.visibility = View.GONE
                    btn_start_camera.text = "Take A Photo"
                } catch (e: RuntimeException) {
                }
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

    override fun onBackPressed() {}
}
