package com.lwq.richedittext.super_editext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lwq.richedittext.super_editext.model.JsonData;
import com.lwq.richedittext.super_editext.model.PaintBrush;
import com.lwq.richedittext.super_editext.utils.BitmapUtils;
import com.lwq.richedittext.super_editext.utils.FileUtils;
import com.lwq.richedittext.super_editext.utils.LogUtils;
import com.lwq.richedittext.super_editext.utils.Md5Utils;
import com.lwq.richedittext.super_editext.utils.PathByUri;
import com.lwq.richedittext.super_editext.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User:lwq
 * Date:2017-04-15
 * Time:10:20
 * introduction:
 */
public class SuperEditText extends android.support.v7.widget.AppCompatEditText {

    public interface OnEditTextStatusListener {
        void onReadFileFail(String msg);

        void onReadFileSuccess();

        void onSaveFileFail(String msg);

        void onSaveFileSuccess();

        void onAddImgFail(String msg);

        void onAddImgSuccess();

        void onWarn(String msg);
    }

    private Context mContext;
    //状态事件回调
    private OnEditTextStatusListener mOnEditTextStatusListener;
    //画笔
    private PaintBrush mPaintBrush = new PaintBrush();
    //是否可以编辑
    private boolean mCanEditable = true;
    //临时Bitmap
    private Bitmap mTempBitmap;
    //数据集合
    private ArrayList<JsonData> jsonDatas = new ArrayList<>();
    //handler事件处理
    private final int INSERT_TEXT = 1;
    private final int INSERT_IMG = 2;
    private final int INSERT_LOCAL_DATA = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INSERT_TEXT:
                    JsonData.TextData textData = (JsonData.TextData) msg.obj;
                    insertText(textData.getPaintBrush(), textData.getText(), textData.getUrl());
                    break;
                case INSERT_IMG:
                    JsonData.ImgData imgData = (JsonData.ImgData) msg.obj;
                    insertImg(mTempBitmap, imgData.getLoclPath(), imgData.getUrlPath());
                    //添加图片成功回调
                    onAddImgSuccess();
                    break;
                case INSERT_LOCAL_DATA:
                    insertLocalData();

