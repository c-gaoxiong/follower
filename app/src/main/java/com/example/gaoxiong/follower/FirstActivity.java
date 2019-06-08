package com.example.gaoxiong.follower;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;




public  class FirstActivity extends AppCompatActivity implements PermissionInterface{





    Button stopButton ;
    Button frontButton ;
    Button backButton ;
    Button leftButton ;
    Button rightButton ;
    Button startButton ;
    Button overButton;
    private PermissionHelper mPermissionHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
         stopButton = (Button)findViewById(R.id.stopButton);
         frontButton = (Button)findViewById(R.id.frontButton);
         backButton = (Button)findViewById(R.id.backButton);
         leftButton = (Button)findViewById(R.id.leftButton);
         rightButton = (Button)findViewById(R.id.rightButton);
         startButton = (Button)findViewById(R.id.startButton);
         overButton = (Button)findViewById(R.id.overButton);
        stopButton.setOnClickListener(clickListener );
        frontButton.setOnClickListener(clickListener );
        backButton.setOnClickListener(clickListener );
        leftButton.setOnClickListener(clickListener );
        rightButton.setOnClickListener(clickListener );
        startButton.setOnClickListener(clickListener );
        overButton.setOnClickListener(clickListener );
        Intent intent = new Intent(FirstActivity.this, BleService.class);
        startService(intent);


    }



    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.overButton :
                    String v1="c";
                    Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("c");
                    intent0.putExtra("control",v1);
                    sendBroadcast(intent0);
                    break;
                case R.id.startButton:
                    String a="a";
                    Logger.e("a");
                    Intent intent1 = new Intent(BleUUID.CHAIR_CONTROL);
                    intent1.putExtra("control",a);
                    sendBroadcast(intent1);
                    break;
                case R.id.stopButton:
                    String s="s";
                    Logger.e("s");
                    Intent intent2= new Intent(BleUUID.CHAIR_CONTROL);
                    intent2.putExtra("control",s);
                    sendBroadcast(intent2);
                    break;
                case R.id.leftButton:
                    String l="l";
                    Logger.e("l");
                    Intent intent3 = new Intent(BleUUID.CHAIR_CONTROL);
                    intent3.putExtra("control",l);
                    sendBroadcast(intent3);
                    break;
                case R.id.rightButton:
                    String r="r";
                    Intent intent4 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("r");

                    intent4.putExtra("control",r);
                    sendBroadcast(intent4);
                    break;
                case R.id.frontButton:
                    String f="f";
                    Intent intent5 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("f");
                    intent5.putExtra("control",f);
                    sendBroadcast(intent5);
                    break;
                case R.id.backButton:
                    String b="b";
                    Intent intent7 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("b");
                    intent7.putExtra("control",b);
                    sendBroadcast(intent7);
                    break;
                default:break;
            }
        }

    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(FirstActivity.this,BleActivity.class);
            FirstActivity.this.startActivity(intent);
        }
        if(id == R.id.action_connect){
            Intent intent = new Intent(FirstActivity.this,ConnectActivity.class);
            FirstActivity.this.startActivity(intent);
        }
        if(id==R.id.action_gravity){
            Intent intent = new Intent(FirstActivity.this,GravityActivity.class);
            FirstActivity.this.startActivity(intent);

        }
        if(id==R.id.action_quit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(FirstActivity.this, BleService.class);
        stopService(intent);
        super.onDestroy();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public int getPermissionsRequestCode() {
        return 1000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
//权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
    }
    private void initViews() {
        //已经拥有所需权限，可以放心操作任何东西了
    }



}
