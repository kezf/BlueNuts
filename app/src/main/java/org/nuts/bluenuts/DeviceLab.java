package org.nuts.bluenuts;

import android.content.Context;

import org.nuts.jmiser.bluetooth.ibridge.BluetoothIBridgeDevice;

import java.util.ArrayList;

/**
 * Created by Centerm on 16/1/18.
 */
public class DeviceLab {
    private ArrayList<BluetoothIBridgeDevice> mFoundDevices;
    private ArrayList<BluetoothIBridgeDevice> mSelectedDevices;
    private static DeviceLab sDeviceLab;
    private Context mContext;

    private DeviceLab(Context context) {
        mContext = context;
        mFoundDevices = new ArrayList<BluetoothIBridgeDevice>();
        mSelectedDevices = new ArrayList<BluetoothIBridgeDevice>();
    }

    public static DeviceLab get(Context c) {
        if (sDeviceLab == null) {
            sDeviceLab = new DeviceLab(c.getApplicationContext());
        }
        return sDeviceLab;
    }

    public ArrayList<BluetoothIBridgeDevice> getFoundDevices() {
        return mFoundDevices;
    }

    public ArrayList<BluetoothIBridgeDevice> getSelectedDevices() {
        return mSelectedDevices;
    }
}
