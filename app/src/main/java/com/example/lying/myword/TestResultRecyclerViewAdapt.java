package com.example.lying.myword;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TestResultRecyclerViewAdapt extends RecyclerView.Adapter<TestResultRecyclerViewAdapt.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView wordLeft,stateMiddle,inputRight;

        public ViewHolder(View itemView) {
            super(itemView);
            wordLeft = (TextView)itemView.findViewById(R.id.TestResultRecyclerItemWordSpell);
            stateMiddle = (TextView)itemView.findViewById(R.id.TestResultRecyclerItemState);
            inputRight = (TextView)itemView.findViewById(R.id.TestResultRecyclerItemInput);
        }
    }

    //数据源
    private List<Word> WordData;

    //构造函数
    public TestResultRecyclerViewAdapt(List<Word>WordData){
        if(WordData == null){
            this.WordData = new ArrayList<>();
        }else{
            this.WordData = WordData;
        }
    }

    @Override
    public TestResultRecyclerViewAdapt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.testresult_recyclerview_item,parent,false);
        return new TestResultRecyclerViewAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TestResultRecyclerViewAdapt.ViewHolder holder, int position) {
        holder.wordLeft.setText(WordData.get(position).getWordSpell());
        if(WordData.get(position).getIsTestRight() == -1){
            holder.stateMiddle.setText("未测试");
        }else if(WordData.get(position).getIsTestRight() == 0){
            holder.stateMiddle.setText("拼写错误");
        }else if(WordData.get(position).getIsTestRight() == 1){
            holder.stateMiddle.setText("拼写正确");
        }
        holder.inputRight.setText(WordData.get(position).getMyInputWordSpell());
    }

    @Override
    public int getItemCount() {
        return WordData.size();
    }
}
