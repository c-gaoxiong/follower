package com.example.gaoxiong.follower;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectActivity extends ListActivity {
String[] str = new String[]{BleUUID.CHAIR_ADDRESS,BleUUID.RADAR_ADDRESS,null};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("user_name", "智能轮椅");
        map1.put("user_id",BleUUID.CHAIR_ADDRESS);
        map1.put("connect","连接");
        list.add(map1);
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("user_name", "激光雷达");
        map2.put("user_id",BleUUID.RADAR_ADDRESS);
        map2.put("connect","连接");
        list.add(map2);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.connect,
                new String[]{"user_name", "user_id","connect"}, new int[]{R.id.user_name, R.id.user_id,R.id.connect});
        setListAdapter(simpleAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent =new Intent("android.ble.chair.control");
        intent.putExtra("address",str[position]);
        sendBroadcast(intent);
    }
}
