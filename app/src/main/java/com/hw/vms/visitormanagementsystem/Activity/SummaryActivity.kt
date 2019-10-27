package com.hw.vms.visitormanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.R
import java.text.SimpleDateFormat
import java.util.*

class SummaryActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView
    lateinit var btn_back : Button
    lateinit var btn_next : Button

    lateinit var tv_name : TextView
    lateinit var tv_host : TextView
    lateinit var tv_company : TextView
    lateinit var tv_phone_number : TextView
    lateinit var tv_email : TextView
    lateinit var iv_profile_picture : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        initHeader()
        initView()
    }
    private fun initView(){
        tv_name = findViewById(R.id.tv_name)
        tv_host = findViewById(R.id.tv_host)
        tv_company = findViewById(R.id.tv_company)
        tv_phone_number = findViewById(R.id.tv_phone_number)
        tv_email = findViewById(R.id.tv_email)
        iv_profile_picture = findViewById(R.id.iv_profile_picture)

        tv_name.text = DAO.name
        tv_host.text = DAO.host_name
        tv_company.text = DAO.company
        tv_phone_number.text = DAO.company
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
    override fun onBackPressed() {

    }
}
