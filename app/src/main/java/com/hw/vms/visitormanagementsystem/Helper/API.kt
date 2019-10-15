package com.hw.rms.roommanagementsystem.Helper

import android.util.Log
import com.google.gson.GsonBuilder
import com.hw.vms.visitormanagementsystem.DataSet.ResponseBooking
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetHost
import com.hw.vms.visitormanagementsystem.DataSet.ResponseGetVisitorNumber
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.net.Socket
import java.util.concurrent.TimeUnit

interface API {

    @GET("/vms/api/host/get_host")
    fun getAllHost() : Call<ResponseGetHost>

    @Multipart
    @POST("/vms/api/booking/visitor_number")
    fun getVisitorNumber(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>) : Call<ResponseGetVisitorNumber>

    @Multipart
    @POST("/vms/api/booking/")
    fun booking(@PartMap params : Map<String, @JvmSuppressWildcards RequestBody>, @Part image : MultipartBody.Part) : Call<ResponseBooking>

    companion object Factory{

//        http://139.180.142.76/room_management_system

//        var serverUrl : String? = DAO.settingsData?.server_full_url
        var serverUrl : String? = "http://139.180.142.76"

        fun networkApi() : API{
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(60,TimeUnit.SECONDS)
                .build()
            //try catch ini
            val retrofit = Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            return retrofit.create(API::class.java)
        }
    }

}