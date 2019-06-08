package com.example.gaoxiong.follower;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectActivity extends ListActivity {
    String[] str = new String[]{BleUUID.CHAIR_ADDRESS,BleUUID.RADAR_ADDRESS};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("user_name", "智能轮椅");
        map1.put("user_id",str[0]);
        map1.put("connect","连接");
        list.add(map1);
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("user_name", "激光雷达");
        map2.put("user_id",str[1]);
        map2.put("connect","连接");
        list.add(map2);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.connect,
                new String[]{"user_name", "user_id","connect"}, new int[]{R.id.user1_name, R.id.user1_id,R.id.connect}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                final int p=position;
                final View view=super.getView(position, convertView, parent);
                view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(0,0,0,"断开连接").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(BleUUID.CHAIR_CONTROL);
                                i.putExtra("disconnect","disconnect");
                                i.putExtra("address",str[p]);
                                sendBroadcast(i);
                                return true;
                            }
                        });
                    }
                });
                Button useBtn=(Button)view.findViewById(R.id.connect);
                useBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Logger.d("点击 ："+p);
                        Intent intent =new Intent("android.ble.chair.control");
                        intent.putExtra("address",str[p]);
                        sendBroadcast(intent);
                    }

                });
//                convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//
//
//                        return true;
//                    }
//                });
                return view;
            }



        };
        setListAdapter(simpleAdapter);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

//        Intent intent =new Intent("android.ble.chair.control");
//        intent.putExtra("address",str[position]);
//        sendBroadcast(intent);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Logger.d("点击了：");
            }
        });


    }
//    onL
}
