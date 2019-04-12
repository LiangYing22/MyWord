package com.example.lying.myword.util;

import android.content.Context;
import android.widget.Toast;

public class MyToast {
    public static void getToast(Context context,String showString){
        Toast toast = Toast.makeText(context,null,Toast.LENGTH_SHORT);
        toast.setText(showString);
        toast.show();
    }
}
