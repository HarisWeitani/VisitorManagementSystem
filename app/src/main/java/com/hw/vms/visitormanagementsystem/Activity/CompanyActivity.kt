package com.hw.vms.visitormanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.R
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class CompanyActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView

    lateinit var et_company : EditText
    lateinit var btn_back : Button
    lateinit var btn_next : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)

        initHeader()
        initView()
    }

    override fun onResume() {
        super.onResume()
        try {
            if( !DAO.company.isNullOrEmpty() ){
                et_company.setText(DAO.company)
            }
        }catch (e:Exception){}
    }

    private fun initView(){
        et_company = findViewById(R.id.et_company)
        btn_next = findViewById(R.id.btn_next)
        btn_back = findViewById(R.id.btn_back)

        btn_next.setOnClickListener {
            if( !et_company.text.isNullOrEmpty() ){
                DAO.company = et_company.text.toString()
                startActivity<PhoneEmailActivity>()
            }else{
                Toast.makeText(this@CompanyActivity,"Company Cannot Be Empty", Toast.LENGTH_SHORT).show()
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
