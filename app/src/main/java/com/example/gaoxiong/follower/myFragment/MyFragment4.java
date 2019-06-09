package com.example.gaoxiong.follower.myFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.gaoxiong.follower.BleActivity;
import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment4 extends Fragment {

    public MyFragment4() {
    }
    String[] str = new String[]{BleUUID.CHAIR_ADDRESS,BleUUID.RADAR_ADDRESS,BleUUID.CUSION_ADDRESS};
    String[] str2 = new String[]{"连接","连接","连接"};
    View view;
    Button scan;
    Context context;
    ListView listView;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    SimpleAdapter simpleAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.connect_ble_layout,container,false);
         scan = (Button) view.findViewById(R.id.cButton);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(),BleActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView)view.findViewById(R.id.ble_list);
        Logger.e( "第四个Fragment");


        listView.setAdapter(simpleAdapter);
        return view;

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addData();
         simpleAdapter = new SimpleAdapter(context.getApplicationContext(), list, R.layout.connect,new String[]{"user_name", "user_id","connect"}, new int[]{R.id.user1_name, R.id.user1_id,R.id.connect}){
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
                                context.getApplicationContext().sendBroadcast(i);
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
                        context.getApplicationContext().sendBroadcast(intent);
                    }

                });
                return view;
            }

        };

    }
    public  void addData(){
    HashMap<String, String> map1 = new HashMap<>();
    map1.put("user_name", "智能轮椅");
    map1.put("user_id",str[0]);
    map1.put("connect",str2[0]);
    list.add(map1);
    HashMap<String, String> map2 = new HashMap<>();
    map2.put("user_name", "激光雷达");
    map2.put("user_id",str[1]);
    map2.put("connect",str2[1]);
    list.add(map2);
    HashMap<String, String> map3= new HashMap<>();
    map3.put("user_name", "智能坐垫");
    map3.put("user_id",str[2]);
    map3.put("connect",str2[2]);
    list.add(map3);
}

}
