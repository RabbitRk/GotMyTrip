package com.rabbitt.gotmytrip;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.gotmytrip.DBhelper.recycleAdapter;

import java.util.List;


public class yourRidesAdapter extends RecyclerView.Adapter<yourRidesAdapter.holder> {

    private List<recycleAdapter> dataModelArrayList;

    yourRidesAdapter(List<recycleAdapter> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.package_list, null);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {

        recycleAdapter dataModel = dataModelArrayList.get(i);

        holder.book_id.setText(dataModel.getBook_id());
        holder.v_type.setText(dataModel.getV_type());
        holder.travel_type.setText(dataModel.getTravel_type());
        holder.ori.setText(dataModel.getOrigin());
        holder.dest.setText(dataModel.getDestination());
        holder.dateof.setText(dataModel.getTimeat());

        Log.i("MaluRk", "onBindViewHolder: "+dataModel.getOrigin());
        Log.i("MaluRk", "onBindViewHolder: "+dataModel.getBook_id());
        Log.i("MaluRk", "onBindViewHolder: "+dataModel.getTimeat());
        Log.i("MaluRk", "onBindViewHolder: "+dataModel.getOrigin());
        Log.i("MaluRk", "onBindViewHolder: "+dataModel.getDestination());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class holder extends RecyclerView.ViewHolder {

        TextView book_id, travel_type, v_type, dateof, ori, dest;

        holder(@NonNull View itemView) {
            super(itemView);

            book_id = itemView.findViewById(R.id.book_id);
            travel_type = itemView.findViewById(R.id.travel_type);
            v_type = itemView.findViewById(R.id.v_type);
            dateof = itemView.findViewById(R.id.dateof);
            ori = itemView.findViewById(R.id.ori);
            dest = itemView.findViewById(R.id.dest);
        }
    }
}