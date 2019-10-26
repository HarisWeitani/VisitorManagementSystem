package com.hw.vms.visitormanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.Adapter.HostAdapter
import com.hw.vms.visitormanagementsystem.DataSet.DataHost
import com.hw.vms.visitormanagementsystem.R
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class HostActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView
    lateinit var btn_back : Button
    lateinit var btn_next : Button
    lateinit var actv_host : AutoCompleteTextView

    var hostId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        initHeader()
        initView()
    }

    private fun initView(){
        btn_next = findViewById(R.id.btn_next)
        btn_back = findViewById(R.id.btn_back)
        actv_host = findViewById(R.id.actv_host)

        btn_next.setOnClickListener {
            startActivity<HostActivity>()
        }
        btn_back.setOnClickListener {
            super.onBackPressed()
        }

        actv_host.isEnabled = DAO.responseGetHost != null

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
}
