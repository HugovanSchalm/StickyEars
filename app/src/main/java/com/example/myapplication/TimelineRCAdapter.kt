package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_timeline.view.*

class TimelineRCAdapter(
    private val notifications: MutableList<TimelineNotification>,
    context: Context?
) : RecyclerView.Adapter<TimelineRCAdapter.TodoViewHolder>() {
    private val notSP = context?.getSharedPreferences("noti", Context.MODE_PRIVATE)
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        for(entry in notSP!!.all.entries){
            println("entry: " + entry)
            val gson = Gson()
            val entDC = gson.fromJson(entry.value.toString(), TimelineNotification::class.java)
            notifications.add(entDC)
        }
                return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_timeline,             parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        println("ayayaya: " + notSP!!.getString("0", "bruh"))
        println(notSP!!.all.entries)
        val curNot = notifications[position]
        holder.itemView.apply {
            tvColor.text = curNot.stickyEarColor
            tvID.text = curNot.stickyEarId
            tvTime.text = curNot.time
            tvTitle.text = curNot.title
            tvDescription.text = curNot.description
        }
    }
    override fun getItemCount(): Int {
        return notifications.size
    }
}