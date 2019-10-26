package com.hw.vms.visitormanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.R
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class NameActivity : AppCompatActivity() {

    lateinit var iv_logo : ImageView
    lateinit var tv_visitor_number : TextView
    lateinit var tv_date_now : TextView

    lateinit var et_name : EditText
    lateinit var btn_next : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        initHeader()
        initView()
    }

    private fun initView(){
        et_name = findViewById(R.id.et_name)
        btn_next = findViewById(R.id.btn_next)

        btn_next.setOnClickListener {
            if( !et_name.text.isNullOrEmpty() ){
                DAO.name = et_name.text.toString()
                startActivity<CompanyActivity>()
            }else{
                Toast.makeText(this@NameActivity,"Name Cannot Be Empty", Toast.LENGTH_SHORT).show()
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
    override fun onBackPressed() {

    }
}
