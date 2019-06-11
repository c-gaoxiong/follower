package com.example.gaoxiong.follower;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gaoxiong.follower.myFragment.MyFragmentPagerAdapter;
import com.orhanobut.logger.Logger;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;


/**
 * Created by Coder-pig on 2015/8/28 0028.
 */
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener,PermissionInterface {
    public StartScan startScan;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,

        };
    }

    @Override
    public int getPermissionsRequestCode() {
        return 100;
    }

    @Override
    public void requestPermissionsSuccess() {
//权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {

    }
    private void initViews() {
        //已经拥有所需权限，可以放心操作任何东西了
        //初始化并发起权限申请


//        if ( !mBluetoothAdapter.isEnabled()) {
//            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.enable();///　/*隐式打开蓝牙*/
//            }
//
//        }
        Logger.d("进入>>>>>initView()");


    }

    //UI Objects
//    private TextView txt_topbar;
    private RadioGroup rg_tab_bar;
    private RadioButton rb_channel;
    private RadioButton rb_message;
    private RadioButton rb_better;
    private RadioButton rb_setting;
    public static ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    private PermissionHelper mPermissionHelper;
   public static BluetoothAdapter mBluetoothAdapter;
    BluetoothManager bluetoothManager;
    BleOpenedReceiver bleOpenedReceiver;

    //几个代表页面的常量
    private int REQUEST_ENABLE_BT = 1;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;
    String[] str = new String[]{BleUUID.CHAIR_ADDRESS,BleUUID.RADAR_ADDRESS,BleUUID.CUSION_ADDRESS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

        if ( !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }else {
            init();
        }
        bleOpenedReceiver = new BleOpenedReceiver();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(ACTION_STATE_CHANGED);

        registerReceiver(bleOpenedReceiver,intentFilter2);

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_channel.setChecked(true);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // 蓝牙已经开启
                Logger.d("蓝牙已经开启完毕");


            } else {
                Toast.makeText(this,"请打开蓝牙",Toast.LENGTH_LONG).show();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);



    }

    void  init(){
       Logger.d("进入>>>>>init()");
       startScan =StartScan.getInstance();
       startScan.setmBluetoothAdapter(mBluetoothAdapter);
       startScan.scanDevice(true);
       Intent intent = new Intent(MainActivity.this, BleService.class);
       startService(intent);

       Intent intent2 = new Intent("android.ble.chair.control");
       for(int i =0;i < 3;i++){
           intent2.putExtra("address", str[i]);
           sendBroadcast(intent2);
           Logger.d("sendBroadcast>>>>>>"+str[i]);
       }




    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(bleOpenedReceiver);
        Intent intent = new Intent(MainActivity.this, BleService.class);
        stopService(intent);
        super.onDestroy();
    }

    private void bindViews() {
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_channel = (RadioButton) findViewById(R.id.rb_channel);
        rb_message = (RadioButton) findViewById(R.id.rb_message);
        rb_better = (RadioButton) findViewById(R.id.rb_better);
        rb_setting = (RadioButton) findViewById(R.id.rb_setting);
        rg_tab_bar.setOnCheckedChangeListener(this);

        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_channel:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_message:
                vpager.setCurrentItem(PAGE_TWO);
                break;
            case R.id.rb_better:
                vpager.setCurrentItem(PAGE_THREE);
                break;
            case R.id.rb_setting:
                vpager.setCurrentItem(PAGE_FOUR);
//                startScan.scanDevice(true);
                break;
        }
    }



    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_channel.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_message.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb_better.setChecked(true);
                    break;
                case PAGE_FOUR:
                    rb_setting.setChecked(true);
                    break;
            }
        }
    }
    public  ViewPager getViewPager(){
        return vpager;
    }

    public class  BleOpenedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:    // 蓝牙打开中
                    Logger.d("蓝牙打开中");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Logger.d(" 蓝牙打开完成");
                    // 蓝牙打开完成
                    init();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:  // 蓝牙关闭中

                    break;
                case BluetoothAdapter.STATE_OFF:          // 蓝牙关闭完成

                    break;
            }




        }
    }
}
