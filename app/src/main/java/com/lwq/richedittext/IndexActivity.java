package com.lwq.richedittext;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lwq.richedittext.super_editext.model.FileData;
import com.lwq.richedittext.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * User:lwq
 * Date:2017-06-27
 * Time:14:39
 * introduction:
 */

public class IndexActivity extends BaseActivity {
    private RecyclerView rcy_data;
    @Override
    protected View initView() {
        View view = View.inflate(this,R.layout.activity_index,null);
        rcy_data= (RecyclerView) view.findViewById(R.id.rcy_data);
        return view;
    }

    @Override
    protected void initData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    public void createEditext(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updataAdapter();
    }

    private ArrayList<FileData> getFileData(){
        String localFileDatas = (String) SharedPreferencesUtils.getParam(this,"file_datas","");
        ArrayList<FileData> fileDataArrayList=new Gson().fromJson(localFileDatas, new TypeToken<ArrayList<FileData>>() {
        }.getType());
        if (fileDataArrayList==null){
            fileDataArrayList=new ArrayList<>();
        }
        return fileDataArrayList;
    }

    private DataAdapter dataAdapter;
    private void updataAdapter(){
        final ArrayList<FileData> fileDataArrayList=getFileData();
        if (dataAdapter==null){
            dataAdapter = new DataAdapter(this, fileDataArrayList, new DataAdapter.OnViewClickListener() {
                @Override
                public void onDeleteClick(final FileData fileData) {

                    new AlertDialog.Builder(IndexActivity.this)
                            .setTitle("确认")
                            .setMessage("确认删除此文章吗")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除文件
                                    new File(fileData.getFile_path()).delete();
                                    fileDataArrayList.remove(fileData);
                                    dataAdapter.notifyDataSetChanged(fileDataArrayList);
                                    //更新本地数据
                                    SharedPreferencesUtils.setParam(IndexActivity.this,"file_datas",new Gson().toJson(fileDataArrayList));
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

                @Override
                public void onTitleClick(FileData fileData) {
                    Intent intent = new Intent(IndexActivity.this,MainActivity.class);
                    intent.putExtra("filepath",fileData.getFile_path());
                    intent.putExtra("title",fileData.getFile_title());
                    IndexActivity.this.startActivity(intent);
                }
            });
            rcy_data.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            rcy_data.setAdapter(dataAdapter);
        }else {
            dataAdapter.notifyDataSetChanged(fileDataArrayList);
        }

    }
}
