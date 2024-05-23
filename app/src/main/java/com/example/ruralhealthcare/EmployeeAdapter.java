package com.example.ruralhealthcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ItemHolder> {

    private ArrayList<UserRole> userRoles;

    private Context context;

    onClickListener onClickListener;

    public void OnClickListener(onClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public interface onClickListener{
        View.OnClickListener onClick(UserRole userRole);
    }

    public EmployeeAdapter(Context context, ArrayList<UserRole> userRoles){
        this.userRoles = userRoles;
        this.context = context;
    }

    @NonNull
    @Override
    public EmployeeAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctorlist,parent,false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ItemHolder holder, int position) {
        UserRole userRole = userRoles.get(position);
        holder.itemView.setOnClickListener(onClickListener.onClick(userRoles.get(position)));
        holder.onBind(userRole);
    }

    @Override
    public int getItemCount() {
        return userRoles.size();
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {

        ImageView imageview;
        TextView name,position;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            position = itemView.findViewById(R.id.position);

            imageview = itemView.findViewById(R.id.imageView);

        }
        public void onBind(UserRole userRole){
            name.setText(userRole.getUsername());
            position.setText(userRole.getRole());
            Glide.with(itemView.getContext()).load(userRole.getRole()).error(R.drawable.logo).into(imageview);
        }
    }
}

