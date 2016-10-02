package electrical.nus.com.angeldetector.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.List;
import java.util.UUID;

import electrical.nus.com.angeldetector.R;
import electrical.nus.com.angeldetector.Utils.UserInteraction;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceItem;
import electrical.nus.com.angeldetector.adapter.GattCharacteristicsAdapter;
import electrical.nus.com.angeldetector.adapter.GattCharacteristicsItem;
import electrical.nus.com.angeldetector.adapter.GattServiceAdapter;
import electrical.nus.com.angeldetector.adapter.GattServiceItem;
import electrical.nus.com.angeldetector.services.SrvDictionary;

public class MainActivity extends AppCompatActivity {

    TextView bleaddressTextView;
    TextView gattStatusTextview;
    BluetoothDevice bluetoothDevice;
    ListView gattServicesListView;
    GattServiceAdapter mGattServiceAdapter;
    SrvDictionary srvDictionary;
    LinearLayout gattServicesView;
    LinearLayout gattCharacteristicsView;
    BluetoothGatt bluetoothGatt;
    GattCharacteristicsAdapter mGattCharacteristicsAdapter;
    ListView gattCharacteristicsListView;
    Context context;

    private static final int GATT_STATE_DISCONNECTED = 0;
    private static final int GATT_STATE_CONNECTED = 1;
    private static final int GATT_STATE_DISCOVERED_SERVICES = 2;
    private static final int GATT_STATE_NO_DISCOVERED_SERVICES = 3;
    private int gattState = GATT_STATE_DISCONNECTED;

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_main);
        bleaddressTextView = (TextView)findViewById(R.id.bleaddressTextView);
        gattStatusTextview = (TextView)findViewById(R.id.gattStatusTextview);
        gattServicesListView = (ListView)findViewById(R.id.gattServicesListView);
        gattServicesView = (LinearLayout)findViewById(R.id.gattServicesView);
        gattCharacteristicsView = (LinearLayout)findViewById(R.id.gattCharacteristicsView);
        gattCharacteristicsListView = (ListView)findViewById(R.id.gattCharacteristicsListView);

        mGattServiceAdapter = new GattServiceAdapter(this,R.layout.list_item);
        gattServicesListView.setAdapter(mGattServiceAdapter);
        srvDictionary = new SrvDictionary();
        gattServicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                GattServiceItem serviceItem = mGattServiceAdapter.getItem(position);
                BluetoothGattService gattService= serviceItem.getmBluetoothGattService();
                gattCharacteristicsView.setVisibility(View.VISIBLE);
                Assert.assertTrue(gattService != null);

                mGattCharacteristicsAdapter.clear();
                mGattCharacteristicsAdapter.clearAdapter();
                mGattCharacteristicsAdapter.notifyDataSetChanged();
                //gattCharacteristicsListView.setAdapter(mGattCharacteristicsAdapter);
                for(BluetoothGattCharacteristic charac:gattService.getCharacteristics()){
                    GattCharacteristicsItem btchar = new GattCharacteristicsItem(
                            serviceItem.getItemName(),
                            charac.getUuid().toString(),
                            charac);
                    mGattCharacteristicsAdapter.add(btchar);
                    mGattCharacteristicsAdapter.addItem(btchar);
                }
                mGattCharacteristicsAdapter.notifyDataSetChanged();
            }
        });

        mGattCharacteristicsAdapter = new GattCharacteristicsAdapter(this, R.layout.list_item);
        gattCharacteristicsListView.setAdapter(mGattCharacteristicsAdapter);
        gattCharacteristicsView.setVisibility(View.GONE);
        gattCharacteristicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GattCharacteristicsItem gattCharacteristicsItem = mGattCharacteristicsAdapter.getItem(position);
                BluetoothGattCharacteristic characteristic = gattCharacteristicsItem.getmBluetoothGattCharacteristic();
                int properties = characteristic.getProperties();

                if(bluetoothGatt != null) {
                    if(((properties & 16) > 0 || (properties & 32) > 0)) {
                        if(!bluetoothGatt.setCharacteristicNotification(characteristic, true)) {
                            throw new AssertionError("Failed setCharacteristicNotification for UUID " + characteristic.getUuid());
                        } else {
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                            //byte[] NOTIFY_AND_INDICATE = new byte[]{(byte)3, (byte)0};
                            //descriptor.setValue(enabled?NOTIFY_AND_INDICATE:BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            //this.mWaitingForConfirmation = true;
                            if(!bluetoothGatt.writeDescriptor(descriptor)) {
                                throw new AssertionError("Failed to write BLE descriptor " + descriptor.getUuid() + " for UUID " + characteristic.getUuid());
                            } /*else {
                                try {
                                    synchronized(this) {
                                        this.wait(5000L);
                                        *//*if(this.mWaitingForConfirmation) {
                                            throw new AssertionError("Did not receive confirmation for mBluetoothGatt.writeDescriptor(" + characteristic.getUuid() + ")");
                                        }*//*
                                    }
                                } catch (InterruptedException var8) {
                                    throw new AssertionError("Interrupted while waiting for response to mBluetoothGatt.writeDescriptor");
                                }
                            }*/
                        }
                    }else{
                        UserInteraction.showAlert("Non-readable Bluetooth Property", String.valueOf(properties),context);
                    }

                }


                //https://developer.android.com/reference/android/bluetooth/BluetoothGatt.html#setCharacteristicNotification(android.bluetooth.BluetoothGattCharacteristic, boolean)
                /*boolean enabled = bluetoothGatt.setCharacteristicNotification(
                        characteristic,
                        true);

                //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
                // and then call setValue on that descriptor
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));

                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);*/
            }
        });
    }

    private void setViewState(int state){
        if(state==GATT_STATE_DISCONNECTED){
            gattState=GATT_STATE_DISCONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_connection_not_connected);
                    gattServicesView.setVisibility(View.GONE);
                    gattCharacteristicsView.setVisibility(View.GONE);
                }
            });
        }else if(state==GATT_STATE_CONNECTED){
            gattState=GATT_STATE_CONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_connection_connected);
                    gattServicesView.setVisibility(View.GONE);
                    gattCharacteristicsView.setVisibility(View.GONE);
                }
            });
        }else if(state==GATT_STATE_DISCOVERED_SERVICES){
            gattState=GATT_STATE_DISCOVERED_SERVICES;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_discovered_gatt_services);
                    gattServicesView.setVisibility(View.VISIBLE);
                    gattCharacteristicsView.setVisibility(View.VISIBLE);
                }
            });
        }else if(state==GATT_STATE_NO_DISCOVERED_SERVICES){
            gattState=GATT_STATE_NO_DISCOVERED_SERVICES;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_no_discovered_gatt_services);
                    gattServicesView.setVisibility(View.GONE);
                    gattCharacteristicsView.setVisibility(View.GONE);
                }
            });
        }
    }

    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        assert(extras != null);
        bluetoothDevice = (BluetoothDevice)extras.getParcelable("bluetooth_device");
        bleaddressTextView.setText(bluetoothDevice.getAddress());

        setViewState(GATT_STATE_DISCONNECTED);

        connect(bluetoothDevice);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }

    }

    private void connect(BluetoothDevice bluetoothDevice){
        bluetoothDevice.connectGatt(this,false,mGattCallback);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            bluetoothGatt = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setViewState(GATT_STATE_DISCOVERED_SERVICES);
                final List<BluetoothGattService> services = bluetoothGatt.getServices();
                /*for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                }*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(BluetoothGattService service:services){
                            GattServiceItem newService = new GattServiceItem(
                                    srvDictionary.map.get(service.getUuid()),
                                    service.getUuid().toString(),
                                    service);
                            mGattServiceAdapter.add(newService);
                            mGattServiceAdapter.addItem(newService);
                            mGattServiceAdapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                setViewState(GATT_STATE_NO_DISCOVERED_SERVICES);
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            bluetoothGatt = gatt;
            if(newState== BluetoothProfile.STATE_CONNECTED){
                setViewState(GATT_STATE_CONNECTED);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        bluetoothGatt.discoverServices());
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gattState = GATT_STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }


        /*public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //super.onCharacteristicChanged(gatt, characteristic);
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.i(TAG, characteristic.toString());
        }*/

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            String serviceUuid = characteristic.getService().getUuid().toString();
            if(serviceUuid == null) {
                throw new AssertionError();
            } else {
                String charcteristicUuid = characteristic.getUuid().toString();
                if(charcteristicUuid == null) {
                    throw new AssertionError();
                } else {
                    //BleDevice.this.handleOnCharacteristicChanged(serviceUuid, charcteristicUuid);
                    //characteristic.
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == 0) {
                String serviceUuid = characteristic.getService().getUuid().toString();
                if(serviceUuid == null) {
                    throw new AssertionError();
                }

                String charcteristicUuid = characteristic.getUuid().toString();
                if(charcteristicUuid == null) {
                    throw new AssertionError();
                }
                //BleDevice.this.handleOnCharacteristicChanged(serviceUuid, charcteristicUuid);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(status==0){
                descriptor.getValue();
            }
        }
    };

}
