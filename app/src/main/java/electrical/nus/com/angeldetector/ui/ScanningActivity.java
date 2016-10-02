package electrical.nus.com.angeldetector.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import junit.framework.Assert;

import electrical.nus.com.angeldetector.R;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceAdapter;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceItem;

public class ScanningActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private ListView bluetoothDeviceListView;
    BluetoothAdapter mBluetoothAdapter;
    private BluetoothDeviceAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final String bluetoothTag = "bluetooth device";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);


        bluetoothDeviceListView = (ListView)findViewById(R.id.listView);
        if(this.mLeDeviceListAdapter==null){
            mLeDeviceListAdapter = new BluetoothDeviceAdapter(this,R.layout.list_item);
        }
        bluetoothDeviceListView.setAdapter(mLeDeviceListAdapter);
        bluetoothDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                BluetoothDevice bluetoothDevice = mLeDeviceListAdapter.getItem(position).getBluetoothDevice();
                Assert.assertTrue(bluetoothDevice != null);
                Intent intent = new Intent(parent.getContext(), MainActivity.class);
                intent.putExtra("ble_device_address", bluetoothDevice.getAddress());
                startActivity(intent);
            }
        });

        //initializing bluetooth manager
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mHandler=new Handler();

    }

    @Override
    protected void onResume(){
        super.onResume();
        //checking if bluetooth is on or not
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            scanLeDevice(true);
        }
    }

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*if (device.getName() != null && device.getName().startsWith("Angel")) {
                                BluetoothDeviceItem newDevice = new BluetoothDeviceItem(device.getName(), device.getAddress(), device);
                                mLeDeviceListAdapter.add(newDevice);
                                mLeDeviceListAdapter.addItem(newDevice);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }*/
                            BluetoothDeviceItem newDevice = new BluetoothDeviceItem(device.getName(), device.getAddress(), device);
                            mLeDeviceListAdapter.add(newDevice);
                            mLeDeviceListAdapter.addItem(newDevice);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
}
