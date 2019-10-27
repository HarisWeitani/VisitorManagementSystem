package com.hw.vms.visitormanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.R
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class PhoneEmailActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView

    lateinit var btn_back : Button
    lateinit var btn_next : Button

    lateinit var et_phone_number : EditText
    lateinit var et_email : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_email)

        initHeader()
        initView()
    }

    override fun onResume() {
        super.onResume()
        try {
            if( !DAO.phone.isNullOrEmpty() ){
                et_phone_number.setText(DAO.phone)
            }
            if( !DAO.email.isNullOrEmpty() ){
                et_email.setText(DAO.email)
            }
        }catch (e:Exception){}
    }

    private fun initView(){
        btn_next = findViewById(R.id.btn_next)
        btn_back = findViewById(R.id.btn_back)

        et_phone_number = findViewById(R.id.et_phone_number)
        et_email = findViewById(R.id.et_email)

        btn_next.setOnClickListener {
            if( !et_phone_number.text.isNullOrEmpty() && !et_email.text.isNullOrEmpty() ){
                DAO.phone = et_phone_number.text.toString()
                DAO.email = et_email.text.toString()
                startActivity<HostActivity>()
            }else{
                Toast.makeText(this@PhoneEmailActivity,"Phone and Email Cannot Be Empty", Toast.LENGTH_SHORT).show()
            }

        }
        btn_back.setOnClickListener {
            super.onBackPressed()
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
