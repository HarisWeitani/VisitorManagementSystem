package com.hw.vms.visitormanagementsystem.Activity

import android.app.Dialog
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.DataSet.ResponseBooking
import com.hw.vms.visitormanagementsystem.Helper.GlobalVal
import com.hw.vms.visitormanagementsystem.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SummaryActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView
    lateinit var btn_back : Button
    lateinit var btn_submit : Button

    lateinit var tv_name : TextView
    lateinit var tv_host : TextView
    lateinit var tv_company : TextView
    lateinit var tv_phone_number : TextView
    lateinit var tv_email : TextView
    lateinit var iv_profile_picture : ImageView

    var apiService : API? = API.networkApi()

    /***
     * Loading
     */
    var loadingDialog : Dialog? = null
    var thankyouDialog : Dialog? = null
    var submitFailedDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        initHeader()
        initView()
    }
    private fun initView(){

        loadingDialog = Dialog(this@SummaryActivity)
        thankyouDialog = Dialog(this@SummaryActivity)
        submitFailedDialog = Dialog(this@SummaryActivity)

        initLoadingDialog()
        initThankYouDialog()
        initSubmitFailed()


        tv_name = findViewById(R.id.tv_name)
        tv_host = findViewById(R.id.tv_host)
        tv_company = findViewById(R.id.tv_company)
        tv_phone_number = findViewById(R.id.tv_phone_number)
        tv_email = findViewById(R.id.tv_email)
        iv_profile_picture = findViewById(R.id.iv_profile_picture)
        btn_back = findViewById(R.id.btn_back)
        btn_submit = findViewById(R.id.btn_submit)

        btn_submit.setOnClickListener {
            loadingDialog?.show()
            submit(convertBitmapToFile())
        }
        btn_back.setOnClickListener {
            super.onBackPressed()
        }

        tv_name.text = DAO.name
        tv_host.text = DAO.host_name
        tv_company.text = DAO.company
        tv_phone_number.text = DAO.phone
        tv_email.text = DAO.email
        iv_profile_picture.setImageBitmap(DAO.imageBmp)
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
    fun initLoadingDialog(){
        loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(R.layout.loading_dialog)
    }
    fun initThankYouDialog(){
        thankyouDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        thankyouDialog?.setCancelable(false)
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
            startActivity<NameActivity>()
        },1000)
    }
    fun showSubmitFailedDialog(){
        submitFailedDialog?.show()
        Handler().postDelayed({
            submitFailedDialog?.dismiss()
        },3000)
    }
    private fun submitFailed(){
        loadingDialog?.dismiss()
        showSubmitFailedDialog()
    }
    private fun resetUI(){
        DAO.name = ""
        DAO.company = ""
        DAO.phone = ""
        DAO.email = ""
        DAO.host_id = ""
        DAO.host_name = ""
        DAO.imageBmp = null
    }
    override fun onBackPressed() {

    }


    private fun submit(imageFile : File){

        var host_id = RequestBody.create(MediaType.parse("text/plain"), DAO.host_id)
        var guest_name = RequestBody.create(MediaType.parse("text/plain"), DAO.name)
        var guest_phone = RequestBody.create(MediaType.parse("text/plain"), DAO.phone)
        var guest_company = RequestBody.create(MediaType.parse("text/plain"), DAO.company)
        var guest_email = RequestBody.create(MediaType.parse("text/plain"), DAO.email)
        var guest_address = RequestBody.create(MediaType.parse("text/plain"), "ADDRESS")

//        var guest_image : RequestBody? = null
//        if (guestImage != null) guest_image = RequestBody.create(MediaType.parse("image/*"), guestImage)
        var guest_image = RequestBody.create(MediaType.parse("image/*"), imageFile)

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["host_id"] = host_id
        requestBodyMap["guest_name"] = guest_name
        requestBodyMap["guest_phone"] = guest_phone
        requestBodyMap["guest_company"] = guest_company
        requestBodyMap["guest_email"] = guest_email
        requestBodyMap["guest_address"] = guest_address

        var image_body = MultipartBody.Part.createFormData("guest_image","guest_image.jpeg",guest_image)

        apiService!!.booking(requestBodyMap, image_body).enqueue(object :
            Callback<ResponseBooking> {
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
                    if(response.body().ok == 1) {
                        showThankYouDialog()
                    }else{
                        try{
                            Toast.makeText(this@SummaryActivity,"${response.body().message}",Toast.LENGTH_LONG).show()
                        }catch (e :Exception){}
                        submitFailed()
                    }
                }else{
                    submitFailed()
                }
            }
        })
    }
    private fun convertBitmapToFile() : File{
        val f = File(this.cacheDir, "guest_image.jpeg")
        f.createNewFile()

        //Convert bitmap to byte array
        val bitmap = DAO.imageBmp
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
}
