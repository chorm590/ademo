package com.chorm.ademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.chorm.ademo.funClass.CusWatchViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView lv;
    private String[] mainTitle = new String[]{
            "自定义表盘",

    };
    private String[] subTitle = new String[]{
            "一种自定义视图实现的模拟手表表盘视图",
    };
    private Class[] clz = new Class[]{
            CusWatchViewActivity.class,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.lv);
        List<Map<String, String>> data = new ArrayList<>();
        for(int i = 0; i < mainTitle.length; i++){
            try {
                Map<String, String> item = new HashMap<>();
                item.put("mainTitle", mainTitle[i]);
                item.put("subTitle", subTitle[i]);
                data.add(item);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                break;
            }

        }

        String[] from = new String[]{
                "mainTitle",
                "subTitle",
        };
        int[] to = new int[]{
                android.R.id.text1,
                android.R.id.text2
        };

        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, from, to);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent().setClass(this, clz[i]));
    }
}
