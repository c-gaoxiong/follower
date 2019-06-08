package com.example.gaoxiong.follower;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.UUID;


public class BleGattClass {
    private String uuid = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice bluetoothDevice = null;
    private BluetoothGatt bluetoothGatt = null;
//    private BluetoothGattCharacteristic bluetoothGattCharacteristic = null;
    private BluetoothManager bluetoothManager;
    private Context context;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristicWrite;
    private BluetoothGattDescriptor descriptor;
    private int state = 0;

    BleGattClass(Context context, String string) {
        this.uuid = string;
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(uuid);
    }

    void connectBluetooth() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (bluetoothGatt == null){
                    bluetoothGatt = bluetoothDevice.connectGatt(context, false, callback);
                    Logger.e("执行了连接的操作");
                }else {
                    bluetoothGatt.close();
                    bluetoothGatt = bluetoothDevice.connectGatt(context, false, callback);
                }

            }
        }).start();

//      if (bluetoothGatt == null)
//          bluetoothGatt = bluetoothDevice.connectGatt(context, false, callback);
//          Logger.e("执行了连接的操作");
    }

    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            state = newState;
            Handler handler = new Handler(Looper.getMainLooper());

            if (newState == BluetoothProfile.STATE_CONNECTING) {
                Logger.e("正在连接");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "正在连接:" + uuid, Toast.LENGTH_LONG).show();
                    }
                });
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "连接成功:" + uuid, Toast.LENGTH_LONG).show();
                    }
                });
                Logger.e("连接成功");

                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.e("断开连接的状态");
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "断开连接:" + uuid, Toast.LENGTH_LONG).show();
                    }
                });
//                bluetoothGatt.close();
//                connectBluetooth();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.d("onServicesDiscovered--" + "发现服务");
//                获取全部服务， gatt.getServices();
                List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
                Logger.d(bluetoothGattServices.size());
                Logger.d(gatt.getServices().get(0));
                Logger.d(gatt.getServices().get(1));
                Logger.d(gatt.getServices().get(2));
//获取对应的BleUUID.UUID_BLUE_SERVICE服务
                bluetoothGattService = gatt
                        .getService(BleUUID.UUID_BLUE_SERVICE);
                characteristicWrite = bluetoothGattService.getCharacteristic(BleUUID.UUID_BLUE_WRITE);
                setCharacteristicNotification(gatt.getServices().get(2).getCharacteristic(BleUUID.UUID_BLUE_WRITE), true);
//
                gatt.getService(UUID.randomUUID());
                for (BluetoothGattService bluetoothGattService :
                        bluetoothGattServices) {
                    // Logger.d("进入for 循环");

                    List<BluetoothGattCharacteristic>
                            bluetoothGattCharacteristics =
                            bluetoothGattService.getCharacteristics();
                    Logger.d("onServicesDiscovered--遍历特征值=" + bluetoothGattCharacteristics);
                    UUID uuid = bluetoothGattService.getUuid();
                    Logger.d("onServicesDiscovered--服务的uuid=" + uuid);//服务

                 /*获取指定服务uuid的特征值*/

//
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic
                            : bluetoothGattCharacteristics) {
                        setCharacteristicNotification(bluetoothGattCharacteristic, true);
                        Logger.d("onServicesDiscovered--服务的特征值的uuid=" +
                                bluetoothGattCharacteristic.getUuid());

                        // Logger.d("onServicesDiscovered--指定服务uuid的特征值不为空=--");
                        final int charaProp =
                                bluetoothGattCharacteristic.getProperties();

                        //
//                 bluetoothGattCharacteristic.getWriteType()==BluetoothGattCharacteristic.PROPERTY_READ;
                 /*如果该字符串可读*/
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) >
                                0) {
                            Logger.d("onServicesDiscovered--字符串可读--");
                            byte[] value = new byte[20];
                            bluetoothGattCharacteristic.setValue(value[0],
                                    BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                            String writeBytes = "a";
                            bluetoothGattCharacteristic.setValue(writeBytes.getBytes());
                        }
                        if
                                (gatt.setCharacteristicNotification(bluetoothGattCharacteristic,
                                true)) {
                            Logger.d("onServicesDiscovered--设置通知成功=--" + uuid);
                        }
                 /*3.再从指定的Characteristic中，我们可以通过getDescriptor()方法来获取该特征所包含的descriptor
                 以上的BluetoothGattService、BluetoothGattCharacteristic、BluetoothGattDescriptor。
                 我们都可以通过其getUuid()方法，来获取其对应的Uuid，从而判断是否是自己需要的。*/
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors =
                                bluetoothGattCharacteristic.getDescriptors();
                        Logger.d("onServicesDiscovered--遍历Descriptor=");
                        for (BluetoothGattDescriptor bluetoothGattDescriptor :
                                bluetoothGattDescriptors) {
                            Logger.d("onServicesDiscovered--Descriptor的uuid=" +
                                    bluetoothGattDescriptor.getUuid());
                            // bluetoothGattDescriptor.getValue();

                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.d("onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_RECEIVED, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdate(BleUUID.RECEIVED, characteristic);
            Logger.d("onCharacteristicChanged" + new String(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,  BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Logger.d("onCharacteristicWrite" + characteristic.getValue().toString());

        }

    };

    private void broadcastUpdate(String received, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(received);
        intent.putExtra("data", new String(characteristic.getValue()));
        context.sendBroadcast(intent);

    }

    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        Logger.d("setCharacteristicNotification", "setCharacteristicNotification");
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        descriptor = characteristic
                .getDescriptor(BleUUID.UUID_NOTIFY);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public String getAddress() {
        return uuid;
    }

    public int getState() {
        return state;
    }

    public void sendOrder(final String strValue) {
        Logger.d("进入sendOrder");
        if (bluetoothGatt != null && characteristicWrite != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    characteristicWrite.setValue(strValue.getBytes());
                    bluetoothGatt.writeCharacteristic(characteristicWrite);
                }
            }).start();
        } else {
            Logger.d("找不到对应的通道，找不到对应写入的特征");
        }
    }
}
