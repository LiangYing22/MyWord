package com.example.lying.myword;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lying.myword.util.File_read_write;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudySurveyActivityReclerViewAdapt extends RecyclerView.Adapter<StudySurveyActivityReclerViewAdapt.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView survey_module,survey_unLearningTop,survey_needReviewTop,survey_hadMastTop;
        RelativeLayout survey_unLearning,survey_needReview,survey_hadMast;
        public ViewHolder(View view){
            super(view);
            survey_module = (TextView)view.findViewById(R.id.survey_module);
            survey_unLearningTop = (TextView)view.findViewById(R.id.survey_unLearningTop);
            survey_needReviewTop = (TextView)view.findViewById(R.id.survey_needReviewTop);
            survey_hadMastTop = (TextView)view.findViewById(R.id.survey_hadMastTop);
            survey_unLearning = (RelativeLayout)view.findViewById(R.id.survey_unLearning);
            survey_needReview = (RelativeLayout)view.findViewById(R.id.survey_needReview);
            survey_hadMast = (RelativeLayout)view.findViewById(R.id.survey_hadMast);
        }
    }

    public interface SurveyClickListener{
        void unLearningClickListener(String ModuleName,List<Word>unlearning);
        void reviewClickListener(String ModuleName,List<Word>review);
        void masterClickListener(String ModuleName,List<Word>master);
    }

    public void setListener(SurveyClickListener surveyClickListener){
        this.surveyClickListener = surveyClickListener;
    }

    //Context对象
    private Context context;
    private List<Survey> surveyData = new ArrayList<>();
    private SurveyClickListener surveyClickListener;

    //数据源（全部的学习txt文件）
    public void initData(){
        //file目录
        File directory = context.getFilesDir();
        if (directory != null && directory.exists() && directory.isDirectory()){
            for (File item : directory.listFiles()) {
                if(item.getName().contains(".txt") && !item.getName().contains("review")){
                    File_read_write file_read_write = new File_read_write(context,item.getName());
                    surveyData.add(file_read_write.getSurvey());
                }
            }
        }
    }

    //构造函数
    public StudySurveyActivityReclerViewAdapt(Context context){
            this.context = context;
            initData();
    }

    @Override
    public StudySurveyActivityReclerViewAdapt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.studysurvey_recyclerview_item,parent,false);
        return new StudySurveyActivityReclerViewAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StudySurveyActivityReclerViewAdapt.ViewHolder holder, final int position) {
        holder.survey_module.setText("'"+surveyData.get(position).getModuleName()+"'"+"模块学习概况");
        holder.survey_unLearningTop.setText(surveyData.get(position).getUnLearningNum()+"");
        holder.survey_needReviewTop.setText(surveyData.get(position).getReviewingNum()+"");
        holder.survey_hadMastTop.setText(surveyData.get(position).getMasterNum()+"");
        holder.survey_unLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surveyClickListener != null){
                    surveyClickListener.unLearningClickListener(surveyData.get(position).getModuleName(),surveyData.get(position).getUnLearning());
                }
            }
        });
        holder.survey_needReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surveyClickListener != null){
                    surveyClickListener.reviewClickListener(surveyData.get(position).getModuleName(),surveyData.get(position).getReviewing());
                }
            }
        });
        holder.survey_hadMast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surveyClickListener != null){
                    surveyClickListener.masterClickListener(surveyData.get(position).getModuleName(),surveyData.get(position).getMaster());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return surveyData.size();
    }
}
