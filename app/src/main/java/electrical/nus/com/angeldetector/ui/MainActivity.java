package electrical.nus.com.angeldetector.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
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
import android.os.Handler;
import java.util.logging.LogRecord;

import electrical.nus.com.angeldetector.R;
import electrical.nus.com.angeldetector.Utils.UserInteraction;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceItem;
import electrical.nus.com.angeldetector.adapter.GattCharacteristicsAdapter;
import electrical.nus.com.angeldetector.adapter.GattCharacteristicsItem;
import electrical.nus.com.angeldetector.adapter.GattServiceAdapter;
import electrical.nus.com.angeldetector.adapter.GattServiceItem;
import electrical.nus.com.angeldetector.services.BluetoothLeService;
import electrical.nus.com.angeldetector.services.SrvDictionary;

public class MainActivity extends AppCompatActivity {

    TextView dataTextView;
    TextView bleaddressTextView;
    TextView gattStatusTextview;
    TextView displayDataSource;
    BluetoothDevice bluetoothDevice;
    ListView gattServicesListView;
    GattServiceAdapter mGattServiceAdapter;
    SrvDictionary srvDictionary;
    LinearLayout gattServicesView;
    LinearLayout gattCharacteristicsView;
    GattCharacteristicsAdapter mGattCharacteristicsAdapter;
    ListView gattCharacteristicsListView;
    Context context;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private Handler mHandler;
    private Runnable mPeriodicReader;
    private String source = "";

    private static final int GATT_STATE_DISCONNECTED = 0;
    private static final int GATT_STATE_CONNECTED = 1;
    private static final int GATT_STATE_DISCOVERED_SERVICES = 2;
    private static final int GATT_STATE_NO_DISCOVERED_SERVICES = 3;
    public static final int GATT_STATE_DATA_AVAILABLE = 4;
    private int gattState = GATT_STATE_DISCONNECTED;

    private final static String TAG = MainActivity.class.getSimpleName();

