package com.lwq.richedittext;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwq.richedittext.super_editext.model.FileData;

import java.util.ArrayList;

/**
 * User:kelly
 * Date:2017-05-08
 * Time:13:19
 * introduction:
 */

class DataAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<FileData> mFiles;
    private OnViewClickListener mOnViewClickListener;

    DataAdapter(Context context,ArrayList<FileData> fileDatas,OnViewClickListener onViewClickListener) {
        mFiles=fileDatas;
        mContext = context;
        mOnViewClickListener=onViewClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new DataHolder(LayoutInflater.from(mContext).inflate(R.layout.item_data, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        DataHolder dataHolder = (DataHolder) holder;
        dataHolder.tv_title.setText(mFiles.get(position).getFile_title());
        dataHolder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener!=null){
                    mOnViewClickListener.onTitleClick(mFiles.get(holder.getAdapterPosition()));
                }

            }
        });
        dataHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener!=null){
                    mOnViewClickListener.onDeleteClick(mFiles.get(holder.getAdapterPosition()));
                }
            }
        });

    }

    public void notifyDataSetChanged(ArrayList<FileData> fileDatas){
        mFiles=fileDatas;
        notifyDataSetChanged();
    }

    interface OnViewClickListener{
        void onDeleteClick(FileData fileData);
        void onTitleClick(FileData fileData);
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    private class DataHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_delete;

        DataHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
