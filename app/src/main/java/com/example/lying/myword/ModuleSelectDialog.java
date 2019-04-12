package com.example.lying.myword;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ModuleSelectDialog extends Dialog{

    Context context;
    RadioGroup.OnCheckedChangeListener checkListener;
    View.OnClickListener cancelListener;
    View.OnClickListener confirmListener;

    public ModuleSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ModuleSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected ModuleSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.option_dialog, null);
        setContentView(view);
        RadioGroup rg = (RadioGroup)view.findViewById(R.id.MoudleSelectRadioGroup);
//        RadioButton rbLetter = (RadioButton)view.findViewById(R.id.SelectForLetter);
//        RadioButton rbDisorder = (RadioButton)view.findViewById(R.id.SelectForDisorder);
        Button cancelButton = (Button)view.findViewById(R.id.CancleButton);
        Button confirmButton = (Button)view.findViewById(R.id.ConfirmButton);
        rg.setOnCheckedChangeListener(checkListener);
        cancelButton.setOnClickListener(cancelListener);
        confirmButton.setOnClickListener(confirmListener);
    }

    public void setRadioCheckListener(RadioGroup.OnCheckedChangeListener checkListener){
        this.checkListener = checkListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener){
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener){
        this.confirmListener = confirmListener;
    }

}
