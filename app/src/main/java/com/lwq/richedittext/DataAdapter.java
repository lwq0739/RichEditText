package com.lwq.richedittext;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    DataAdapter(Context context,ArrayList<FileData> fileDatas) {
        mFiles=fileDatas;
        mContext = context;
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
                Intent intent = new Intent(mContext,MainActivity.class);
                intent.putExtra("filepath",mFiles.get(holder.getAdapterPosition()).getFile_path());
                intent.putExtra("title",mFiles.get(holder.getAdapterPosition()).getFile_title());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    private class DataHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        DataHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
