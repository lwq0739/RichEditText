package com.lwq.richedittext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lwq.richedittext.super_editext.model.FileData;
import com.lwq.richedittext.utils.SharedPreferencesUtils;

import java.util.ArrayList;

/**
 * User:lwq
 * Date:2017-06-27
 * Time:14:39
 * introduction:
 */

public class IndexActivity extends BaseActivity {
    private RecyclerView rcy_data;
    private boolean isFirst = true;
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
        String localFileDatas = (String) SharedPreferencesUtils.getParam(this,"file_datas","");
        ArrayList<FileData> fileDataArrayList=new Gson().fromJson(localFileDatas, new TypeToken<ArrayList<FileData>>() {
        }.getType());
        if (fileDataArrayList==null){
            fileDataArrayList=new ArrayList<>();
        }
        DataAdapter dataAdapter = new DataAdapter(this,fileDataArrayList);
        rcy_data.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rcy_data.setAdapter(dataAdapter);
    }
}
