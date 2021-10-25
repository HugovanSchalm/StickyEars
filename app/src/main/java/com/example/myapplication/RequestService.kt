package com.example.myapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.util.*

class RequestService : Service() {
    private val CHANNEL_ID = "sticky_ears_channel_01"
    private val notificationId = 101
    private lateinit var queue : RequestQueue
    private lateinit var task : TimerTask
    private lateinit var timer : Timer
    private lateinit var context: Context
    private lateinit var ipSP : SharedPreferences
    private lateinit var colorSP : SharedPreferences
    private lateinit var notSP : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            startMyOwnForeground()
        } else {
            startForeground(1, Notification())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        context = this
        queue = Volley.newRequestQueue(context)
        timer = Timer()
        notSP = context.getSharedPreferences("noti", Context.MODE_PRIVATE)
        createNotificationChannel()
        task = object : TimerTask() {
            override fun run() {
                ipSP = context?.getSharedPreferences("ip", Context.MODE_PRIVATE)
                println(ipSP.all)
                if(getCurrentSSID().equals("\"StickyEar\"")){
                    val ip = "10.0.0.5"
                    val request = JsonObjectRequest("http://$ip:5000/getData", {
                        response ->
                        println(response)
                        sendRequestToStickyEar(ip, response.get("color").toString(), response.get("id").toString())
                    }, {
                        error -> println(error)
                    })
                    queue.add(request)
                }
                for(entry in ipSP.all.entries){
                    println(entry.key + " " + entry.value.toString())
                    ipSP = context?.getSharedPreferences("ip", Context.MODE_PRIVATE)
                    colorSP = context?.getSharedPreferences("color", Context.MODE_PRIVATE)
                    sendRequestToStickyEar(entry.value.toString(), colorSP.getString(entry.key, "Blue")!!, entry.key)
                }
            }
        }
        timer.scheduleAtFixedRate(task, 0, 5000)
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()

        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice")
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground(){
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        val notification = builder
            .setOngoing(true)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }


    private fun sendRequestToStickyEar(ip: String, color: String, id: String){
        val url = "http://$ip:5000/checkSound"



        val request = StringRequest(
            Request.Method.GET, url,
            {
                    response -> println("Response is: $response")
                if(response == "yes"){
                    sendNotification("Sticky Ear heard a sound!", "Your $color sticky ear heard a sound!")
                    val notification = TimelineNotification(
                        "Heard a sound",
                        "Your $color Sticky Ear heard a sound!",
                        Calendar.getInstance().time.toString(),
                        id,
                        "$color Sticky Ear"
                    )
                    val gson = Gson()
                    val json = gson.toJson(notification)
                    val size = notSP.all.size
                    val editor = notSP.edit()
                    editor.apply{
                        putString(size.toString(), json)
                        commit()
                    }
                }
            },
            { error -> println("Whoops, request went wrong: ${error.toString()}") }
        )

        queue.add(request)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Sticky Ears"
            val descriptionText = "Notification channel for the Sticky Ears app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String = "Example Title", description: String = "Example Description") {

        val intent = Intent(context, RequestService::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        //val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ear)


        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            //.setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    private fun getCurrentSSID(): String? {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return info.ssid
    }
}