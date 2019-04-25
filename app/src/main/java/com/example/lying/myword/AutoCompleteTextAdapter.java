package com.example.lying.myword;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteTextAdapter extends BaseAdapter implements Filterable {

    Context context;

    private ArrayFilter mFilter;//数据过滤器
    final private List<String> mList;//传进来的数据
    private List<String> filterList;//过滤后的数据

    //构造函数
    public AutoCompleteTextAdapter(Context context,List<String>mList){
        this.context = context;
        if(mList == null){
            this.mList = new ArrayList<String>();
        }else{
            this.mList = mList;
        }
    }

    @Override
    public int getCount() {
        return filterList == null ? 0 : filterList.size();
    }

    @Override
    public Object getItem(int i) {
        return filterList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1;
        ViewHolder viewHolder;
        if(view == null){
            view1 = View.inflate(context, R.layout.auto_item, null);
            viewHolder = new ViewHolder();
            viewHolder.word = (TextView)view1.findViewById(R.id.auto_word);
            view1.setTag(viewHolder);
        }else{
            view1 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.word.setText(filterList.get(i));

        return view1;
    }

    @Override
    public Filter getFilter() {
        //创建过滤器的对象
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    class ViewHolder {
        public TextView word;
    }

    class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //过滤后的结果
            FilterResults resultsWord = new FilterResults();
            //若没有输入内容则不过滤
            if(charSequence == null || charSequence.length() == 0){
                resultsWord.values = Transform(mList);
                resultsWord.count = mList.size();
            }else {
                if(filterList != null){
                    filterList.clear();
                }
                filterList = new ArrayList<String>();
                //过滤的条件
                String input = charSequence.toString().trim();
                for(int i=0;i<mList.size();i++){
                    if(mList.get(i) != null){
                        if(mList.get(i).startsWith(input)){
                            filterList.add(mList.get(i));
                        }
                    }
                }
                resultsWord.values = filterList;
                resultsWord.count = filterList.size();
            }
            return resultsWord;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filterList = (List<String>)filterResults.values;
            if (filterResults.count > 0) {
                //重绘当前可见区域
                Log.i("AutoCompleteTextAdapter","数目："+filterResults.count);
                notifyDataSetChanged();
            } else {
                //重绘控件，还原到初始状态
                notifyDataSetInvalidated();
            }
        }

        //解决Bug。Bug：直接通过List对象赋值(List a=b)，导致修改(如，清空操作)被赋值的对象(a),另一个对象(b)也被操作(如，被清空)
        private List<String> Transform (List<String> list){
            int size = list.size();
            List<String>newList = new ArrayList<String>();
            for(int i =0;i<size;i++){
                newList.add(list.get(i));
            }
            return newList;
        }

    }

//    //返回过滤数据是否为空
//    public boolean isFilterDataEmpty(String input){
//        if(null != input && !input.equals("")){
//            if(filterList != null && filterList.size() == 0){
//                return true;
//            }
//        }
//        return false;
//    }
}
