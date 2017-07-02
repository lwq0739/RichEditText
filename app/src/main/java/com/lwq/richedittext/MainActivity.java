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
import com.lwq.richedittext.super_editext.SuperEditText;
import com.lwq.richedittext.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {


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
    private RecyclerView rcv_color;
    private WaitDialog mReadWaitDialog;
    private WaitDialog mSaveWaitDialog;
    private WaitDialog mAddImgWaitDialog;


    @Override
    protected View initView() {
        String title = getIntent().getStringExtra("title");

        View view = View.inflate(this, R.layout.activity_main, null);

        richET = (SuperEditText) view.findViewById(R.id.richET);
        et_title = (EditText) view.findViewById(R.id.et_title);
        et_title.setText(title);
        ll_Bottom_Area = (LinearLayout) view.findViewById(R.id.ll_Bottom_Area);
        rl_extra_area = (RelativeLayout) view.findViewById(R.id.rl_extra_area);
        ll_fontsize_area = (LinearLayout) view.findViewById(R.id.ll_fontsize_area);
        tv_fontsizeshow = (TextView) view.findViewById(R.id.tv_fontsizeshow);
        tv_fontsizeshow.setText(richET.getPaintBrush().getTextSize() + "px");
        ll_fontcolor_area = (LinearLayout) view.findViewById(R.id.ll_fontcolor_area);
        rcv_color = (RecyclerView) view.findViewById(R.id.rcv_color);
        return view;
    }

    @Override
    protected void setListener() {
        et_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
        richET.setOnEditTextStatusListener(new SuperEditText.OnEditTextStatusListener() {
            @Override
            public void onReadFileFail(String msg) {
                mReadWaitDialog.dismiss();
                Toast.makeText(MainActivity.this, "读取文件失败" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReadFileSuccess() {
                mReadWaitDialog.dismiss();
                Toast.makeText(MainActivity.this, "读取文件成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveFileFail(String msg) {
                mSaveWaitDialog.dismiss();
                Toast.makeText(MainActivity.this, "保存文件失败" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveFileSuccess() {
                mSaveWaitDialog.dismiss();
                Toast.makeText(MainActivity.this, "保存文件成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddImgSuccess() {
                mAddImgWaitDialog.dismiss();
                Toast.makeText(MainActivity.this, "添加图片成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddImgFail(String msg) {
                mAddImgWaitDialog.dismiss();
                Toast.makeText(MainActivity.this, "添加图片失败" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarn(String msg) {
                Toast.makeText(MainActivity.this, "警告:" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void initData() {
        mReadWaitDialog = new WaitDialog(this, "正在读取文件请稍后");
        mSaveWaitDialog = new WaitDialog(this, "正在保存文件请稍后");
        mAddImgWaitDialog = new WaitDialog(this, "正在添加图片请稍后");

        //颜色数据填充
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


        String filepath = getIntent().getStringExtra("filepath");
        if (!TextUtils.isEmpty(filepath)) {
            //弹出等待对话框
            mReadWaitDialog.show();
            richET.readFileData(new File(filepath));
        }
    }

    /**
     * 按钮1保存
     *
     * @param view
     */
    public void onSave(View view) {
        String title = et_title.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "题目不能为空", Toast.LENGTH_SHORT).show();
        } else {

            //弹出等待对话框
            mSaveWaitDialog.show();
            //保存文件 并获取文件绝对路径
            String filepath = richET.saveResultData(title);

            //以下都为数据保存
            FileData newfileData = new FileData(filepath, title);

            try {
                saveFileInformation(newfileData);
            } catch (Exception e) {
                e.printStackTrace();
                mSaveWaitDialog.dismiss();
            }

        }
    }

    private void saveFileInformation(FileData newfileData) throws Exception {
        String localFileDatas = (String) SharedPreferencesUtils.getParam(this, "file_datas", "");
        ArrayList<FileData> fileDatas;
        //有数据情况下
        if (!TextUtils.isEmpty(localFileDatas)) {
            //解析数据
            fileDatas = new Gson().fromJson(localFileDatas, new TypeToken<ArrayList<FileData>>() {
            }.getType());
            //判断是否已经存在
            if (fileDatas.contains(newfileData)) {//已经存在 标题有可能改变 所以更新下标题
                int position = fileDatas.indexOf(newfileData);
                fileDatas.get(position).setFile_title(newfileData.getFile_title());
            } else {//不存在 直接加入
                fileDatas.add(newfileData);
            }
            //最后保存数据
            String newFileDatasJson = new Gson().toJson(fileDatas);
            SharedPreferencesUtils.setParam(this, "file_datas", newFileDatasJson);
            return;

        }
        //以前的数据为空 或者 解析异常直接重置
        fileDatas = new ArrayList<>();
        fileDatas.add(newfileData);
        String newFileDatasJson = new Gson().toJson(fileDatas);
        SharedPreferencesUtils.setParam(this, "file_datas", newFileDatasJson);
    }

    /**
     * 按钮2添加图片
     *
     * @param view
     */
    public void onPictureClick(View view) {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    /**
     * 按钮3改变画笔大小
     *
     * @param view
     */
    public void onFontSizeClick(View view) {
        switchFontSet(FONT_SIZE_SET);
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

    /**
     * 按钮4改变画笔颜色
     *
     * @param view
     */
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


    private boolean mOnFontBoldCheck = false;
    private boolean mOnFontItalicCheck = false;

    /**
     * 按钮5字体加粗
     *
     * @param view
     */
    public void onFontBold(View view) {
        mOnFontBoldCheck = !mOnFontBoldCheck;
        view.setBackground(mOnFontBoldCheck ? getResources().getDrawable(R.drawable.shape_check) : null);
        setFontType();
    }

    /**
     * 按钮6字体倾斜
     *
     * @param view
     */
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

    private boolean isEditable = false;

    /**
     * 按钮7锁定 阅读模式与编辑模式切换
     *
     * @param view
     */
    public void changeStatus(View view) {
        Toast.makeText(this, isEditable ? "编辑模式" : "阅读模式", Toast.LENGTH_SHORT).show();
        richET.changeEditable(isEditable);
        isEditable = !isEditable;
    }

    /**
     * 按钮8 插入url链接
     *
     * @param view
     */
    public void addUrl(View view) {
        UrlDialog urlDialog = new UrlDialog(this, "url", new UrlDialog.ConfirmListener() {
            @Override
            public void onConfirm(String name, String address) {
                richET.addUrl(name, address);
            }
        });
        urlDialog.show();
    }

    /**
     * 按钮9 插入手机号码链接
     *
     * @param view
     */
    public void addPhonenum(View view) {
        UrlDialog urlDialog = new UrlDialog(this, "tel", new UrlDialog.ConfirmListener() {
            @Override
            public void onConfirm(String name, String address) {
                richET.addTel(name, address);
            }
        });
        urlDialog.show();
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
