package com.lwq.richedittext;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * User:lwq
 * Date:2017-07-01
 * Time:12:27
 * introduction:
 */
public class WaitDialog extends Dialog {

    public WaitDialog(@NonNull Context context,String title) {
        super(context);
        setContentView(R.layout.dialog_wait);
        ((TextView)findViewById(R.id.tv_title)).setText(title);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
            lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
            dialogWindow.setAttributes(lp);
        }
        setCancelable(false);
    }

}
