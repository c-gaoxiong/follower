package com.example.gaoxiong.follower;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class BleActivity extends ListActivity implements PermissionInterface {
    public BluetoothDevice mBluetoothDevice;
    private Button button;
    UUID Uuid;

//    private BService bleService;
    //设备扫描
    private PermissionHelper mPermissionHelper;
    StartScan scan = StartScan.getInstance();

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

//        启动服务
////        初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();


        button = (Button) findViewById(R.id.mButton);
        button.setOnClickListener(new ButtonListener());
        addDeviceToList();
        scan.scanDevice(true);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Ble Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
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

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction0());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction0());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction0() {
        Thing object = new Thing.Builder()
                .setName("Ble Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Logger.e("开始扫描");
            scan.scanDevice(true);
            addDeviceToList();
        }
    }

    private void addDeviceToList() {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (Iterator iterator = scan.devices.iterator(); iterator.hasNext(); ) {
            mBluetoothDevice = (BluetoothDevice) iterator.next();
            HashMap<String, String> map1 = new HashMap<String, String>();

            map1.put("user_name", mBluetoothDevice.getName());
            map1.put("user_id", mBluetoothDevice.getAddress());
            list.add(map1);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.user,
                new String[]{"user_name", "user_id"}, new int[]{R.id.user_name, R.id.user_id});
        setListAdapter(simpleAdapter);
    }

    //启动蓝牙服务
//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            bleService = ((BService.LocalBinder) iBinder).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            unbindService(serviceConnection);
//        }
//    };

    private  String Mac;

    //对点击的ListView进行监听
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        BluetoothDevice bluetoothDevice = scan.devices.get(position);
        Mac = bluetoothDevice.getAddress();
//        bleService.disConnect();
//        bleService.refreshDeviceCache(mBluetoothGatts.get(position));
//        bleService.initialize();
//        bleService.connect(bluetoothDevice,Mac);
//        bleService.sendOrder("a");

        Intent intent =new Intent("android.ble.chair.control");
        intent.putExtra("address",Mac.toString());
        sendBroadcast(intent);

        Logger.e("点击地址>>>>>>>：" + Mac);

    }






}
