package electrical.nus.com.angeldetector.adapter;

/**
 * Created by Lee Han Young on 01-Oct-16.
 */

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceItem implements Comparable<BluetoothDeviceItem> {

    private final String mItemKey;
    private final String mItemName;
    private final BluetoothDevice mBluetoothDevice;


    public BluetoothDeviceItem(String itemName, String itemKey, BluetoothDevice bluetoothDevice) {
        mItemKey = itemKey;
        mItemName = itemName;
        mBluetoothDevice = bluetoothDevice;
    }


    public String getItemKey() {
        return mItemKey;
    }


    public String getItemName() {
        return mItemName;
    }


    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }


    @Override
    public String toString() {
        return mItemName;
    }


    @Override
    public int compareTo(BluetoothDeviceItem item) {
        int nameCmp = mItemName.compareTo(item.getItemName());
        return (nameCmp != 0 ? nameCmp : mItemKey.compareTo(item.getItemKey()));
    }
}
