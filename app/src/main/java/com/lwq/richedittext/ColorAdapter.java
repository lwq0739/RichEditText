package com.lwq.richedittext;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * User:kelly
 * Date:2017-05-08
 * Time:13:19
 * introduction:
 */

class ColorAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Integer> mColors;
    private int mCheckColor = 0;

    ColorAdapter(Context context, ArrayList<Integer> colorlist, int checkcolor, OnCheckListener onCheckListener) {
        mColors = colorlist;
        mContext = context;
        mCheckColor = checkcolor;
        mOnCheckListener = onCheckListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ColorHolder(LayoutInflater.from(mContext).inflate(R.layout.item_color, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ColorHolder colorHolder = (ColorHolder) holder;
        colorHolder.iv_color.setColorFilter(mColors.get(position));
        if (position == mCheckColor) {
            colorHolder.iv_color.setBackgroundResource(R.drawable.shape_check);
        } else {
            colorHolder.iv_color.setBackground(null);
        }
        colorHolder.iv_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                notifyDataSetChanged(position);
                if (mOnCheckListener != null) {
                    mOnCheckListener.onCheckColor(mColors.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }

    private void notifyDataSetChanged(int checkcolor) {
        mCheckColor = checkcolor;
        notifyDataSetChanged();
    }

//    int getmCheckColor(){
//        return mColors.get(mCheckColor);
//    }

    private OnCheckListener mOnCheckListener;

    public interface OnCheckListener {
        void onCheckColor(@ColorInt int color);
    }

    private class ColorHolder extends RecyclerView.ViewHolder {
        ImageView iv_color;

        ColorHolder(View itemView) {
            super(itemView);
            iv_color = (ImageView) itemView.findViewById(R.id.iv_color);
        }
    }
}
