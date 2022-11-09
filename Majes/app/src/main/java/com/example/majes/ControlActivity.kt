package com.example.majes

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object{
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        ,
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnect: Boolean = false
        lateinit var m_address: String

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        val maju = findViewById<Button>(R.id.control_maju)
        val mundur = findViewById<Button>(R.id.control_mundur)
        val kiri = findViewById<Button>(R.id.control_kiri)
        val kanan = findViewById<Button>(R.id.control_kanan)
        val disconnect = findViewById<Button>(R.id.control_disconnect)

        ConnectToDevice(this).execute()

        maju.setOnClickListener { sendCommand("f") }
        mundur.setOnClickListener { sendCommand("b") }
        kiri.setOnClickListener { sendCommand("l") }
        kanan.setOnClickListener { sendCommand("r") }
        disconnect.setOnClickListener { disconnect() }

    }

    private fun sendCommand(input: String){
        if (m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){
        if (m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnect = false
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) :  AsyncTask<Void, Void, String>(){
        private var connectSucess :Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg params: Void?): String {
            try{
                if (m_bluetoothSocket == null || !m_isConnect)
                {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException){
                connectSucess = false
                e.printStackTrace()
            }
            return null.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSucess){
                Log.i("data", "couldn't connect")
            }else{
                m_isConnect = true
            }
            m_progress.dismiss()
        }
    }
}