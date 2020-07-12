package com.example.azizi.kotlincameraserver
/**
 * Created by Azizi on 12/07/2020.
 */
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

class MainActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    var mPreview: MyCameraView? = null
    var serverStatus: TextView? = null
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        serverStatus = findViewById(R.id.textView) as TextView
        SERVERIP = localIpAddress
        mCamera = cameraInstance
        mPreview = MyCameraView(this, mCamera)
        val preview = findViewById(R.id.camera_preview) as FrameLayout
        preview.addView(mPreview)
        val cThread = Thread(MyServerThread(this, SERVERIP!!, SERVERPORT, handler))
        cThread.start()
    }

    /**
     * Get local ip address of the phone
     * @return ipAddress
     */
    private val localIpAddress: String?
        private get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress().toString()
                        }
                    }
                }
            } catch (ex: SocketException) {
                Log.e("ServerActivity", ex.toString())
            }
            return null
        }

    companion object {
        var SERVERIP: String? = "localhost"
        const val SERVERPORT = 9191

        /**
         * Get camera instance
         * @return
         */
        val cameraInstance: Camera?
            get() {
                var c: Camera? = null
                try {
                    c = Camera.open()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return c
            }
    }
}
