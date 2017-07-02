package com.lwq.richedittext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lwq.richedittext.super_editext.model.FileData;
import com.lwq.richedittext.super_editext.SharedPreferencesUtils;

import java.util.ArrayList;

/**
 * User:lwq
 * Date:2017-06-27
 * Time:14:39
 * introduction:
 */

public class IndexActivity extends Activity {
    private RecyclerView rcy_data;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        rcy_data= (RecyclerView) findViewById(R.id.rcy_data);

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
