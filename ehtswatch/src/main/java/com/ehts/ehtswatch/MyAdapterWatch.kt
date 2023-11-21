package com.ehts.ehtswatch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView



class MyAdapterWatch(private val context: Context, private val dataList: List<ProfileData>) :
    RecyclerView.Adapter<MyViewHolderWatch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderWatch {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item, parent, false)
        return MyViewHolderWatch(view)
    }

    override fun onBindViewHolder(holder: MyViewHolderWatch, position: Int) {
        val data = dataList[position]

        holder.recName.text = data.dataName
        holder.recEmpID.text = data.dataEmpID
        holder.recgender.text = data.dataEmpID


        holder.recCard.setOnClickListener {


            val intent = Intent(context, HeartRate::class.java).apply {
                putExtra("Employee ID", data.dataEmpID)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

class MyViewHolderWatch(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recName: TextView = itemView.findViewById(R.id.recName)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
    val recEmpID: TextView = itemView.findViewById(R.id.recEmpID)

    val recgender: TextView = itemView.findViewById(R.id.recgender)



}
