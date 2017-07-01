package com.lwq.richedittext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.lwq.richedittext.super_editext.BitmapUtils;
import com.lwq.richedittext.super_editext.PaintBrush;
import com.lwq.richedittext.super_editext.SuperEditText;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User:kelly
 * Date:2017-05-10
 * Time:18:01
 * introduction:
 */

public class SuperEditextContainer extends ScrollView {


    public SuperEditextContainer(Context context) {
        super(context);
        init(context);
    }

    public SuperEditextContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuperEditextContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private LinearLayout mLinearLayout;
    Rect scrollBounds = new Rect();
    private void init(Context context) {
        mContext = context;
        createLinearLayoutContainer();

//        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//        // 使用最大可用内存值的1/8作为缓存的大小。
//        int cacheSize = maxMemory / 8;
//        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
//                return bitmap.getByteCount() / 1024;
//            }
//        };
        getHitRect(scrollBounds);

    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (int i = 0; i < mSuperEditTexts.size(); i++) {
            if (mSuperEditTexts.get(i).getLocalVisibleRect(scrollBounds)) {
                //子控件至少有一个像素在可视范围内
            } else {
                //子控件完全不在可视范围内
            }
        }

    }

    private ArrayList<SuperEditText> mSuperEditTexts = new ArrayList<>();
    private int mCurrentLine = 0;

    public SuperEditText getmCurrentRichEditText() {
        return mSuperEditTexts.get(mCurrentLine);
    }

    public int getmCurrentLine() {
        return mCurrentLine;
    }

    public void setmCurrentLine(int mCurrenLine) {
        mCurrentLine = mCurrenLine;
    }






    private void createLinearLayoutContainer() {
        mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        ScrollView.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinearLayout.setLayoutParams(layoutParams);
        addView(mLinearLayout);

        createRichEditText();
    }

    private void createRichEditText() {
        final int position = mSuperEditTexts.size();
        SuperEditText superEditText = new SuperEditText(mContext);
        superEditText.setTextSize(20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 0, 15, 0);
        superEditText.setLayoutParams(layoutParams);

        superEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setmCurrentLine(position);
                }
            }
        });
        superEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SuperEditText text = (SuperEditText) v;
                if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction() && text.getSelectionStart() == text.length()) {
                    nextRichEditText(position);
                    return true;
                }
                return false;
            }
        });
//        richEditText.setOnSizeChanged(new RichEditText.OnSizeChanged() {
//            @Override
//            public void onSizeChanged(int hight) {
//
//            }
//        });
//        richEditText.setmOnPaintBrush(new RichEditText.OnPaintBrush() {
//            @Override
//            public PaintBrush onPaintBrush() {
//                return mPaintBrush;
//            }
//        });
        mSuperEditTexts.add(superEditText);
        setmCurrentLine(position);
        mLinearLayout.addView(superEditText);
    }

    public void nextRichEditText(int position){
        if (position == mSuperEditTexts.size() - 1) {
            createRichEditText();
            mSuperEditTexts.get(position + 1).requestFocus();
        } else {
            mSuperEditTexts.get(position + 1).requestFocus();
        }
    }

    public void addPicture(Uri uri, String s) throws IOException {
        SuperEditText superEditText = mSuperEditTexts.get(getmCurrentLine());
//        richEditText.addImage(uri, "asdad");
//        nextRichEditText(getmCurrentLine());


        int paddingLeft = superEditText.getPaddingLeft();
        int paddingRight = superEditText.getPaddingRight();
        int zoomWidth = superEditText.getWidth() - (paddingLeft + paddingRight);
//        Bitmap bitmap = getBitmapFromMemCache(s);
//        if (bitmap!=null){
//            richEditText.addImage(bitmap,"sssss");
//        }else {
//            bitmap=BitmapUtils.getBitmapFormUri(mContext,uri,zoomWidth);
//            richEditText.addImage(bitmap,"sssss");
//            addBitmapToMemoryCache(s,bitmap);
//        }

        Bitmap bitmap= BitmapUtils.getBitmapFormUri(mContext,uri,zoomWidth);
//        richEditText.addImage(bitmap,"sssss");

        nextRichEditText(getmCurrentLine());
    }


    public void updataFont(){
//        getmCurrentRichEditText().updataFont();
    }

    public void setGravity(int gravity){
        getmCurrentRichEditText().setGravity(gravity);
    }

    public void changeEditable(boolean canEditable){
        for (int i = 0; i < mSuperEditTexts.size(); i++) {
            mSuperEditTexts.get(i).changeEditable(canEditable);
        }
    }


    private PaintBrush mPaintBrush = new PaintBrush();

    public PaintBrush getmPaintBrush() {
        return mPaintBrush;
    }

    public void setmPaintBrush(PaintBrush mPaintBrush) {
        this.mPaintBrush = mPaintBrush;
    }




//    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
//        if (getBitmapFromMemCache(key) == null) {
//            mMemoryCache.put(key, bitmap);
//        }
//    }
//
//    public Bitmap getBitmapFromMemCache(String key) {
//        return mMemoryCache.get(key);
//    }
}
