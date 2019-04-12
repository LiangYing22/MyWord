package com.example.lying.myword;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchWordFragment extends Fragment implements View.OnClickListener{

    //Context对象
    Context context;

    //数据库
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;

    //需显示的 单词、音标、中文含义
    String displayWord="";
    String displayPhoneticSymbol="";
    String displayChineseMean="";

    //用于保存当前词本的所有单词(存储Word对象)
    List<Word> wordOfWordsBook = new ArrayList<Word>();
    //用于自动匹配的词本中的所有单词（存储String对象）
    List<String> stringOfWordsBook = new ArrayList<String>();

    //控件(自动匹配输入框)
    AutoCompleteTextView search_input;
    //控件(清除输入)
    ImageView search_clearInput;
    //控件(显示数据的RelativeLayout)
    RelativeLayout search_content;
    //控件(提示、单词、音标、中文含义、加入生词本)
    TextView search_hint,search_word,search_phoneticSymbol,search_chineseMean,search_addToNewWords;

    public SearchWordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getContext();
        //获取数据库数据
        mydatabasehelper = new MyDBHelper(getContext());
        mydatabase = mydatabasehelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View SearchWordView = inflater.inflate(R.layout.searchword,container,false);

        //初始化数据
        initData();

        //绑定控件
        search_input = (AutoCompleteTextView)SearchWordView.findViewById(R.id.search_input);//自动提示输入框
        search_clearInput = (ImageView)SearchWordView.findViewById(R.id.search_clearInput);//清除输入
        search_content = (RelativeLayout)SearchWordView.findViewById(R.id.search_content);//显示数据的RelativeLayout
        search_hint = (TextView)SearchWordView.findViewById(R.id.search_hint);//提示
        search_word = (TextView)SearchWordView.findViewById(R.id.search_word);//单词
        search_phoneticSymbol = (TextView)SearchWordView.findViewById(R.id.search_phoneticSymbol);//音标
        search_chineseMean = (TextView)SearchWordView.findViewById(R.id.search_chineseMean);//中文含义
        search_addToNewWords = (TextView)SearchWordView.findViewById(R.id.search_addToNewWords);//加入生词本

        /**进入此页面就获取焦点，弹出软键盘**/
        //输入框自动获取焦点
        search_input.setFocusable(true);
        search_input.setFocusableInTouchMode(true);
        search_input.requestFocus();
        //弹出软键盘
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        //控件操作
        search_input.addTextChangedListener(new myTextChangedListener());//输入
        search_clearInput.setOnClickListener(this);//清除输入
        if(null != stringOfWordsBook && !stringOfWordsBook.isEmpty()){//若数据源有数据
            //系统自带的设配器，简单方便（但不能设置过滤条件，如：将输入的字符串去除首尾空白）
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, stringOfWordsBook);
            search_input.setAdapter(adapter);
//            //自定义设配器实现（要实现过滤器接口）用上面系统自带的也可以（但不能设置每一项的样式）
//            AutoCompleteTextAdapter autoCompleteTextAdapter = new AutoCompleteTextAdapter(context,stringOfWordsBook);
//            search_input.setAdapter(autoCompleteTextAdapter);
        }
        //单击自动提示的子项时
        search_input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的单词
                String selectWord = adapterView.getAdapter().getItem(i).toString();
                if(selectWord != null && wordOfWordsBook != null){
                    for(int j=0;j<wordOfWordsBook.size();j++){
                        Word word = wordOfWordsBook.get(j);
                        if(word.getWordSpell().equals(selectWord)){
                            //相应控件显示与隐藏，控件显示内容设置
                            search_content.setVisibility(View.VISIBLE);
                            search_hint.setVisibility(View.GONE);
                            search_word.setText(selectWord);
                            search_phoneticSymbol.setText(word.getPhoneticSymbol());
                            search_chineseMean.setText(word.getChineseMean());
                            displayWord=selectWord;
                            displayPhoneticSymbol=word.getPhoneticSymbol();
                            displayChineseMean=word.getChineseMean();
                            //加入生词本控件状态判断
                            //控制“加入生词本”的可点击性和颜色（通过判断单词是否在生词表中）
                            Cursor cursor_newWordsBook = mydatabase.rawQuery("select * from "+MyDBHelper.Table_NewWordsBook_NAME+" where NewWordSpelling=?",new String[]{selectWord});
                            if(cursor_newWordsBook.getCount() == 0){
                                //初始状态
                                search_addToNewWords.setText("加入生词本");
                                search_addToNewWords.setEnabled(true);
                            }else if(cursor_newWordsBook.getCount() > 0){
                                search_addToNewWords.setText("已添加生词");
                                search_addToNewWords.setEnabled(false);
                            }
                            cursor_newWordsBook.close();
                            return;
                        }
                    }
                }
            }
        });
        //单击加入生词本
        search_addToNewWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_addToNewWords.setText("已添加生词");
                search_addToNewWords.setEnabled(false);
                //2.加入数据库
                Cursor cursor_newwords = mydatabase.rawQuery("select * from " + MyDBHelper.Table_NewWordsBook_NAME, null);
                int count_newwords = cursor_newwords.getCount();
                if(count_newwords >= 0){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("NewWordID",count_newwords+1);
                    contentValues.put("NewWordSpelling",displayWord);
                    contentValues.put("NewWordPhoneticSymbol",displayPhoneticSymbol);
                    contentValues.put("NewWordChineseMean",displayChineseMean);
                    mydatabase.insert(MyDBHelper.Table_NewWordsBook_NAME, null, contentValues);
                }
                cursor_newwords.close();
            }
        });

        return SearchWordView;
    }

    //初始化数据
    private void initData(){
        wordOfWordsBook.clear();
        stringOfWordsBook.clear();
        Cursor cursor_word = mydatabase.rawQuery("select WordSpelling,WordChineseMean,WordPhoneticSymbol from "
                +MyDBHelper.Table_Words_NANE,null);
        int count_word = cursor_word.getCount();
        if(count_word > 0){
            while(cursor_word.moveToNext()){
                Word word = new Word(cursor_word.getString(0),cursor_word.getString(2),cursor_word.getString(1));
                wordOfWordsBook.add(word);
                stringOfWordsBook.add(cursor_word.getString(0));
            }
        }
        cursor_word.close();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mydatabase.close();
        mydatabasehelper.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_clearInput:
                search_input.setText("");
                break;
        }
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
            String input = search_input.getText().toString().trim();
            if(null != input && !input.equals("")){
                search_clearInput.setVisibility(View.VISIBLE);
            }else if(null == input || input.equals("")){
                search_clearInput.setVisibility(View.GONE);
            }

        }
    }

}
