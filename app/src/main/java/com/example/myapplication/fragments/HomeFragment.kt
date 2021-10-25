package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.myapplication.R
import android.content.pm.PackageManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.StickyEar
import com.example.myapplication.StickyEarAdapter
import kotlinx.android.synthetic.main.fragment_home.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val wifi= wifisettings()

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

        val view:View= inflater.inflate(R.layout.fragment_home, container, false)
        val btn_setting =view.findViewById<Button>(R.id.open_settings)

        val adapter = StickyEarAdapter(mutableListOf(), context, parentFragmentManager)


        val rec = view.findViewById<RecyclerView>(R.id.rec)
        rec.adapter = adapter
        rec.layoutManager = LinearLayoutManager(context)

        btn_setting.setOnClickListener{
            //startActivityForResult( Intent(Settings.ACTION_WIFI_SETTINGS));
            ///openWifiSettings()
            replaceFragment(wifi)



        }

        // Inflate the layout for this fragment
        return  view
    }

    private fun startActivityForResult(any: Any) {

    }




    private fun replaceFragment(fragment: Fragment){
        val transaction=getParentFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)


        transaction.commit()

    }




}