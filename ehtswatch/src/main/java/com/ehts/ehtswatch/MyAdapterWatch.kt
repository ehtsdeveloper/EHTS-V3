package com.ehts.ehtswatch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide

class MyAdapterWatch(private val context: Context, private val dataList: List<ProfileData>) :
    RecyclerView.Adapter<MyViewHolderWatch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderWatch {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item, parent, false)
        return MyViewHolderWatch(view)
    }

    override fun onBindViewHolder(holder: MyViewHolderWatch, position: Int) {
        val data = dataList[position]
     //   Glide.with(context).load(data.dataImage).into(holder.recImage)
        holder.recName.text = data.dataName
        holder.recEmpID.text = data.dataEmpID
        holder.recDeviceID.text = data.dataEmpID
      //  val age = data.dataAge.toString()
        //val height = data.dataHeight.toString()
        //val weight = data.dataWeight.toString()

      //  holder.recAge.text = age
      //  holder.recHeight.text = height
       // holder.recWeight.text = weight

        holder.recCard.setOnClickListener {
            val intent = Intent(context, EmployeeRecord::class.java).apply {
             //   putExtra("images/", data.dataImage)
                putExtra("Employee Name", data.dataName)
                putExtra("Employee ID", data.dataEmpID)
              //  putExtra("Age", age)
             //   putExtra("Height (in)", height)
             //   putExtra("Weight (lb)", weight)
                putExtra("Device ID", data.dataDeviceID)
             //   putExtra("Key", data.key)
            }
            context.startActivity(intent)
        }
/*
        holder.goToBtn.setOnClickListener {
            holder.recCard.performClick()
        }

 */
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

class MyViewHolderWatch(itemView: View) : RecyclerView.ViewHolder(itemView) {
  //  val recImage: ImageView = itemView.findViewById(R.id.recImage)
    val recName: TextView = itemView.findViewById(R.id.recName)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
    val recEmpID: TextView = itemView.findViewById(R.id.recEmpID)
    //val recAge: TextView = itemView.findViewById(R.id.recage)
   // val recHeight: TextView = itemView.findViewById(R.id.recHeight)
   // val recWeight: TextView = itemView.findViewById(R.id.recWeight)
 val recDeviceID: TextView = itemView.findViewById(R.id.recDeviceID)
  //  val goToBtn: ImageButton = itemView.findViewById(R.id.recViewProfile)
}
