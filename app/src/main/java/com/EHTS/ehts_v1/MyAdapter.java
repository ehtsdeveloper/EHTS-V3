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

import java.util.ArrayList;
import java.util.List;
//This class is to fill the recycler view with employee cards

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
                intent.putExtra("images/", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Employee Name", dataList.get(holder.getAdapterPosition()).getDataName());
                intent.putExtra("Employee ID", dataList.get(holder.getAdapterPosition()).getDataEmpID());
                intent.putExtra("Age", age);
                intent.putExtra("Height (in)", height);
                intent.putExtra("Weight (lb)", weight);
                intent.putExtra("Gender", dataList.get(holder.getAdapterPosition()).getDatagender());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());


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


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<Profile_Data> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView recImage;
    TextView recName, recEmpID, recAge, recheight, recweight, genderData;;
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
        genderData = itemView.findViewById(R.id.genderData);

        goTobtn = itemView.findViewById(R.id.recViewProfile);
    }

}