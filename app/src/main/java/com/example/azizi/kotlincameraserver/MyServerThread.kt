package com.example.azizi.kotlincameraserver
/**
 * Created by Azizi on 12/07/2020.
 */
import android.content.Context
import android.os.Handler
import android.util.Log
import java.io.DataOutputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class MyServerThread(
    private val mContext: Context,
    private val mServerIP: String,
    private val mServerPort: Int,
    private val mHandler: Handler
) :
    Runnable {
    private val mActivityInstance: MainActivity
    override fun run() {
        try {
            val ss = ServerSocket(mServerPort)
            mHandler.post { mActivityInstance.serverStatus!!.text = "Listening on IP: $mServerIP" }
            while (true) {
                val s = ss.accept()
                //socketList.add(ss);
                Thread(ServerSocketThread(s)).start()
            }
        } catch (e: Exception) {
            Log.d("ServerThread", "run: erro")
        }
    }

    inner class ServerSocketThread(s: Socket?) : Runnable {
        var s: Socket? = null

        // BufferedReader br = null;
        //BufferedWriter bw = null;
        var os: OutputStream? = null
        override fun run() {
            if (s != null) {
                val clientIp = s!!.inetAddress.toString().replace("/", "")
                val clientPort = s!!.port
                println("====client ip=====$clientIp")
                println("====client port=====$clientPort")
                try {
                    s!!.keepAlive = true
                    os = s!!.getOutputStream()
                    while (true) {
                        //服务器端向客户端发送数据
                        //dos.write(mPreview.mFrameBuffer.);
                        val dos = DataOutputStream(os)
                        dos.writeInt(4)
                        dos.writeUTF("#@@#")
                        dos.writeInt(mActivityInstance.mPreview!!.mFrameBuffer!!.size())
                        dos.writeUTF("-@@-")
                        dos.flush()
                        System.out.println(mActivityInstance.mPreview!!.mFrameBuffer!!.size())
                        dos.write(mActivityInstance.mPreview!!.mFrameBuffer!!.toByteArray())
                        //System.out.println("outlength"+mPreview.mFrameBuffer.length);
                        dos.flush()
                        Thread.sleep(1000 / 15.toLong())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        if (os != null) os!!.close()
                    } catch (e2: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                println("socket is null")
            }
        }

        init {
            this.s = s
            //br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        }
    }

    init {
        mActivityInstance = mContext as MainActivity
    }
}

