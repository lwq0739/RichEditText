package com.lwq.richedittext;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by lwq on 2017/6/17.
 */

public class UrlDialog extends Dialog {
    private TextView tv_title;
    private EditText et_name;
    private EditText et_address;
    private Button btn_confirm;
    private Button btn_canel;
    private final String URL_TYPE="url";
    private final String TEL_TYPE="tel";

    private ConfirmListener mConfirmListener;
    public UrlDialog(@NonNull Context context,String type,ConfirmListener confirmListener) {
        super(context);
        initView(type);
        mConfirmListener=confirmListener;
        setListener();
    }

    private void initView(String type) {
        View view = View.inflate(getContext(),R.layout.dialog_url,null);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        et_name= (EditText) view.findViewById(R.id.et_name);
        et_address= (EditText) view.findViewById(R.id.et_address);
        btn_confirm= (Button) view.findViewById(R.id.btn_confirm);
        btn_canel= (Button) view.findViewById(R.id.btn_canel);
        switch (type) {
            case URL_TYPE:
                tv_title.setText("插入网站地址");
                et_address.setText("http://");
                et_address.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                break;
            case TEL_TYPE:
                tv_title.setText("插入手机号码");
                et_address.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }
        setContentView(view);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
            lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
            dialogWindow.setAttributes(lp);
        }
    }

    private void setListener(){
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String address = et_address.getText().toString();
                if (TextUtils.isEmpty(name)||TextUtils.isEmpty(address)){
                    return;
                }else {
                    mConfirmListener.onConfirm(name,address);
                    UrlDialog.this.dismiss();
                }
            }
        });
        btn_canel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlDialog.this.dismiss();
            }
        });
    }


    public interface ConfirmListener{
        void onConfirm(String name,String address);
    }
}
