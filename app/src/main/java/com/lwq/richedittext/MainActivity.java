package com.lwq.richedittext;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lwq.richedittext.super_editext.model.FileData;
import com.lwq.richedittext.super_editext.SharedPreferencesUtils;
import com.lwq.richedittext.super_editext.SuperEditText;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {


//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }

    protected final int CHOOSE_PICTURE = 1;
    private LinearLayout ll_Bottom_Area;
    private SuperEditText richET;

    private EditText et_title;

    private RelativeLayout rl_extra_area;
    //字体大小更改
    private LinearLayout ll_fontsize_area;
    private TextView tv_fontsizeshow;
    //字体颜色更改
    private LinearLayout ll_fontcolor_area;
    private WaitDialog mReadWaitDialog ;
    private WaitDialog mSaveWaitDialog;
    private WaitDialog mAddImgWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filepath = getIntent().getStringExtra("filepath");
        String title=getIntent().getStringExtra("title");

        mReadWaitDialog=new WaitDialog(this,"正在读取文件请稍后");
        mSaveWaitDialog=new WaitDialog(this,"正在保存文件请稍后");
        mAddImgWaitDialog=new WaitDialog(this,"正在添加图片请稍后");


        richET = (SuperEditText) findViewById(R.id.richET);

        et_title= (EditText) findViewById(R.id.et_title);

        et_title.setText(title);
        et_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_ENTER){
                    return true;
                }
                return false;
            }
        });

        richET.setOnEditTextStatusListener(new SuperEditText.OnEditTextStatusListener() {
            @Override
            public void onReadFileFail(String msg) {
                mReadWaitDialog.dismiss();
                Toast.makeText(MainActivity.this,"读取文件失败"+msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReadFileSuccess() {
                mReadWaitDialog.dismiss();
                Toast.makeText(MainActivity.this,"读取文件成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveFileFail(String msg) {
                mSaveWaitDialog.dismiss();
                Toast.makeText(MainActivity.this,"保存文件失败"+msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveFileSuccess() {
                mSaveWaitDialog.dismiss();
                Toast.makeText(MainActivity.this,"保存文件成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddImgSuccess() {
                mAddImgWaitDialog.dismiss();
                Toast.makeText(MainActivity.this,"添加图片成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddImgFail(String msg) {
                mAddImgWaitDialog.dismiss();
                Toast.makeText(MainActivity.this,"添加图片失败"+msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarn(String msg) {
                Toast.makeText(MainActivity.this,"警告:"+msg,Toast.LENGTH_SHORT).show();
            }
        });

        ll_Bottom_Area = (LinearLayout) findViewById(R.id.ll_Bottom_Area);

        rl_extra_area = (RelativeLayout) findViewById(R.id.rl_extra_area);

        ll_fontsize_area = (LinearLayout) findViewById(R.id.ll_fontsize_area);
        tv_fontsizeshow = (TextView) findViewById(R.id.tv_fontsizeshow);
        tv_fontsizeshow.setText(richET.getPaintBrush().getTextSize() + "px");

        ll_fontcolor_area = (LinearLayout) findViewById(R.id.ll_fontcolor_area);
        RecyclerView rcv_color = (RecyclerView) findViewById(R.id.rcv_color);
        //颜色
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorEdittext));
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.DKGRAY);
        colors.add(Color.GRAY);
        colors.add(Color.LTGRAY);


        ColorAdapter mColorAdapter = new ColorAdapter(this, colors, colors.indexOf(richET.getPaintBrush().getTextColor()), new ColorAdapter.OnCheckListener() {
            @Override
            public void onCheckColor(@ColorInt int color) {
                richET.setFontColor(color);
            }
        });
        rcv_color.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_color.setAdapter(mColorAdapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (!TextUtils.isEmpty(filepath)) {
            //弹出等待对话框
            mReadWaitDialog.show();
            richET.readFileData(new File(filepath));
        }
    }

    public void onPictureClick(View view) {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    public void onFontSizeClick(View view) {
        switchFontSet(FONT_SIZE_SET);
    }

    public void onFontColorClick(View view) {
        switchFontSet(FONT_COLOR_SET);
    }

    private final int FONT_SIZE_SET = 1;
    private final int FONT_COLOR_SET = 2;
    private int mCurrentFontSetType = 0;

    private void switchFontSet(int type) {
        if (rl_extra_area.getVisibility() == View.VISIBLE && type == mCurrentFontSetType) {
            rl_extra_area.setVisibility(View.GONE);
            return;
        } else if (type == FONT_SIZE_SET) {
            ll_fontsize_area.setVisibility(View.VISIBLE);
            ll_fontcolor_area.setVisibility(View.GONE);
        } else if (type == FONT_COLOR_SET) {
            ll_fontsize_area.setVisibility(View.GONE);
            ll_fontcolor_area.setVisibility(View.VISIBLE);
        }
        rl_extra_area.setVisibility(View.VISIBLE);
        mCurrentFontSetType = type;
    }


    public void addUrl(View view) {
        UrlDialog urlDialog = new UrlDialog(this, "url", new UrlDialog.ConfirmListener() {
            @Override
            public void onConfirm(String name, String address) {
                richET.addUrl(name, address);
            }
        });
        urlDialog.show();
    }

    public void addPhonenum(View view) {
        UrlDialog urlDialog = new UrlDialog(this, "tel", new UrlDialog.ConfirmListener() {
            @Override
            public void onConfirm(String name, String address) {
                richET.addTel(name, address);
            }
        });
        urlDialog.show();
    }

    private boolean isEditable = false;

    public void changeStatus(View view) {
        Toast.makeText(this,isEditable?"编辑模式":"阅读模式",Toast.LENGTH_SHORT).show();
        richET.changeEditable(isEditable);
        isEditable = !isEditable;
    }


    private ArrayList<FileData> fileDatas;
    public void onSave(View view) {
        String title = et_title.getText().toString();
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this,"题目不能为空",Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(this,"正在保存",Toast.LENGTH_SHORT).show();

            //弹出等待对话框
            mSaveWaitDialog.show();

            String filepath=richET.saveResultData(title);


            FileData newfileData = new FileData(filepath,title);
            String localFileDatas = (String) SharedPreferencesUtils.getParam(this,"file_datas","");

            //有数据情况下
            if (!TextUtils.isEmpty(localFileDatas)){
                try {
                    //解析数据
                    fileDatas = new Gson().fromJson(localFileDatas, new TypeToken<ArrayList<FileData>>() {
                    }.getType());
                    //判断是否已经存在
                    if (fileDatas.contains(newfileData)){//已经存在 标题有可能改变 所以更新下标题
                        int position=fileDatas.indexOf(newfileData);
                        fileDatas.get(position).setFile_title(newfileData.getFile_title());
                    }else {//不存在 直接加入
                        fileDatas.add(newfileData);
                    }
                    //最后保存数据
                    String newFileDatasJson=new Gson().toJson(fileDatas);
                    SharedPreferencesUtils.setParam(this,"file_datas",newFileDatasJson);
                    return;

                } catch (JsonSyntaxException e) {
                    //取消
                    mSaveWaitDialog.dismiss();
                    e.printStackTrace();
                }

            }
            //以前的数据为空 或者 解析异常直接重置
            fileDatas=new ArrayList<>();
            fileDatas.add(newfileData);
            String newFileDatasJson=new Gson().toJson(fileDatas);
            SharedPreferencesUtils.setParam(this,"file_datas",newFileDatasJson);



        }

    }


    private boolean mOnFontBoldCheck = false;
    private boolean mOnFontItalicCheck = false;

    public void onFontBold(View view) {
        mOnFontBoldCheck = !mOnFontBoldCheck;
        view.setBackground(mOnFontBoldCheck ? getResources().getDrawable(R.drawable.shape_check) : null);
        setFontType();
    }

    public void onFontItalic(View view) {
        mOnFontItalicCheck = !mOnFontItalicCheck;
        view.setBackground(mOnFontItalicCheck ? getResources().getDrawable(R.drawable.shape_check) : null);
        setFontType();
    }

    private void setFontType() {
        if (mOnFontBoldCheck && mOnFontItalicCheck) {
            richET.setFontBold_Italic();
        } else if (!mOnFontBoldCheck && !mOnFontItalicCheck) {
            richET.setFontNormal();
        } else {
            if (mOnFontBoldCheck) {
                richET.setFontBold();
            } else if (mOnFontItalicCheck) {
                richET.setFontItalic();
            }
        }
    }

    public void onFontSizeAdd(View view) {
        if (richET.getFontSize() >= 32) {
            return;
        }
        tv_fontsizeshow.setText(richET.addFontSize() + "px");
    }

    public void onFontSizeReduce(View view) {
        if (richET.getFontSize() <= 8) {
            return;
        }
        tv_fontsizeshow.setText(richET.reduceFontSize() + "px");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PICTURE:
                if (data != null) {
                    richET.addImage(data.getData());
                    //弹出等待对话框
                    mAddImgWaitDialog.show();
                }
                break;
        }
    }
}
