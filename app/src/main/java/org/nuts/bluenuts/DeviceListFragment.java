package org.nuts.bluenuts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.nuts.jmiser.bluetooth.ibridge.BluetoothIBridgeAdapter;
import org.nuts.jmiser.bluetooth.ibridge.BluetoothIBridgeDevice;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DeviceListFragment extends ListFragment implements BluetoothIBridgeAdapter.DiscoveryReceiver {
    private BluetoothIBridgeAdapter mAdapter;
    private ArrayList<BluetoothIBridgeDevice> mFoundDevices;
    private ArrayList<BluetoothIBridgeDevice> mSelectedDevices;
    private int mConnectedDeviceMax;
    private DeviceAdapter mDeviceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mAdapter = BluetoothIBridgeAdapter.getDefaultAdapter(getActivity().getApplicationContext());
        mAdapter.registerDiscoveryReceiver(this);
        mAdapter.startDiscovery();
        mFoundDevices = DeviceLab.get(getActivity()).getFoundDevices();
        mSelectedDevices = DeviceLab.get(getActivity()).getSelectedDevices();

        mConnectedDeviceMax = getActivity()
                .getSharedPreferences(SettingsConstant.SETTINGS, Context.MODE_PRIVATE)
                .getInt(SettingsConstant.SETTINGS_CONNECTED_DEVICE_MAX, 1);

        mDeviceAdapter = new DeviceAdapter(mFoundDevices);
        setListAdapter(mDeviceAdapter);

        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_device_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_search_device:
                mFoundDevices.clear();
                mSelectedDevices.clear();
                mDeviceAdapter.notifyDataSetChanged();
                if (mAdapter != null) {
                    mAdapter.startDiscovery();
                }
                return true;
            case R.id.menu_item_test_device:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.unregisterDiscoveryReceiver();
            mAdapter.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BluetoothIBridgeDevice device = (BluetoothIBridgeDevice) (getListAdapter()).getItem(position);
        if (mSelectedDevices.contains(device)) {
            mSelectedDevices.remove(device);
        } else {
            if (mSelectedDevices.size() < mConnectedDeviceMax) {
                mSelectedDevices.add(device);
            } else {
                Toast.makeText(getActivity(), R.string.selected_device_full, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mDeviceAdapter.notifyDataSetChanged();
    }

    private class DeviceAdapter extends ArrayAdapter<BluetoothIBridgeDevice> {
        public DeviceAdapter(ArrayList<BluetoothIBridgeDevice> devices) {
            super(getActivity(), android.R.layout.simple_list_item_1, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_device_list, null);
            }

            // configure the view for this Crime
            BluetoothIBridgeDevice device = getItem(position);

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.device_list_item_nameTextView);
            titleTextView.setText(device.getDeviceName());
            TextView dateTextView =
                    (TextView) convertView.findViewById(R.id.device_list_item_addressTextView);
            dateTextView.setText(device.getDeviceAddress());

            CheckBox solvedCheckBox =
                    (CheckBox) convertView.findViewById(R.id.device_list_item_selectedCheckBox);
            solvedCheckBox.setChecked(mSelectedDevices.contains(device));

            return convertView;
        }
    }

    @Override
    public void onDiscoveryFinished() {
        Toast.makeText(getActivity(), R.string.discovery_finished, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceFound(BluetoothIBridgeDevice device) {
        if (device == null) {
            return;
        }
        if (!mFoundDevices.contains(device)) {
            mFoundDevices.add(device);
            mDeviceAdapter.notifyDataSetChanged();
        }
    }
}