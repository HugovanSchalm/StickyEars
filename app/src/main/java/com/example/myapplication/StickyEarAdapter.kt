package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.fragments.SettingsFragment
import kotlinx.android.synthetic.main.list_stickyear.view.*

class StickyEarAdapter(
    private val stickyears: MutableList<StickyEar>,
    context: Context?,
    manager: FragmentManager,
) : RecyclerView.Adapter<StickyEarAdapter.TodoViewHolder>() {
    val manager = manager
    private val wifi_info: SharedPreferences =context!!.getSharedPreferences("color", Context.MODE_PRIVATE) as SharedPreferences
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        for(entry in wifi_info.all.entries ) {
            println(entry)
            stickyears.add(StickyEar(entry.key, entry.value.toString()))
        }
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_stickyear,             parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curSE = stickyears[position]
        holder.itemView.apply {
            this.setOnClickListener {
                val fragment = SettingsFragment()
                val bundle = Bundle()
                bundle.putString("id", curSE.id)
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
            tvStickyEarName.text = "${curSE.color} Sticky Ear (Tap to edit)"
            tvStickyEarID.text = "ID: ${curSE.id}"
        }
    }
    override fun getItemCount(): Int {
        return stickyears.size
    }



    private fun replaceFragment(fragment: Fragment){
        val transaction= manager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()

    }
}