                    break;
            }
        }
    };

    public SuperEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public SuperEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public SuperEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setBackground(null);
    }

    //******************************公共方法********************************************************//

    public void setOnEditTextStatusListener(OnEditTextStatusListener mOnEditTextStatusListener) {
        this.mOnEditTextStatusListener = mOnEditTextStatusListener;
    }

    public PaintBrush getPaintBrush() {
        return mPaintBrush;
    }

    public void setmPaintBrush(PaintBrush mPaintBrush) {
        this.mPaintBrush = mPaintBrush;
    }


    public void addImage(Uri uri) {
        if (uri == null) {
            //添加照片失败
            onAddImgFail("uri不能为空");
            return;
        }
        addImage_asynchronous(PathByUri.getRealFilePath(mContext, uri));

    }

    public void addImage(String path) {
        if (TextUtils.isEmpty(path)) {
            //添加照片失败
            onAddImgFail("path不能为空");
            return;
        }
        addImage_asynchronous(path);

    }

    /**
     * 更变文本编辑状态 (不可编辑状态 无法插入文字 图片等 可点击链接)
     *
     * @param canEditable 是否可以编辑
     */
    public void changeEditable(boolean canEditable) {
        mCanEditable = canEditable;
        setCursorVisible(canEditable);

        if (!mCanEditable) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0); //强制隐藏键盘
        }

    }

    /**
     * 插入url链接 只在不可编辑状态下点击跳转
     *
     * @param text 名称
     * @param url  url地址
     */
    public void addUrl(String text, String url) {
        insertText(getPaintBrush(), text, url);
    }

    /**
     * 插入tel链接 只在不可编辑状态下点击跳转
     *
     * @param text     名称
     * @param phonenum 手机号码
     */
    public void addTel(String text, String phonenum) {
        insertText(getPaintBrush(), text, "tel:" + phonenum);
    }


    public int getFontSize() {
        return getPaintBrush().getTextSize();
    }

    public int getFontType() {
        return getPaintBrush().getTypeface();
    }

    public int getFontColor() {
        return getPaintBrush().getTextColor();
    }

    public int addFontSize() {
        mPaintBrush.setTextSize(mPaintBrush.getTextSize() + 1);
        return mPaintBrush.getTextSize();
    }

    public int reduceFontSize() {
        mPaintBrush.setTextSize(mPaintBrush.getTextSize() - 1);
        return mPaintBrush.getTextSize();
    }

    public void setFontColor(@ColorInt int color) {
        mPaintBrush.setTextColor(color);
    }

    public void setFontNormal() {
        mPaintBrush.setTypeface(Typeface.NORMAL);
    }

    public void setFontBold() {
        mPaintBrush.setTypeface(Typeface.BOLD);
    }

    public void setFontItalic() {
        mPaintBrush.setTypeface(Typeface.ITALIC);
    }

    public void setFontBold_Italic() {
        mPaintBrush.setTypeface(Typeface.BOLD_ITALIC);
    }


    private String mFileName = "";
    private String mTitle = "";
    private String mLastEditTime = "";
    private HashMap<String, Bitmap> mLocalFileImgs;
    private ArrayList<JsonData> mLocalFileDatas;

    /**
     * 读取数据file
     *
     * @param file file
     */
    public void readFileData(final File file) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                //数据读取
                String[] datas;
                try {
                    datas = FileUtils.readSDFile(file.getAbsolutePath()).split("\n");
                } catch (IOException e) {
                    //读取文件失败回调
                    onReadFileFail(e.getMessage());
                    e.printStackTrace();
                    return;
                }

                mFileName = file.getName();
                mTitle = datas[0];
                mLastEditTime = datas[1];
                String textdata = datas[2];

                //数据解析
                try {
                    mLocalFileDatas = new Gson().fromJson(textdata, new TypeToken<ArrayList<JsonData>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    onReadFileFail(e.getMessage());
                    e.printStackTrace();
                    return;
                }

                /////////////////////////////////////////////////////////////////////////////////////



                while (true){
                    if (getWidth()!=0){
                        mLocalFileImgs = new HashMap<>();
                        for (JsonData data : mLocalFileDatas) {
                            if (data == null) {
                                continue;
                            }
                            if (data.getType() == 2) {//图片
                                Bitmap localbitmap = change_image(data.getImgData().getLoclPath(), true);
                                //图片处理
                                if (localbitmap != null) {
                                    mLocalFileImgs.put(data.getImgData().getLoclPath(), localbitmap);
                                }
                            }
                        }
                        sendMessage(INSERT_LOCAL_DATA, null);
                        break;
                    }else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();


//        System.out.println("asdasd  " + textdata);

    }

    private void insertLocalData() {
        //读取本地数据
        //数据填充
        for (JsonData data : mLocalFileDatas) {
            if (data == null) {
                continue;
            }
            if (data.getType() == 2) {//图片
                JsonData.ImgData imgdata = data.getImgData();
                Bitmap bitmap = mLocalFileImgs.get(data.getImgData().getLoclPath());
                //图片处理
                if (bitmap != null) {
                    insertImg(bitmap, imgdata.getLoclPath(), imgdata.getUrlPath());
                }
            } else if (data.getType() == 1) {//文字
                JsonData.TextData textdata = data.getTextData();
                insertText(textdata.getPaintBrush(), textdata.getText(), textdata.getUrl());
            }
        }
        //读取文件结束回调
        onReadFileSuccess();
        mLocalFileDatas = null;
        mLocalFileImgs = null;
    }

    /**
     * 获得数据集合jsonDatas的json字符串
     *
     * @return jsonDatas的json字符串
     */
    public String getResultData() {
        Gson gson = new Gson();
        return gson.toJson(jsonDatas);
    }

    /**
     * 保存结果数据
     *
     * @param title 标题
     * @return 保存文件的地址
     */
    public String saveResultData(String title) {
        String finalfilename = mFileName;
        String finalfilepath = "";
        //1文本文件已经存在 直接更新文件 不使用传入的filepath
        //2文本文件不存在  使用时间戳加md5作为文件名
        if (TextUtils.isEmpty(mFileName)) {//文件不存在
            finalfilename = Md5Utils.md5(TimeUtils.getTime());
        }

        LogUtils.i(finalfilename + "    " + title + "   " + TimeUtils.getTime() + "   " + getResultData());
        try {
            finalfilepath = FileUtils.createSDNewFile(Constant.SAVE_PATH, finalfilename);
            FileUtils.print(finalfilepath, title + "\n" + TimeUtils.getTime() + "\n" + getResultData());
            onSaveFileSuccess();
        } catch (IOException e) {
            onSaveFileFail(e.getMessage());
            if (!TextUtils.isEmpty(finalfilepath)) {
                File file = new File(finalfilepath);
                file.delete();
            }
            e.printStackTrace();
            return null;
        }
        return finalfilepath;
    }

    //******************************公共方法********************************************************//


    /**
     * 图片处理 压缩 与 缩放
     *
     * @param filePath  图片路径
     * @param fitscreen 是否缩放到当前文本宽度
     * @return 处理后的Bitmap
     */
    private Bitmap change_image(String filePath, boolean fitscreen) {

        //图片压缩
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int zoomWidth = getWidth() - (paddingLeft + paddingRight);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtils.getBitmapFormPath( filePath, zoomWidth);
        } catch (IOException e) {
            //添加照片失败
            onAddImgFail(e.getMessage());
            e.printStackTrace();
            return null;
        }

        if (fitscreen) {
            //图片适应屏幕宽度缩放
            int bmWidth = bitmap.getWidth();//图片高度
            int bmHeight = bitmap.getHeight();//图片宽度
            int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight * 1);
            bitmap = BitmapUtils.zoomImage(bitmap, zoomWidth - 10, zoomHeight);
        }
        mTempBitmap = bitmap;
        return bitmap;

    }

    /**
     * 异步插入图片
     *
     * @param filePath 图片路径
     */
    private void addImage_asynchronous(final String filePath) {
        if (!mCanEditable) {
            onWarn("不可编辑状态(阅读模式/正在插入图片)");
            onAddImgFail("不可编辑状态(阅读模式/正在插入图片)");
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (change_image(filePath, true) != null) {
                    JsonData.ImgData imgData = new JsonData.ImgData();
                    imgData.setLoclPath(filePath);
                    imgData.setUrlPath("");
                    sendMessage(INSERT_IMG, imgData);
                    //手动插入图片加个回车方便编辑
                    JsonData.TextData textData = new JsonData.TextData();
                    textData.setText("\n");
                    sendMessage(INSERT_TEXT, textData);
                }
            }
        }.start();

    }

    private void sendMessage(int what, Object obj) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        mHandler.sendMessage(message);
    }

    /**
     * 插入图片
     *
     * @param bitmap    bitmap
     * @param localpath 本地路径
     * @param url       url路径
     */
    private void insertImg(Bitmap bitmap, String localpath, String url) {
        ImageSpan imgSpan = new ImageSpan(mContext, bitmap);
        SpannableString spanString = new SpannableString(localpath);
        spanString.setSpan(imgSpan, 0, localpath.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int start = getSelectionStart(); // 用于计算图片在文中长度 开始位置
        getText().insert(start, spanString); // 设置spanString要添加的位置
        int end = getSelectionStart();// 用于计算图片在文中长度 结束位置
        //更新数据
        updataImg(localpath, url, start, end);
    }

    /**
     * 插入文本
     *
     * @param paintBrush 画笔 可以为空
     * @param text       文本内容
     * @param url        可点击文本地址 可以为空
     */
    private void insertText(@Nullable PaintBrush paintBrush, String text, @Nullable String url) {
        if (!mCanEditable) {
            onWarn("不可编辑状态(阅读模式/正在插入图片)");
            return;
        }
        if (TextUtils.equals(text, "\n")) {
            updataText(text, getSelectionStart(), null, null);
            getText().insert(getSelectionStart(), "\n");
            return;
        }
        paintBrush = paintBrush == null ? getPaintBrush() : paintBrush;
        SpannableString spannableString = new SpannableString(text);
        //字体大小
        spannableString.setSpan(new AbsoluteSizeSpan(paintBrush.getTextSize(), true), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //字体颜色
        spannableString.setSpan(new ForegroundColorSpan(paintBrush.getTextColor()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //字体样式
        switch (paintBrush.getTypeface()) {
            case Typeface.NORMAL:
                spannableString.setSpan(new StyleSpan(Typeface.NORMAL), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //正常
                break;
            case Typeface.BOLD:
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
                break;
            case Typeface.ITALIC:
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
                break;
            case Typeface.BOLD_ITALIC:
                spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗斜体
                break;
        }
        //url类型
        if (!TextUtils.isEmpty(url)) {
            spannableString.setSpan(new URLSpan(url), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //网络
        }

        int start = getSelectionStart();
        //更新文本数据
        updataText(text, start, paintBrush, url);
        //插入文本
        getText().insert(start, spannableString);

    }

    /**
     * 更新图片数据到jsonDatas
     *
     * @param loclpath 本地图片地址
     * @param urlpath  url地址
     * @param start    开始位置
     * @param end      结束位置
     */
    private void updataImg(String loclpath, String urlpath, int start, int end) {
        int sum = end - start;
        for (int i = 0; i < sum; i++) {
            if (i == 0) {
                JsonData jsonData = new JsonData();
                JsonData.ImgData imgData = new JsonData.ImgData();
                imgData.setLoclPath(loclpath);
                imgData.setUrlPath(urlpath);
                jsonData.setType(2);
                jsonData.setImgData(imgData);
                jsonDatas.add(start, jsonData);
            } else {
                jsonDatas.add(start + 1, null);
            }
        }
    }

    /**
     * 更新文本数据到jsonDatas
     *
     * @param text       文本信息
     * @param start      开始位置
     * @param paintBrush 画笔
     * @param url        链接地址
     */
    private void updataText(String text, int start, PaintBrush paintBrush, @Nullable String url) {
        for (int i = 0; i < text.length(); i++) {
            String s = text.substring(i, i + 1);
            JsonData jsonData = new JsonData();
            JsonData.TextData textData = new JsonData.TextData();
            jsonData.setType(1);
            if (TextUtils.equals(s, "\n")) {//回车

                textData.setText("\n");
            } else {//文字

                textData.setText(s);
                textData.setPaintBrush(new PaintBrush(paintBrush.getTextSize(), paintBrush.getTypeface(), paintBrush.getTextColor()));
                if (!TextUtils.isEmpty(url)) {
                    textData.setUrl(url);
                }
            }
            jsonData.setTextData(textData);

            jsonDatas.add(start + i, jsonData);
        }

    }


    private void onWarn(String msg) {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onWarn(msg);
        }
    }

    private void onReadFileFail(String msg) {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onReadFileFail(msg);
        }
    }

    private void onReadFileSuccess() {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onReadFileSuccess();
        }
    }

    private void onSaveFileFail(String msg) {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onSaveFileFail(msg);
        }
    }

    private void onSaveFileSuccess() {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onSaveFileSuccess();
        }
    }

    private void onAddImgFail(String msg) {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onAddImgFail(msg);
        }
    }

    private void onAddImgSuccess() {
        if (mOnEditTextStatusListener != null) {
            mOnEditTextStatusListener.onAddImgSuccess();
        }
    }


    private int backend = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {//删除 记录删除按键的所在位置 用于图片删除的距离计算
            backend = getSelectionStart();
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {//回车
            insertText(null, "\n", null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //记录删除之后的光标位置
            int backstart = getSelectionStart();
            if (backend - backstart == 1) {//字符
                jsonDatas.remove(backstart);
                System.out.println(jsonDatas.toString());
            } else if (backend - backstart > 1) {//图片
                for (int i = 0; i < backend - backstart; i++) { //计算图片在文中长度 并删除相应的占位符
                    jsonDatas.remove(backstart);
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanEditable) {
            return super.onTouchEvent(event);
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= this.getTotalPaddingLeft();
                y -= this.getTotalPaddingTop();

                x += this.getScrollX();
                y += this.getScrollY();

                Layout layout = this.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = getText().getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    link[0].onClick(this);
                }
            }
            return true;
        }
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if (inputConnection == null) {
            return null;
        }
        return new EditTextInputConnection(inputConnection, false);
    }

    private class EditTextInputConnection extends InputConnectionWrapper {

        EditTextInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            // some code which takes the input and manipulates it and calls editText.getText().replace() afterwards

            insertText(getPaintBrush(), text.toString(), null);
            return true;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }
    }


}
