package com.example.lying.myword;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewWordsBookFragment extends Fragment implements View.OnClickListener{

    //Context 对象
    Context context;

    //用于搜索时暂存显示的数据源
    List<Word>temporarySaveData = null;

    //控件 (编辑、生词本标题、搜索)
    TextView newWordEdit,newWordsBookText,newWordSearch;
    //控件 (全选、取消、删除)
    TextView newWordSelect,newWordsCannel,newWordDelete;
    //控件 (输入)
    EditText newWordsInput;
    //控件(RecyclerView、没有生词时显示的文本)
    RecyclerView newWordRecyclerView;
    TextView noNewWords;

    //RecyclerView 的设配器
    NewWordsBookReclerViewAdapt adapter;

    public NewWordsBookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View NewWordsBookView=inflater.inflate(R.layout.newwordsbook,container,false);

        //绑定控件
        newWordEdit = (TextView)NewWordsBookView.findViewById(R.id.newWordEdit);//编辑
        newWordsBookText = (TextView)NewWordsBookView.findViewById(R.id.newWordsBookText);//生词本标题
        newWordSearch = (TextView)NewWordsBookView.findViewById(R.id.newWordSearch);//搜索
        newWordSelect = (TextView)NewWordsBookView.findViewById(R.id.newWordSelect);//全选
        newWordsCannel = (TextView)NewWordsBookView.findViewById(R.id.newWordsCannel);//取消
        newWordDelete = (TextView)NewWordsBookView.findViewById(R.id.newWordDelete);//删除
        newWordsInput = (EditText)NewWordsBookView.findViewById(R.id.newWordsInput);//输入
        newWordRecyclerView = (RecyclerView)NewWordsBookView.findViewById(R.id.newWordRecyclerView);//RecyclerView
        noNewWords = (TextView)NewWordsBookView.findViewById(R.id.noNewWords);//没有生词时显示的文本

        //先设置提示文本不可见，后面再判断
        noNewWords.setVisibility(View.GONE);

        //对RecyclerView控件的操作
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        //设置为垂直布局，这也是默认的设置
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        newWordRecyclerView.setLayoutManager(layoutManager);
        //给RecycleView设置设配器
        adapter = new NewWordsBookReclerViewAdapt(context);
        newWordRecyclerView.setAdapter(adapter);

        //如果没有生词 则 显示提示文本
        if(adapter.getWordData().size() == 0){
            noNewWords.setVisibility(View.VISIBLE);
        }

        //控件操作
        newWordEdit.setOnClickListener(this);//编辑
        newWordSelect.setOnClickListener(this);//全选 全不选
        newWordsCannel.setOnClickListener(this);//取消
        newWordDelete.setOnClickListener(this);//删除
        newWordSearch.setOnClickListener(this);//搜索
        newWordsInput.addTextChangedListener(new myTextChangedListener());//输入

        return NewWordsBookView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newWordEdit://编辑
                //1.点击后标题栏控件变化
                setVisibleToGone();
                newWordSelect.setVisibility(View.VISIBLE);//全选 全不选 可见
                newWordsCannel.setVisibility(View.VISIBLE);//取消 可见
                newWordDelete.setVisibility(View.VISIBLE);//删除 可见
                //RecyclerView中的图标变化
                adapter.setWhichPicture(1);
                adapter.setIsSelect(0);
                adapter.notifyDataSetChanged();
                break;
            case R.id.newWordSelect://全选 全不选
                if(newWordSelect.getText().equals("全选")){
                    newWordSelect.setText("全不选");
                    //RecyclerView中的图标变化
                    adapter.setWhichPicture(1);
                    adapter.setIsSelect(1);
                    adapter.notifyDataSetChanged();
                }else if(newWordSelect.getText().equals("全不选")){
                    newWordSelect.setText("全选");
                    //RecyclerView中的图标变化
                    adapter.setWhichPicture(1);
                    adapter.setIsSelect(0);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.newWordsCannel://取消
                newWordSelect.setText("全选");
                setVisibleToGone();
                newWordEdit.setVisibility(View.VISIBLE);
                newWordsBookText.setVisibility(View.VISIBLE);
                newWordSearch.setVisibility(View.VISIBLE);
                //RecyclerView中的图标变化
                adapter.setWhichPicture(0);
                adapter.setIsSelect(0);
                adapter.notifyDataSetChanged();
                break;
            case R.id.newWordDelete://删除
                int[] selectPosition = adapter.getSelectItemPosition();
                adapter.deleteData(selectPosition);
                adapter.notifyDataSetChanged();
                if(adapter.getWordData().size() == 0){
                    noNewWords.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.newWordSearch://搜索
                if(newWordSearch.getText().equals("搜索")){
                    //保存数据源
                    temporarySaveData = adapter.getWordData();
                    setVisibleToGone();
                    newWordsInput.setVisibility(View.VISIBLE);
                    newWordSearch.setVisibility(View.VISIBLE);
                    newWordSearch.setText("取消");
                    //输入框自动获取焦点
                    newWordsInput.setFocusable(true);
                    newWordsInput.setFocusableInTouchMode(true);
                    newWordsInput.requestFocus();
                    //弹出软键盘
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }else if(newWordSearch.getText().equals("取消")){
                    //清空输入(若不清空数据显示出错。因为：不管输入框是否时gone状态，EditText的addTextChangedListener监听仍会执行)
                    newWordsInput.setText("");
                    //恢复数据源
                    if(!temporarySaveData.isEmpty()){
                        adapter.setWordData(temporarySaveData);
                        adapter.notifyDataSetChanged();
                    }
                    //若输入法弹出，则要隐藏
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newWordsInput.getWindowToken(), 0);
                    setVisibleToGone();
                    newWordEdit.setVisibility(View.VISIBLE);
                    newWordsBookText.setVisibility(View.VISIBLE);
                    newWordSearch.setVisibility(View.VISIBLE);
                    newWordSearch.setText("搜索");
                }
                break;
        }
    }

    //设置标题栏所有控件为不可见
    private void setVisibleToGone(){
        newWordEdit.setVisibility(View.GONE);//编辑
        newWordsBookText.setVisibility(View.GONE);//生词本标题
        newWordSearch.setVisibility(View.GONE);//搜索
        newWordSelect.setVisibility(View.GONE);//全选
        newWordsCannel.setVisibility(View.GONE);//取消
        newWordDelete.setVisibility(View.GONE);//删除
        newWordsInput.setVisibility(View.GONE);//输入
    }

    private class myTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //获取输入
            String input = newWordsInput.getText().toString().trim();
            //总数据源
            List<Word>totalData = null;
            if(temporarySaveData != null && !temporarySaveData.isEmpty()){
                totalData = temporarySaveData;

                //根据输入应显示的数据
                List<Word> displayData = new ArrayList<Word>();
                if(null != input && !"".equals(input)){
                    int input_lenght = input.length();
                    //获取匹配的字串
                    for(int i =0;i<totalData.size();i++){
                        String word = totalData.get(i).getWordSpell();
                        if(word.length() > input_lenght){
                            if(word.substring(0,input_lenght).equals(input)){
                                displayData.add(totalData.get(i));
                            }
                        }
                        if(word.length() == input_lenght){
                            if(word.equals(input)){
                                displayData.add(totalData.get(i));
                            }
                        }
                    }
                }else if("".equals(input)){
                    displayData = temporarySaveData;
                }
                //设置显示的数据源
                adapter.setWordData(displayData);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
