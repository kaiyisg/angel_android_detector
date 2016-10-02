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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.Assert;

import electrical.nus.com.angeldetector.R;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceAdapter;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceItem;

public class ScanningActivity extends AppCompatActivity {

    private static final int IDLE = 0;  //on but not scanning
    private static final int SCANNING = 1;
    private static final int FOUND = 2;
    private static final int OFF = 3;
    private static final int NOTFOUND = 4;
    private int viewState = OFF;
    private static final String bluetoothTag = "bluetooth device";

    private final static int REQUEST_ENABLE_BT = 1;
    private ListView bluetoothDeviceListView;
    BluetoothAdapter mBluetoothAdapter;
    private BluetoothDeviceAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private Handler mHandler;

    Button bluetoothScanningButton;
    Button bluetoothButton;
    TextView bluetoothStatusTextView;
    TextView bluetoothScanningTextView;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private void setViewState(int viewState){
        if(viewState==OFF){
            bluetoothButton.setEnabled(true);
            bluetoothStatusTextView.setText(getResources().getString(R.string.status_bluetooth_off));
            bluetoothScanningButton.setVisibility(View.GONE);
            bluetoothScanningTextView.setVisibility(View.GONE);
            bluetoothButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            });
        }
        else if(viewState==IDLE){
            bluetoothButton.setEnabled(false);
            bluetoothStatusTextView.setText(getResources().getString(R.string.status_bluetooth_on));
            bluetoothScanningButton.setVisibility(View.VISIBLE);
            bluetoothScanningButton.setText(getResources().getString(R.string.button_start_scanning));
            bluetoothScanningButton.setEnabled(true);
            bluetoothScanningTextView.setVisibility(View.VISIBLE);
            bluetoothScanningButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanLeDevice(true);
                    setViewState(SCANNING);
                }
            });
        }
        else if(viewState==SCANNING){
            bluetoothButton.setEnabled(false);
            bluetoothStatusTextView.setText(getResources().getString(R.string.status_bluetooth_on));
            bluetoothScanningButton.setVisibility(View.VISIBLE);
            bluetoothScanningTextView.setText(getResources().getString(R.string.status_bluetooth_not_scanning));
            bluetoothScanningTextView.setVisibility(View.VISIBLE);
            bluetoothScanningButton.setEnabled(false);
        }
        else if(viewState==FOUND){
            bluetoothButton.setEnabled(false);
            bluetoothStatusTextView.setText(getResources().getString(R.string.status_bluetooth_on));
            bluetoothScanningButton.setVisibility(View.VISIBLE);
            bluetoothScanningTextView.setText(getResources().getString(R.string.status_bluetooth_devices_found));
            bluetoothScanningTextView.setVisibility(View.VISIBLE);
            bluetoothScanningButton.setEnabled(false);
        }
        else if(viewState==NOTFOUND){
            bluetoothButton.setEnabled(false);
            bluetoothStatusTextView.setText(getResources().getString(R.string.status_bluetooth_on));
            bluetoothScanningButton.setVisibility(View.VISIBLE);
            bluetoothScanningTextView.setText(getResources().getString(R.string.status_bluetooth_devices_not_found));
            bluetoothScanningTextView.setVisibility(View.VISIBLE);
            bluetoothScanningButton.setEnabled(false);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        bluetoothScanningButton = (Button)findViewById(R.id.bluetoothScanningButton);
        bluetoothButton = (Button)findViewById(R.id.bluetoothButton);
        bluetoothStatusTextView = (TextView)findViewById(R.id.bluetoothStatusTextView);
        bluetoothScanningTextView = (TextView)findViewById(R.id.bluetoothScanningTextView);
        bluetoothDeviceListView = (ListView)findViewById(R.id.listView);

        mHandler=new Handler();
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
                intent.putExtra("bluetooth_device", bluetoothDevice);
                //intent.putExtra("ble_device_address", bluetoothDevice.getAddress());
                startActivity(intent);
            }
        });

        //initializing bluetooth manager
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onResume(){
        super.onResume();

        //checking if bluetooth is on or not
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            setViewState(OFF);
        }else{
            setViewState(IDLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                setViewState(IDLE);
            }
        }
    }

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
                            if (device.getName() != null && device.getName().startsWith("Angel")) {
                                setViewState(FOUND);
                                BluetoothDeviceItem newDevice = new BluetoothDeviceItem(device.getName(), device.getAddress(), device);
                                mLeDeviceListAdapter.add(newDevice);
                                mLeDeviceListAdapter.addItem(newDevice);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }else{
                                setViewState(NOTFOUND);
                            }
                            /*BluetoothDeviceItem newDevice = new BluetoothDeviceItem(device.getName(), device.getAddress(), device);
                            mLeDeviceListAdapter.add(newDevice);
                            mLeDeviceListAdapter.addItem(newDevice);
                            mLeDeviceListAdapter.notifyDataSetChanged();*/
                        }
                    });
                }
            };
}
