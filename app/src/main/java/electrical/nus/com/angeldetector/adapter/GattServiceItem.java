package electrical.nus.com.angeldetector.adapter;

import android.bluetooth.BluetoothGattService;

/**
 * Created by Lee Han Young on 02-Oct-16.
 */
public class GattServiceItem implements Comparable<GattServiceItem> {

    BluetoothGattService mBluetoothGattService;
    String mItemKey;
    String mItemName;

    public GattServiceItem(String itemName, String itemKey, BluetoothGattService bluetoothGattService) {
        mItemKey = itemKey;
        mItemName = itemName;
        mBluetoothGattService = bluetoothGattService;
    }

    public String getItemKey() {
        return mItemKey;
    }


    public String getItemName() {
        return mItemName;
    }


    public BluetoothGattService getmBluetoothGattService() {
        return mBluetoothGattService;
    }

    @Override
    public String toString() {
        return mItemName;
    }

    @Override
    public int compareTo(GattServiceItem another) {
        int nameCmp = mItemName.compareTo(another.getItemName());
        return (nameCmp != 0 ? nameCmp : mItemKey.compareTo(another.getItemKey()));
    }
}
