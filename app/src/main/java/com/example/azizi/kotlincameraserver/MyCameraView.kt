package com.example.azizi.kotlincameraserver
/**
 * Created by Azizi on 12/07/2020.
 */
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.ByteArrayOutputStream

class MyCameraView(private val con: Context, private var mCamera: Camera?) :
    SurfaceView(con), SurfaceHolder.Callback, PreviewCallback {
    private val mHolder: SurfaceHolder
    var width: Int? = 0
    var height: Int? = 0
    var mFrameBuffer: ByteArrayOutputStream? = null

    /**
     * set preview to the camera
     * @param holder
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            mCamera!!.setPreviewDisplay(holder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * surface destroyed function
     * @param holder
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera!!.setPreviewCallback(null)
        mCamera!!.release()
        mCamera = null
    }

    /**
     * surface changed function
     * @param holder
     * @param format
     * @param w
     * @param h
     */
    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        w: Int,
        h: Int
    ) {
        try {
            mCamera!!.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            //Configration Camera Parameter(full-size)
            val parameters = mCamera!!.parameters
            parameters.setPreviewSize(320, 240)
            width = parameters.previewSize.width
            height = parameters.previewSize.height
            parameters.previewFormat = ImageFormat.NV21
            mCamera!!.parameters = parameters
            // mCamera.setDisplayOrientation(90);
            mCamera!!.setPreviewCallback(this)
            mCamera!!.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * frame call back function
     * @param data
     * @param camera
     */
    override fun onPreviewFrame(
        data: ByteArray,
        camera: Camera
    ) {
        try {
            //convert YuvImage(NV21) to JPEG Image data
            val yuvimage = YuvImage(data, ImageFormat.NV21, this!!.width!!, this!!.height!!, null)
            println("WidthandHeight" + yuvimage.height + "::" + yuvimage.width)
            val baos = ByteArrayOutputStream()
            yuvimage.compressToJpeg(Rect(0, 0, width!!, height!!), 100, baos)
            mFrameBuffer = baos
        } catch (e: Exception) {
            Log.d("parse", "errpr")
        }
    }

    /**
     * Constructor of the MyCameraView
     * @param context
     * @param camera
     */
    init {
        mHolder = holder
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }
}


