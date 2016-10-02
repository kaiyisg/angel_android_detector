package electrical.nus.com.angeldetector.adapter;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by Lee Han Young on 02-Oct-16.
 */
public class GattCharacteristicsItem implements Comparable<GattCharacteristicsItem> {
    BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    String mItemKey;
    String mItemName;

    public GattCharacteristicsItem(String itemName, String itemKey, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        mItemKey = itemKey;
        mItemName = itemName;
        mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    public String getItemKey() {
        return mItemKey;
    }


    public String getItemName() {
        return mItemName;
    }


    public BluetoothGattCharacteristic getmBluetoothGattCharacteristic() {
        return mBluetoothGattCharacteristic;
    }

    @Override
    public int compareTo(GattCharacteristicsItem another) {
        int nameCmp = mItemName.compareTo(another.getItemName());
        return (nameCmp != 0 ? nameCmp : mItemKey.compareTo(another.getItemKey()));
    }
}