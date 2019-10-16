package com.hw.vms.visitormanagementsystem

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.Adapter.HostAdapter
import com.hw.vms.visitormanagementsystem.DataSet.DataHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseBooking
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetVisitorNumber
import com.hw.vms.visitormanagementsystem.Helper.GlobalVal
import com.hw.vms.visitormanagementsystem.Helper.SettingsData
import com.hw.vms.visitormanagementsystem.Helper.SharedPreference
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
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
    lateinit var actv_host : AutoCompleteTextView
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
    var imagePhotoBitmap : Bitmap? = null

    /***
     * Loading
     */
    var loadingDialog : Dialog? = null
    var thankyouDialog : Dialog? = null
    var submitFailedDialog : Dialog? = null

    /***
     * Networking API
     */
    var apiService : API? = null
    var isGetVisitorSuccess : Boolean = false
    var isGetBoothSuccess : Boolean = false
    var firstInstall : Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sharepref = SharedPreference(this)
        firstInstall = sharepref.getValueBoolean(GlobalVal.FRESH_INSTALL_KEY,true)
        DAO.settingsData = Gson().fromJson(sharepref.getValueString(GlobalVal.SETTINGS_DATA_KEY), SettingsData::class.java)

    }

    private fun initApp(){
        apiService = API.networkApi()
        initView()
        loadingDialog = Dialog(this@MainActivity)
        thankyouDialog = Dialog(this@MainActivity)
        submitFailedDialog = Dialog(this@MainActivity)
        initLoadingDialog()
        initThankYouDialog()
        initSubmitFailed()
        getVisitorNumber()
        getAllHost()
        refreshData()
    }

    private fun initView(){
        iv_logo = findViewById(R.id.iv_logo)
        tv_visitor_number = findViewById(R.id.tv_visitor_number)
        tv_date_now = findViewById(R.id.tv_date_now)
        et_name = findViewById(R.id.et_name)
        et_host = findViewById(R.id.et_host)
        actv_host = findViewById(R.id.actv_host)
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

            if( validateData() ) {
                loadingDialog?.show()
                submit()
            }
            else{
                Toast.makeText(this@MainActivity,"Mohon Isi Semua Data", Toast.LENGTH_LONG).show()
            }
        }

        iv_logo.setOnLongClickListener {
            startActivity(Intent(this@MainActivity,AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            true
        }

        actv_host.isEnabled = DAO.responseGetHost != null

        actv_host.setOnItemClickListener { parent, view, position, id ->
            Log.d("ahsiap","asdas $id")
            hostId = id.toString()
        }

        initDate()
        initVisitorNumber()
        initAutoCompleteHost()
    }

    private fun initAutoCompleteHost(){
        try {
            actv_host.setAdapter(
                HostAdapter(
                    this, R.layout.my_list_item_1,
                    DAO.responseGetHost?.data as List<DataHost>
                )
            )
            actv_host.isEnabled = DAO.responseGetHost != null
        }catch (e:Exception){}
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
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            isPermitted = true
        }
        if(firstInstall){
            startActivity(Intent(this@MainActivity,AdminLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }else if (isPermitted){
            initApp()

            layout_camera.visibility = View.GONE
            iv_profile_picture.visibility = View.VISIBLE
        }
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
        var res = 0
        try {
            res = DAO.responseGetVisitorNumber?.visitor_number!!
        }catch (e:Exception){}
        tv_visitor_number.text = "Visitor Number : $res"
    }
    fun initLoadingDialog(){
        loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(R.layout.loading_dialog)
    }
    fun initThankYouDialog(){
        thankyouDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        thankyouDialog?.setCancelable(true)
        thankyouDialog?.setContentView(R.layout.thankyou_dialog)
    }
    fun initSubmitFailed(){
        submitFailedDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        submitFailedDialog?.setCancelable(true)
        submitFailedDialog?.setContentView(R.layout.submit_failed_dialog)
    }
    fun showThankYouDialog(){
        thankyouDialog?.show()
        resetUI()
        Handler().postDelayed({
            thankyouDialog?.dismiss()
        },1500)
    }
    fun showSubmitFailedDialog(){
        submitFailedDialog?.show()
        Handler().postDelayed({
            submitFailedDialog?.dismiss()
        },1500)
    }

    private fun resetUI(){
        isCameraInitialized = false
        layout_camera.visibility = View.GONE
        iv_profile_picture.visibility = View.VISIBLE
        iv_profile_picture.setImageResource(R.drawable.profile_picture_default_transparent)

        et_name.text = null
        et_host.text = null
        actv_host.text = null
        et_address.text = null
        et_phone_number.text = null
    }

    var hostId : String? = null
    var guestName : String? = null
    var guestPhone : String? = null
    var guestCompany : String? = null
    var guestAddress : String? = null
    var guestImage : File? = null

    private fun validateData() : Boolean{
        getInputUser()
        return ( !hostId.isNullOrEmpty() &&
                !guestName.isNullOrEmpty() &&
                !guestPhone.isNullOrEmpty() &&
                !guestCompany.isNullOrEmpty() &&
                !guestAddress.isNullOrEmpty() &&
                guestImage != null
                )
    }

    private fun getInputUser(){
        guestName = et_name.text.toString()
        guestPhone = et_phone_number.text.toString()
        guestCompany = " "
        guestAddress = et_address.text.toString()
        if(imagePhotoBitmap!=null) {
            guestImage = convertBitmapToFile()
        }
    }

    private fun submit(){

        var host_id = RequestBody.create(MediaType.parse("text/plain"), hostId)
        var guest_name = RequestBody.create(MediaType.parse("text/plain"), guestName)
        var guest_phone = RequestBody.create(MediaType.parse("text/plain"), guestPhone)
        var guest_company = RequestBody.create(MediaType.parse("text/plain"), guestCompany)
        var guest_address = RequestBody.create(MediaType.parse("text/plain"), guestAddress)
        var guest_image = RequestBody.create(MediaType.parse("image/*"), guestImage)
//        var guest_image = RequestBody.create(MediaType.parse("image/*"), convertBitmapToFile())

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["host_id"] = host_id
        requestBodyMap["guest_name"] = guest_name
        requestBodyMap["guest_phone"] = guest_phone
        requestBodyMap["guest_company"] = guest_company
        requestBodyMap["guest_address"] = guest_address

        val image_body = MultipartBody.Part.createFormData("guest_image","guest_image.jpeg",guest_image)

        apiService!!.booking(requestBodyMap, image_body).enqueue(object : Callback<ResponseBooking>{
            override fun onFailure(call: Call<ResponseBooking>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG,"onFailure submit "+t.toString())
                loadingDialog?.dismiss()
                showSubmitFailedDialog()
            }

            override fun onResponse(
                call: Call<ResponseBooking>?,
                response: Response<ResponseBooking>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG,"onResponse submit "+response.toString())
                if( response?.code() == 200 && response.body() != null ) {
                    loadingDialog?.dismiss()
                    showThankYouDialog()
                }else{
                    loadingDialog?.dismiss()
                    showSubmitFailedDialog()
                }
            }

        })
    }

    private fun convertBitmapToFile() : File{
        val f = File(this.cacheDir, "guest_image.jpeg")
        f.createNewFile()

        //Convert bitmap to byte array
        val bitmap = imagePhotoBitmap
        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        var fos : FileOutputStream? = null
        try {
            fos = FileOutputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos!!.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e : IOException) {
            e.printStackTrace()
        }
        return f
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

    /***
     * Get Data Every 1 Minute
     */

    private fun refreshData(){

        Handler().postDelayed({
            Log.d(GlobalVal.NETWORK_TAG,"Refresh Data")
            if(isGetVisitorSuccess && isGetBoothSuccess) {
                Log.d(GlobalVal.NETWORK_TAG,"Refresh Data Start")
                getVisitorNumber()
                getAllHost()
            }
            refreshData()
        },60000)

    }

    private fun getVisitorNumber(){
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var current_date = RequestBody.create(MediaType.parse("text/plain"), dateFormat.format(date))

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["current_date"] = current_date

        apiService!!.getVisitorNumber(requestBodyMap).enqueue(object : Callback<ResponseGetVisitorNumber>{
            override fun onFailure(call: Call<ResponseGetVisitorNumber>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG,"onFailure getVisitorNumber "+t.toString())
                Toast.makeText(this@MainActivity,"Get Total Visitor Failed", Toast.LENGTH_SHORT).show()
                isGetVisitorSuccess = false
            }

            override fun onResponse(
                call: Call<ResponseGetVisitorNumber>?,
                response: Response<ResponseGetVisitorNumber>?
            ) {
                Log.d(GlobalVal.NETWORK_TAG,"onResponse getVisitorNumber "+response.toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.responseGetVisitorNumber = response.body()
                    initVisitorNumber()
                    isGetVisitorSuccess = true
                }else{
                    isGetVisitorSuccess = false
                    Toast.makeText(this@MainActivity,"Get Total Visitor Failed", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun getAllHost(){
        apiService!!.getAllHost().enqueue(object : Callback<ResponseGetHost> {
            override fun onFailure(call: Call<ResponseGetHost>?, t: Throwable?) {
                Log.d(GlobalVal.NETWORK_TAG,"onFailure getAllHost "+t.toString())
                Toast.makeText(this@MainActivity,"Get New Host Failed", Toast.LENGTH_SHORT).show()
                isGetBoothSuccess = false
            }
            override fun onResponse(call: Call<ResponseGetHost>?, response: Response<ResponseGetHost>?) {
                Log.d(GlobalVal.NETWORK_TAG,"onResponse getAllHost "+response.toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.responseGetHost = response.body()
                    initAutoCompleteHost()
                    actv_host.isEnabled = true
                    isGetBoothSuccess = true
                }else{
                    Toast.makeText(this@MainActivity,"Get New Host Failed", Toast.LENGTH_SHORT).show()
                    isGetBoothSuccess = false
                }
            }
        })
    }
}
