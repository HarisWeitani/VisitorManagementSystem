package com.hw.vms.visitormanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.Activity.MainActivity
import com.hw.vms.visitormanagementsystem.Activity.NameActivity

class AdminLoginActivity : AppCompatActivity() {

    lateinit var et_admin_pin : EditText
    lateinit var btnBack : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initView()
    }

    private fun initView(){
        et_admin_pin = findViewById(R.id.et_admin_pin)
        btnBack = findViewById(R.id.btnBack)

        var pinNow = DAO.settingsData?.admin_pin
        if( pinNow == null ) pinNow = "1111"

        et_admin_pin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if( s.toString() == pinNow ) {
                    finish()
                    startActivity(Intent(this@AdminLoginActivity, AdminSettingActivity::class.java))
                }
                else if ( s.toString().length == 4)
                    Toast.makeText(this@AdminLoginActivity,"Wrong Code", Toast.LENGTH_SHORT).show()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        btnBack.setOnClickListener {
            startActivity(Intent(this@AdminLoginActivity,
                NameActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }

    }

}