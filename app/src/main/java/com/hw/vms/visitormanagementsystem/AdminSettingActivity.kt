package com.hw.vms.visitormanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.Helper.GlobalVal
import com.hw.vms.visitormanagementsystem.Helper.SettingsData
import com.hw.vms.visitormanagementsystem.Helper.SharedPreference
import kotlinx.android.synthetic.main.activity_admin_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AdminSettingActivity : AppCompatActivity() {

    lateinit var sp_server : Spinner
    val http_https = arrayOf("http","https")

    lateinit var btnBack : LinearLayout
    lateinit var btn_save_and_exit : Button
    var sharePref : SharedPreference? = null

    lateinit var btn_try_serverconn : Button
    lateinit var et_server_url : EditText

    lateinit var etChangeAdminPin : EditText

    var apiService : API? = null
    var serverConnected : Boolean = false
    var serverProtocol : String? = null

    var serverUrl : String? = null

    lateinit var tv_clock : TextView
    lateinit var tv_date : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_setting)

        sharePref = SharedPreference(this)
        initViews()
    }
    private fun initViews(){

        etChangeAdminPin = et_change_admin_pin
        sp_server = findViewById(R.id.spinner_server)

        //button
        btn_save_and_exit = findViewById(R.id.btn_save_and_exit)
        btn_save_and_exit.visibility = View.GONE
        btnBack = findViewById(R.id.btnBack)

        btn_try_serverconn = findViewById(R.id.btn_try_serverconn)

        et_server_url = findViewById(R.id.et_server_url)
        et_server_url.setText(DAO.settingsData?.server_url)

        tv_clock = findViewById(R.id.tv_clock)
        tv_date = findViewById(R.id.tv_date)

        val aaServer = ArrayAdapter(this, android.R.layout.simple_spinner_item, http_https)
        aaServer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_server.adapter = aaServer
        sp_server.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                serverProtocol = http_https[position]
            }

        }

        initDateTime()
        initButtonListener()
    }
    private fun initDateTime(){
        val date = Date()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        val clockFormat = SimpleDateFormat("HH:mm")
        runOnUiThread{
            tv_date.text = dateFormat.format(date)
            tv_clock.text = clockFormat.format(date)
        }
        Handler().postDelayed({
            initDateTime()
        },10000)
    }
    private fun initButtonListener(){
        btnBack.setOnClickListener {
            startActivity(Intent(this@AdminSettingActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        btn_try_serverconn.setOnClickListener {
            if( et_server_url.text.length > 10 ){
                API.serverUrl = "$serverProtocol://${et_server_url.text}/"
                serverUrl = et_server_url.text.toString()
                connectServer()
            }else{
                Toast.makeText(this@AdminSettingActivity,"Wrong Url",Toast.LENGTH_LONG).show()
            }
        }

        btn_save_and_exit.setOnClickListener {
            var pin = DAO.settingsData?.admin_pin
            if (etChangeAdminPin.text.toString().length == 4) {
                pin = etChangeAdminPin.text.toString()
            } else if (etChangeAdminPin.text.toString().isNotEmpty()) {
                Toast.makeText(
                    this@AdminSettingActivity,
                    " Pin Not Accepted ",
                    Toast.LENGTH_LONG
                ).show()
            }

            DAO.settingsData = SettingsData(
                server_url = serverUrl,
                server_full_url = API.serverUrl,
                admin_pin = pin
            )

            val settingDataJson = Gson().toJson(DAO.settingsData)

            sharePref!!.save(GlobalVal.FRESH_INSTALL_KEY, false)
            sharePref!!.save(GlobalVal.SETTINGS_DATA_KEY, settingDataJson)
            startActivity(
                Intent(this@AdminSettingActivity, MainActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            )
        }

    }
    private fun connectServer(){
        runOnUiThread {
            btn_try_serverconn.text = getString(R.string.connecting)
            btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_yellow))
        }
        apiService = API.networkApi()
/*        apiService!!.getConfigData().enqueue(object : Callback<ResponseConfig> {
            override fun onFailure(call: Call<ResponseConfig>?, t: Throwable?) {
                GlobalVal.networkLogging("connectServer onFailure",t.toString())
                runOnUiThread {
                    btn_try_serverconn.text = getString(R.string.failed)
                    btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_red))
                    Toast.makeText(this@AdminSettingActivity,"Connection Failed Please Try Again", Toast.LENGTH_LONG).show()
                }
                serverConnected = false
            }
            override fun onResponse(call: Call<ResponseConfig>?, response: Response<ResponseConfig>?) {
                GlobalVal.networkLogging("connectServer onResponse",response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.configData = response.body()
                    getBuildingList()
                }else{
                    serverConnected = false
                }
            }
        })*/
    }
    private fun serverConnected(){
        runOnUiThread {
            btn_try_serverconn.text = getString(R.string.success)
            btn_try_serverconn.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.status_green))
            btn_save_and_exit.visibility = View.VISIBLE
            et_server_url.isEnabled = false
        }
    }

}
