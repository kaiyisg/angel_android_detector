/*
 * Copyright (c) 2015, Seraphim Sense Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package electrical.nus.com.angeldetector.angelsdk;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;


public class ChTerminalControlPoint extends BleCharacteristic<String> {
    public final static UUID CHARACTERISTIC_UUID = UUID.fromString("99a10dd8-d78f-4a57-8e95-ae584c34177e");


    public ChTerminalControlPoint(BluetoothGattCharacteristic gattCharacteristic,
                             BleDevice bleDevice) {
        super(CHARACTERISTIC_UUID, gattCharacteristic, bleDevice);
    }


    public ChTerminalControlPoint() {
        super(CHARACTERISTIC_UUID);
    }

    
    public void writeCommand(String command) {
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(command);
        getBleDevice().writeCharacteristic(c);
    }

    
    @Override
    protected String processCharacteristicValue() {
        BluetoothGattCharacteristic ch = getBaseGattCharacteristic();
        byte[] bytes = ch.getValue();
        
        return new String(bytes);
    }
}
