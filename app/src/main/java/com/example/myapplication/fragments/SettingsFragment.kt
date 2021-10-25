package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import org.w3c.dom.Text
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment() : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     var counter=0
    var btn1 =false
    var btn2= false
    var btn3= false

    lateinit var sauce:SharedPreferences
    lateinit var bundle: Bundle
    lateinit var id: String


    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = this.requireArguments()
        if(bundle != null){
            id = bundle.getString("id", "None")
        }
    }

    fun add_pref(sk: SharedPreferences){
        sauce=sk
    }

    fun set_text(radio_btn:RadioButton){
        if(radio_btn.isChecked){
            radio_btn.text="on"
        }
        else{
            radio_btn.text="off"
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ):View{
        // Inflate the layout for this fragment
        counter++

        sauce = context?.getSharedPreferences("sett", Context.MODE_PRIVATE)!!

        val view:View=inflater.inflate(R.layout.fragment_settings, container, false)

        val radio_bn1= view.findViewById<RadioButton>(R.id.radio_group1)
        val radio_bn2= view.findViewById<RadioButton>(R.id.radio_group2)
        val radio_bn3= view.findViewById<RadioButton>(R.id.radio_group3)
        val text = view.findViewById<TextView>(R.id.textView2)
        text.text = id

        val editor= sauce.edit()

        //loading data
//        radio_bn1.isChecked=sauce.getBoolean("state1",false)
//        radio_bn2.isChecked=sauce.getBoolean("state2",false)
//        radio_bn3.isChecked=sauce.getBoolean("state3",false)

        set_text(radio_bn1)
        set_text(radio_bn2)
        set_text(radio_bn3)

        when(sauce.getInt(id, 0)){
            0 -> {
                radio_bn1.isChecked = true
                radio_bn1.text = "on"
                radio_bn2.isChecked = false
                radio_bn2.text = "off"
                radio_bn3.isChecked = false
                radio_bn3.text = "off"
            }
            1 -> {
                radio_bn1.isChecked = false
                radio_bn1.text = "off"
                radio_bn2.isChecked = true
                radio_bn2.text = "on"
                radio_bn3.isChecked = false
                radio_bn3.text = "off"
            }
            2 -> {
                radio_bn1.isChecked = false
                radio_bn1.text = "off"
                radio_bn2.isChecked = false
                radio_bn2.text = "off"
                radio_bn3.isChecked = true
                radio_bn3.text = "on"
            }
            else -> {
                radio_bn1.isChecked = true
                radio_bn1.text = "on"
                radio_bn2.isChecked = false
                radio_bn2.text = "off"
                radio_bn3.isChecked = false
                radio_bn3.text = "off"
            }
        }


        radio_bn1.setOnCheckedChangeListener { _, isChecked ->editor.apply{
            //putBoolean("state1",isChecked)
            if(isChecked){
                putInt(id, 0)
                commit()
            }
        }
            if(!isChecked){
                radio_bn1.text ="off"
            }
            else{
                radio_bn1.text="on"

                }
        }

        radio_bn2.setOnCheckedChangeListener { _, isChecked ->editor.apply{
            //putBoolean("state2",isChecked)
            if(isChecked){
                putInt(id, 1)
                commit()
            }
            //Toast.makeText(context,"Only Notification ON",Toast.LENGTH_SHORT).show()
        }
            if(!isChecked){
                radio_bn2.text="off"
            }
            else{
                radio_bn2.text="on"
                }
        }
        radio_bn3.setOnCheckedChangeListener { _, isChecked ->editor.apply{

            if(isChecked){
                putInt(id, 2)
                commit()
            }
            //Toast.makeText(context,"Only Notification ON",Toast.LENGTH_SHORT).show()
        }
            if(!isChecked){
                radio_bn3.text="off"

            }
            else{
                radio_bn3.text="on"

            }
        }

        return view


        }






}