package com.hw.vms.visitormanagementsystem.Activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.Adapter.HostAdapter
import com.hw.vms.visitormanagementsystem.DataSet.DataHost
import com.hw.vms.visitormanagementsystem.Helper.NetworkHelper
import com.hw.vms.visitormanagementsystem.R
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class HostActivity : AppCompatActivity(), NetworkHelper.NetworkCallback {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView
    lateinit var btn_back : Button
    lateinit var btn_next : Button
    lateinit var actv_host : AutoCompleteTextView

    lateinit var btn_refresh_host : Button
    var loadingDialog : Dialog? = null

    var hostId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        initHeader()
        initView()
        loadingDialog = Dialog(this@HostActivity)
        initLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        try {
            if( !DAO.host_name.isNullOrEmpty() && !DAO.host_id.isNullOrEmpty() ){
                actv_host.setText(DAO.host_name)
                hostId = DAO.host_id
            }
        }catch (e:Exception){}

        if( DAO.responseGetHost == null ){
            refreshHost()
        }
    }

    private fun refreshHost(){
        loadingDialog?.show()
        val networkHelper = NetworkHelper(this)
        networkHelper.getAllHost(this)
        networkHelper.getVisitorNumber(this)
    }

    private fun initView(){
        btn_next = findViewById(R.id.btn_next)
        btn_back = findViewById(R.id.btn_back)
        actv_host = findViewById(R.id.actv_host)
        btn_refresh_host = findViewById(R.id.btn_refresh_host)


        btn_next.setOnClickListener {

            if( hostId != null && !actv_host.text.isNullOrEmpty()) {
                DAO.host_id = hostId
                DAO.host_name = actv_host.text.toString()
                startActivity<PhotoActivity>()
            }else{
                Toast.makeText(this@HostActivity,"Please Select The Host", Toast.LENGTH_SHORT).show()
            }

        }
        btn_back.setOnClickListener {
            super.onBackPressed()
        }

        btn_refresh_host.setOnClickListener {
            refreshHost()
        }

        if(DAO.responseGetHost == null){
            actv_host.isEnabled = false
            btn_refresh_host.visibility = View.VISIBLE
        }else{
            actv_host.isEnabled = true
            btn_refresh_host.visibility = View.GONE
        }

        actv_host.setOnItemClickListener { parent, view, position, id ->
            Log.d("ahsiap","asdas $id")
            hostId = id.toString()
        }
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
    fun initLoadingDialog(){
        loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(R.layout.loading_dialog)
    }
    override fun callbackGetAllHost(status: Boolean) {
        loadingDialog?.dismiss()
        if( status ){
            btn_refresh_host.visibility = View.GONE
            initAutoCompleteHost()
        }else{
            btn_refresh_host.visibility = View.VISIBLE
        }
    }
    override fun callbackGetVisitor(res: Int) {

    }


}
