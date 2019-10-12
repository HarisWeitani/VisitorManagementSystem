package com.hw.vms.visitormanagementsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

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

        btn_start_camera.setOnClickListener {

        }

        btn_save.setOnClickListener {

        }

        initDate()
    }

    private fun initDate(){
        val date = Date()
        val df = SimpleDateFormat("dd/MM/yyyy")
        runOnUiThread {
            tv_date_now.text = "Date : ${df.format(date)}"
        }
    }

}
