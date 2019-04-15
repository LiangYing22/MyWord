package com.example.lying.myword;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OneTextViewRecyclerViewAdapt extends RecyclerView.Adapter<OneTextViewRecyclerViewAdapt.ViewHolder>  {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView popItemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            popItemTextView = (TextView)itemView.findViewById(R.id.popItemText);
        }
    }

    interface Listener{
        public void onClickListener(int position);
    }

    //数据源
    List<String> ModuleNameList;
    //监听器
    Listener listener;

    public OneTextViewRecyclerViewAdapt(List<String> ModuleNameList){
        if(ModuleNameList == null){
            this.ModuleNameList = new ArrayList<>();
        }
        this.ModuleNameList = ModuleNameList;
    }

    //设置监听器
    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public OneTextViewRecyclerViewAdapt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popwindow_recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OneTextViewRecyclerViewAdapt.ViewHolder holder, final int position) {
        holder.popItemTextView.setText(ModuleNameList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ModuleNameList.size();
    }
}
