package com.example.myapplication

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.NotificationFragment
import com.example.myapplication.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private val home= HomeFragment()
    private val notifications= NotificationFragment()






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!isMyServiceRunning(RequestService::class.java)){
            Intent(this, RequestService::class.java).also {
                    intent->startService(intent)
            }
        }
        val sharedPref: SharedPreferences =getSharedPreferences("sett", Context.MODE_PRIVATE)


        val bottomNavigationView: BottomNavigationView=findViewById(R.id.bottom_navigation)
        //settings.add_pref(sharedPref)

        replaceFragment(home)

       notifications.add_not(bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.nav_home->replaceFragment(home)
                R.id.nav_notification->replaceFragment(notifications)
                //R.id.nav_settings->replaceFragment(settings)


            }
            true

        }










    }

    private fun replaceFragment(fragment: Fragment){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)


        transaction.commit()

    }

    override fun onDestroy() {
        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice")
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
    private fun isMyServiceRunning(serviceClass : Class<*>) : Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }
}