    private void setViewState(int state) {
        if (state == GATT_STATE_DISCONNECTED) {
            gattState = GATT_STATE_DISCONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_connection_not_connected);
                    gattServicesView.setVisibility(View.GONE);
                    gattCharacteristicsView.setVisibility(View.GONE);
                }
            });
        } else if (state == GATT_STATE_CONNECTED) {
            gattState = GATT_STATE_CONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_connection_connected);
                    gattServicesView.setVisibility(View.GONE);
                    gattCharacteristicsView.setVisibility(View.GONE);
                }
            });
        } else if (state == GATT_STATE_DISCOVERED_SERVICES) {
            gattState = GATT_STATE_DISCOVERED_SERVICES;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_discovered_gatt_services);
                    gattServicesView.setVisibility(View.VISIBLE);
                    gattCharacteristicsView.setVisibility(View.VISIBLE);
                }
            });
        } else if (state == GATT_STATE_NO_DISCOVERED_SERVICES) {
            gattState = GATT_STATE_NO_DISCOVERED_SERVICES;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_no_discovered_gatt_services);
                    gattServicesView.setVisibility(View.GONE);
                    gattCharacteristicsView.setVisibility(View.GONE);
                }
            });
        } else if (state == GATT_STATE_DATA_AVAILABLE) {
            gattState = GATT_STATE_DATA_AVAILABLE;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(bluetoothDevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                setViewState(GATT_STATE_CONNECTED);
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                setViewState(GATT_STATE_DISCONNECTED);
                //invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                setViewState(GATT_STATE_DISCOVERED_SERVICES);
                for (BluetoothGattService service : mBluetoothLeService.getSupportedGattServices()) {
                    GattServiceItem newService = new GattServiceItem(
                            srvDictionary.map.get(service.getUuid()),
                            service.getUuid().toString(),
                            service);
                    mGattServiceAdapter.add(newService);
                    mGattServiceAdapter.addItem(newService);
                    mGattServiceAdapter.notifyDataSetChanged();
                }
                gattServicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                        GattServiceItem serviceItem = mGattServiceAdapter.getItem(position);
                        BluetoothGattService gattService = serviceItem.getmBluetoothGattService();
                        gattCharacteristicsView.setVisibility(View.VISIBLE);
                        Assert.assertTrue(gattService != null);

                        mGattCharacteristicsAdapter.clear();
                        mGattCharacteristicsAdapter.clearAdapter();
                        mGattCharacteristicsAdapter.notifyDataSetChanged();
                        //gattCharacteristicsListView.setAdapter(mGattCharacteristicsAdapter);
                        for (BluetoothGattCharacteristic charac : gattService.getCharacteristics()) {
                            GattCharacteristicsItem btchar = new GattCharacteristicsItem(
                                    serviceItem.getItemName(),
                                    charac.getUuid().toString(),
                                    charac);
                            mGattCharacteristicsAdapter.add(btchar);
                            mGattCharacteristicsAdapter.addItem(btchar);
                        }
                        mGattCharacteristicsAdapter.notifyDataSetChanged();
                        gattCharacteristicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                GattCharacteristicsItem gattCharacteristicsItem = mGattCharacteristicsAdapter.getItem(position);
                                BluetoothGattCharacteristic characteristic = gattCharacteristicsItem.getmBluetoothGattCharacteristic();
                                int charaProp = characteristic.getProperties();

                                //(charaProp)& 10000 or charaProp & 100000
                                if(((charaProp & 16) > 0 || (charaProp & 32) > 0)) {
                                    mBluetoothLeService.setCharacteristicNotification(characteristic,true);
                                    source = characteristic.getUuid().toString();
                                    displayDataSource.setText(source);
                                }else{
                                    UserInteraction.showAlert(characteristic.getUuid().toString(),"Cannot Write ENABLE_NOTIFICATION_VALUE",context);
                                }

                                /*if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                                    // If there is an active notification on a characteristic, clear
                                    // it first so it doesn't update the data field on the user interface.
                                    if (mNotifyCharacteristic != null) {
                                        mBluetoothLeService.setCharacteristicNotification(
                                                mNotifyCharacteristic, false);
                                        mNotifyCharacteristic = null;
                                    }
                                    mBluetoothLeService.readCharacteristic(characteristic);
                                }
                                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                    mNotifyCharacteristic = characteristic;
                                    mBluetoothLeService.setCharacteristicNotification(
                                            characteristic, true);
                                }*/
                            }
                        });
                    }
                });


            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                setViewState(GATT_STATE_DATA_AVAILABLE);
                dataTextView.setText(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                /*mHandler.post(new Runnable() {
                    public void run() {
                        //bleCharacteristic.onCharacteristicChanged();
                    }
                });*/
                //UserInteraction.showAlert("new data available", "data avail", context);
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    /*
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
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SrvDictionary.CLIENT_CHARACTERISTIC_CONFIG);
                            //byte[] NOTIFY_AND_INDICATE = new byte[]{(byte)3, (byte)0};
                            //descriptor.setValue(enabled?NOTIFY_AND_INDICATE:BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            //this.mWaitingForConfirmation = true;
                            if(!bluetoothGatt.writeDescriptor(descriptor)) {
                                throw new AssertionError("Failed to write BLE descriptor " + descriptor.getUuid() + " for UUID " + characteristic.getUuid());
                            }
                        }
                    }else{
                        UserInteraction.showAlert("Non-readable Bluetooth Property", String.valueOf(properties),context);
                    }
                }
            }
        });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_main);
        bleaddressTextView = (TextView) findViewById(R.id.bleaddressTextView);
        gattStatusTextview = (TextView) findViewById(R.id.gattStatusTextview);
        gattServicesListView = (ListView) findViewById(R.id.gattServicesListView);
        gattServicesView = (LinearLayout) findViewById(R.id.gattServicesView);
        gattCharacteristicsView = (LinearLayout) findViewById(R.id.gattCharacteristicsView);
        gattCharacteristicsListView = (ListView) findViewById(R.id.gattCharacteristicsListView);
        dataTextView = (TextView)findViewById(R.id.dataTextView);
        displayDataSource = (TextView)findViewById(R.id.displayDataSource);

        mGattServiceAdapter = new GattServiceAdapter(this, R.layout.list_item);
        gattServicesListView.setAdapter(mGattServiceAdapter);
        srvDictionary = new SrvDictionary();

        mGattCharacteristicsAdapter = new GattCharacteristicsAdapter(this, R.layout.list_item);
        gattCharacteristicsListView.setAdapter(mGattCharacteristicsAdapter);
        gattCharacteristicsView.setVisibility(View.GONE);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        Bundle extras = getIntent().getExtras();
        assert (extras != null);
        bluetoothDevice = (BluetoothDevice) extras.getParcelable("bluetooth_device");
        bleaddressTextView.setText(bluetoothDevice.getAddress());

        setViewState(GATT_STATE_DISCONNECTED);

        mHandler = new Handler(this.getMainLooper());

        mPeriodicReader = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(mPeriodicReader, 1000);
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(bluetoothDevice.getAddress());
            Log.d(TAG, "Connect request result=" + result);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mBluetoothLeService.close();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /*
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            bluetoothGatt = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setViewState(GATT_STATE_DISCOVERED_SERVICES);
                final List<BluetoothGattService> services = bluetoothGatt.getServices();
                *//*for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                }*//*
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


        *//*public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //super.onCharacteristicChanged(gatt, characteristic);
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.i(TAG, characteristic.toString());
        }*//*

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
    };*/
}
