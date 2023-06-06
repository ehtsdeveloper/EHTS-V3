package com.EHTS.ehts_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
//This class is to fill my recycler view with employee cards

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Profile_Data> dataList;
    public MyAdapter(Context context, List<Profile_Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder,  int position) {

        // Load the employee image using Glide or any other image loading library
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getDataName());
        holder.recEmpID.setText(dataList.get(position).getDataEmpID());

        // Convert integer values to strings
        String age = String.valueOf(dataList.get(position).getDataAge());
        String height = String.valueOf(dataList.get(position).getDataHeight());
        String weight = String.valueOf(dataList.get(position).getDataWeight());

        // Set the converted values to the corresponding TextViews
        holder.recAge.setText(age);
        holder.recheight.setText(height);
        holder.recweight.setText(weight);


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, EmployeeRecord.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Employee Name", dataList.get(holder.getAdapterPosition()).getDataName());
                intent.putExtra("Employee ID", dataList.get(holder.getAdapterPosition()).getDataEmpID());
                intent.putExtra("Age", age);
                intent.putExtra("Height (in)", height);
                intent.putExtra("Weight (lb)", weight);
                intent.putExtra("Device ID", dataList.get(holder.getAdapterPosition()).getDataDeviceID());

                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
               // context.startActivity(intent);
                //intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());


                context.startActivity(intent);
            }
        });

        holder.goTobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle goToBtn click event
                holder.recCard.callOnClick(); // Simulate a click on recCard
            }
        });
}

    /*

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getDataName());
        holder.empIdData.setText(dataList.get(position).getDataEmpID());
        holder.ageData.setText(dataList.get(position).getDataAge());
        holder.heightData.setText(dataList.get(position).getDataHeight());
        holder.weightData.setText(dataList.get(position).getDataWeight());
        holder.deviceIDData.setText(dataList.get(position).getDataDeviceID());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EmployeeRecord.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Employee Name", dataList.get(holder.getAdapterPosition()).getDataEmpID());
                intent.putExtra("Employee ID", dataList.get(holder.getAdapterPosition()).getDataEmpID());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Age", dataList.get(holder.getAdapterPosition()).getDataAge());
                intent.putExtra("Height", dataList.get(holder.getAdapterPosition()).getDataHeight());
                intent.putExtra("Weight", dataList.get(holder.getAdapterPosition()).getDataWeight());
                intent.putExtra("Device ID", dataList.get(holder.getAdapterPosition()).getDataDeviceID());
                context.startActivity(intent);
            }
        });
    }
*/
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView recImage;
    TextView recName, recEmpID, recAge, recheight, recweight, deviceIDData;;
    CardView recCard;
    ImageButton goTobtn;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
        recCard = itemView.findViewById(R.id.recCard);
        recEmpID = itemView.findViewById(R.id.recEmpID);
        recAge = itemView.findViewById(R.id.recage);
        recheight = itemView.findViewById(R.id.recHeight);
        recweight = itemView.findViewById(R.id.recWeight);
        deviceIDData = itemView.findViewById(R.id.deviceIdData);

        goTobtn = itemView.findViewById(R.id.recViewProfile);
    }

    /*
    empIdData, ageData, heightData, weightData, deviceIDData;

    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
        empIdData = itemView.findViewById(R.id.EmpIDData);
        ageData = itemView.findViewById(R.id.AgeData);
        heightData = itemView.findViewById(R.id.heightData);
        weightData = itemView.findViewById(R.id.weightData);
        deviceIDData = itemView.findViewById(R.id.deviceIdData);
    }

     */

}