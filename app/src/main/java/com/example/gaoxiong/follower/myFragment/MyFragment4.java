package com.example.gaoxiong.follower.myFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.gaoxiong.follower.BleActivity;
import com.example.gaoxiong.follower.BleService;
import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.MainActivity;
import com.example.gaoxiong.follower.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import static com.example.gaoxiong.follower.MainActivity.mBluetoothAdapter;
import static com.example.gaoxiong.follower.myFragment.MyFragment1.mainActivity;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment4 extends Fragment {

    public MyFragment4() {
    }
    String[] str = new String[]{BleUUID.CHAIR_ADDRESS,BleUUID.RADAR_ADDRESS,BleUUID.CUSHION_ADDRESS};
    String[] str2 = new String[]{"连接","连接","连接"};
    String[] str3 = new String[]{ "智能轮椅", "激光雷达", "智能坐垫"  };
    View view;
    Button scan;
    Context context;
    ListView listView;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    ForthReceiver forthReceiver;
    private long time=0;
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
                mainActivity. vibrator.vibrate(60);
                Intent intent = new Intent(context.getApplicationContext(),BleActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView)view.findViewById(R.id.ble_list);
        Logger.e( "第四个Fragment");

        listView.setAdapter(simpleAdapter);
        updateData();
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

                final Button useBtn=(Button)view.findViewById(R.id.connect);
                useBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mainActivity. vibrator.vibrate(60);
                            Logger.d("点击 ："+useBtn.getText());
                            if(mBluetoothAdapter.isEnabled()){
                                if(useBtn.getText().equals("连接")) {
                                    useBtn.setText(context.getResources().getString(R.string.connecting));
//                                useBtn.setTextColor(context.getResources().getColor(R.color.color_Green));
//                                useBtn.setBackground(context.getResources().getDrawable(R.color.text_gray));
                                    Intent intent = new Intent(BleUUID.CHAIR_CONTROL);
                                    intent.putExtra("address", str[p]);
                                    context.getApplicationContext().sendBroadcast(intent);
                                }
                                if(useBtn.getText().equals(getString(R.string.connecting))){
                                    if((System.currentTimeMillis() - time)>2000){

                                        Toast.makeText(context.getApplicationContext(),"再次点击，停止连接",Toast.LENGTH_SHORT).show();
                                        time = System.currentTimeMillis();
                                    }else {
                                        Intent i = new Intent(BleUUID.CHAIR_CONTROL);
                                        i.putExtra("disconnect","disconnect");
                                        i.putExtra("address",str[p]);
                                        context.getApplicationContext().sendBroadcast(i);
                                        str2[p] = "连接";
                                        useBtn.setText("正在断开");
                                    }
                                }
                                if(useBtn.getText().equals("断开连接")){
                                    Intent i = new Intent(BleUUID.CHAIR_CONTROL);
                                    i.putExtra("disconnect","disconnect");
                                    i.putExtra("address",str[p]);
                                    context.getApplicationContext().sendBroadcast(i);
                                    str2[p] = "连接";
                                    useBtn.setText("正在断开");
//                                useBtn.setTextColor(context.getResources().getColor(R.color.colorWhite));
                                }
                            }else {
                                mBluetoothAdapter.enable();
                            }
                    }

                });
                view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(0,0,0,"断开连接").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                mainActivity. vibrator.vibrate(60);
                                Intent i = new Intent(BleUUID.CHAIR_CONTROL);
                                i.putExtra("disconnect","disconnect");
                                i.putExtra("address",str[p]);
                                context.getApplicationContext().sendBroadcast(i);
                                useBtn.setText("连接");
                                str2[p] = "连接";
                                return true;
                            }
                        });
                    }
                });
                forthReceiver =new ForthReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BleUUID.CONNECT);
                context.getApplicationContext().registerReceiver(forthReceiver,intentFilter);
//                mainActivity = (MainActivity)getActivity();
                return view;
            }

        };

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.getApplicationContext().unregisterReceiver(forthReceiver);
    }

    public class  ForthReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String state= intent.getStringExtra("state");
            int states = Integer.valueOf(state);
            String uuid = intent.getStringExtra("uuid");

            if(action.equals(BleUUID.CONNECT)){
                if(uuid!=null){
                    switch (uuid){
                        case BleUUID.CHAIR_ADDRESS:
                            bluetoothState(0,states);
                            break;
                        case BleUUID.RADAR_ADDRESS:
                            bluetoothState(1,states);
                            break;
                        case BleUUID.CUSHION_ADDRESS:
                            bluetoothState(2,states);
                            break;
                        default:break;
                    }

                }

            }



        }
    }
    public void bluetoothState(int i,int state){
      if(state==0) {
          str2[i] = "连接";
      }

        if(state==2){
            str2[i]="断开连接";

        }

        addData();
        simpleAdapter.notifyDataSetChanged();
    }

    public void updateData(){

        if( BleService.hashMap.get(BleUUID.CHAIR_ADDRESS)!=null){
            bluetoothState(0,BleService.hashMap.get(BleUUID.CHAIR_ADDRESS).getState());

        }
        if(BleService.hashMap.get(BleUUID.RADAR_ADDRESS)!=null){
            bluetoothState(1,BleService.hashMap.get(BleUUID.RADAR_ADDRESS).getState());


        }
        if(BleService.hashMap.get(BleUUID.CUSHION_ADDRESS)!=null){
            bluetoothState(2,BleService.hashMap.get(BleUUID.CUSHION_ADDRESS).getState());
        }

    }



    public  void addData() {
        list.clear();
        for (int i = 0; i < 3; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_name", str3[i]);
            hashMap.put("user_id", str[i]);
            hashMap.put("connect", str2[i]);
            list.add(hashMap);
        }
    }

}
