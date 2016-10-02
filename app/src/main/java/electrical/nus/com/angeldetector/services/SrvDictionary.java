package electrical.nus.com.angeldetector.services;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Lee Han Young on 02-Oct-16.
 */
public class SrvDictionary {

    public HashMap<UUID, String> map;

    //DEFINED IN BLUETOOTH PROTOCOL
    public static final UUID HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static final UUID HEALTH_THERMOMETER_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID GENERIC_ACCESS_SERVICE_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public static final UUID DEVICE_INFORMATION_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    //include device information service in the future

    //defined in angelsensor
    public static final UUID ALARM_CLOCK_SERVICE_UUID = UUID.fromString("7cd50edd-8bab-44ff-a8e8-82e19393af10");
    public static final UUID ACTIVITY_MONITORING_SERVICE_UUID = UUID.fromString("68b52738-4a04-40e1-8f83-337a29c3284d");
    public static final UUID SRV_TERMINAL_SERVICE_UUID = UUID.fromString("41e1bd6a-9e39-441c-9312-b6e862472480");
    public static final UUID WAVEFORM_SIGNAL_SERVICE_UUID = UUID.fromString("481d178c-10dd-11e4-b514-b2227cce2b54");
    public static final UUID HEALTH_JOURNAL_SERVICE_UUID = UUID.fromString("87ef07ff-4739-4527-b38f-b0e228de6ed3");
    public static final UUID BLOOD_OXYGEN_SERVICE_UUID = UUID.fromString("902dcf38-ccc0-4902-b22c-70cab5ee5df2");



    public SrvDictionary(){
        map = new HashMap<UUID , String>();
        map.put(HEART_RATE_SERVICE_UUID, "Bluetooth Standard: Heartrate Service");
        map.put(HEALTH_THERMOMETER_SERVICE_UUID, "Bluetooth Standard: Health thermometer Service");
        map.put(BATTERY_LEVEL_SERVICE_UUID, "Bluetooth Standard: Battery level Service");
        map.put(ALARM_CLOCK_SERVICE_UUID, "Angel Standard: Alarm clock Service");
        map.put(ACTIVITY_MONITORING_SERVICE_UUID, "Angel Standard: Activity monitoring Service");
        map.put(SRV_TERMINAL_SERVICE_UUID, "Angel Standard: srv terminal Service");
        map.put(WAVEFORM_SIGNAL_SERVICE_UUID, "Angel Standard: Waveform signal Service");
        map.put(HEALTH_JOURNAL_SERVICE_UUID,"Angel Standard: Health Journal Service");
        map.put(BLOOD_OXYGEN_SERVICE_UUID,"Angel Standard: Blood Oxygen Service");
        map.put(GENERIC_ACCESS_SERVICE_UUID,"Bluetooth Standard: Generic Access");
        map.put(DEVICE_INFORMATION_SERVICE_UUID,"Bluetooth Standard: Device Information");
    }
}
