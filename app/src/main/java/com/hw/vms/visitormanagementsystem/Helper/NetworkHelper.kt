package com.hw.vms.visitormanagementsystem.Helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.hw.rms.roommanagementsystem.Helper.API
import com.hw.rms.roommanagementsystem.Helper.DAO
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetVisitorNumber
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NetworkHelper{
    var apiService : API? = API.networkApi()
    var isGetVisitorFinished : Boolean = false
    var isGetHostFinished : Boolean = false

    fun getVisitorNumber(context : Context){
        isGetVisitorFinished = false

        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var current_date = RequestBody.create(MediaType.parse("text/plain"), dateFormat.format(date))

        val requestBodyMap = HashMap<String, RequestBody>()
        requestBodyMap["current_date"] = current_date

        apiService!!.getVisitorNumber(requestBodyMap).enqueue(object :
            Callback<ResponseGetVisitorNumber> {
            override fun onFailure(call: Call<ResponseGetVisitorNumber>?, t: Throwable?) {
                isGetVisitorFinished = true
                Log.d(GlobalVal.NETWORK_TAG,"onFailure getVisitorNumber "+t.toString())
                Toast.makeText(context,"Get Total Visitor Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseGetVisitorNumber>?,
                response: Response<ResponseGetVisitorNumber>?
            ) {
                isGetVisitorFinished = true
                Log.d(GlobalVal.NETWORK_TAG,"onResponse getVisitorNumber "+response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.responseGetVisitorNumber = response.body()
                }else{
                    Toast.makeText(context,"Get Total Visitor Failed", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    fun getAllHost(context : Context){

        isGetHostFinished = false

        apiService!!.getAllHost().enqueue(object : Callback<ResponseGetHost> {
            override fun onFailure(call: Call<ResponseGetHost>?, t: Throwable?) {
                isGetHostFinished = true
                Log.d(GlobalVal.NETWORK_TAG,"onFailure getAllHost "+t.toString())
                Toast.makeText(context,"Get New Host Failed", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ResponseGetHost>?, response: Response<ResponseGetHost>?) {
                isGetHostFinished = true
                Log.d(GlobalVal.NETWORK_TAG,"onResponse getAllHost "+response?.body().toString())
                if( response?.code() == 200 && response.body() != null ){
                    DAO.responseGetHost = response.body()
                }else{
                    Toast.makeText(context,"Get New Host Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}