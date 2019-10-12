package com.hw.vms.visitormanagementsystem

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraPreview(internal var context: Context,internal var camera : Camera) : SurfaceView(context), SurfaceHolder.Callback {

    lateinit var mHolder : SurfaceHolder

    init {
        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        var params : Camera.Parameters = camera.parameters

        if(this.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation","potrait")
            camera.setDisplayOrientation(90)
            params.setRotation(90)
        }else{
            params.set("orientation","landscape")
            camera.setDisplayOrientation(0)
            params.setRotation(0)
        }
        camera.parameters = params
        try{
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        }catch (e:Exception){}
    }

}