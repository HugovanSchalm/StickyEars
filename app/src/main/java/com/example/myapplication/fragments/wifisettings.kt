package com.example.myapplication.fragments

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [wifisettings.newInstance] factory method to
 * create an instance of this fragment.
 */
class wifisettings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var queue: RequestQueue
    val wifi_info: SharedPreferences? =context?.getSharedPreferences("color", Context.MODE_PRIVATE)
    val ip_info: SharedPreferences? = context?.getSharedPreferences("ip", Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        queue = Volley.newRequestQueue(context)
        // Inflate the layout for this fragment
        val view:View =inflater.inflate(R.layout.fragment_wifisettings, container, false)
        val btnPair= view.findViewById<Button>(R.id.btnPair)
        val open_wifi= view.findViewById<Button>(R.id.open_wifi)

        btnPair.setOnClickListener{
            pair(queue,view)

        }
        open_wifi.setOnClickListener{
            openWifiSettings()

        }

        return view
    }


    fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivity(intent)

    }



    private fun pair(queue: RequestQueue,view: View){
        val edttxtSSID= view.findViewById<EditText>(R.id.edttxtSSID)
        val edttxtPwd= view.findViewById<EditText>(R.id.edttxtPwd)






        if (getCurrentSSID().equals("\"StickyEar\"", true)){
            if(edttxtSSID.text.isEmpty()){
                Toast.makeText(context, "Please enter an SSID", Toast.LENGTH_LONG).show()
                return
            }
            if(edttxtPwd.text.isEmpty()){
                Toast.makeText(context, "Please enter a password", Toast.LENGTH_LONG).show()
                return
            }
            val ssid = edttxtSSID.text
            val pwd = edttxtPwd.text
            val data = JSONObject()
            data.put("SSID", ssid)
            data.put("pswd", pwd)
            val url = "http://10.0.0.5:5000/pair"
            val request = JsonObjectRequest(Request.Method.POST, url, data, {
                    response -> println(response)
                val inflater = context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view2 = inflater.inflate(R.layout.switchwifipopup, null)
                val width = LinearLayout.LayoutParams.WRAP_CONTENT
                val height = LinearLayout.LayoutParams.WRAP_CONTENT
                val popup = PopupWindow(view2, width, height)
                popup.showAtLocation(view2, Gravity.CENTER, 0, 0)
                val popuptxt = view2.findViewById(R.id.popupTxt) as TextView
                val popupbtn = view2.findViewById(R.id.popupBtn) as Button
                popuptxt.text = "Please switch to the WiFi network called $ssid"
                popupbtn.setOnClickListener {
                    if(getCurrentSSID().equals("\"$ssid\"")) {
                        popup.dismiss()
                        getStickyEarInNetwork(queue)
                    } else {
                        Toast.makeText(context, "Please connect to $ssid", Toast.LENGTH_LONG).show()
                    }
                }
            }, {
                    error -> println(error)
                Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_LONG).show()
            })
            queue.add(request)
        } else {
            Toast.makeText(context, "Please connect to the StickyEar WiFi network", Toast.LENGTH_LONG).show()
        }
    }
    private fun getCurrentSSID(): String? {
        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return info.ssid
    }
    private fun getStickyEarInNetwork(queue: RequestQueue){
        println("Sniffing")
        val cm = context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val wm = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wm.connectionInfo
        val ipAddress = connectionInfo.ipAddress
        val ipString = Formatter.formatIpAddress(ipAddress)
        val prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)


        for (i in 1..255){
            val testIp = prefix + i.toString()
            val url = "http://$testIp:5000/getData"

            val request = JsonObjectRequest(url, {
                    response -> println(response)
                     val id:String= response.get("id").toString()
                    val color:String= response.get("color").toString()
                println(id +  " " + color)
                val wifi_info: SharedPreferences? =context?.getSharedPreferences("color", Context.MODE_PRIVATE)
                val editor= wifi_info?.edit()


//                if(wifi_info?.contains(id) == false){
                    editor?.apply{

                        putString(id,color)
                        commit()


//                    }
                    println(id + " " + testIp)
                    val editor2 = ip_info?.edit()
                    editor2?.apply{
                        putString(id, testIp)
                        commit()
                    }



                }





            }, {
                error -> println(error)
            })
            queue.add(request)
        }
    }

}

