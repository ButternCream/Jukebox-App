package com.jukebox.jukeboxapp;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.ramimartin.multibluetooth.activity.BluetoothActivity;

public class MultiBTActivity extends BluetoothActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_startup);
    }

    @Override
    public int myNbrClientMax() {
        return 7;
    }

    @Override
    public void onBluetoothStartDiscovery() {
    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {
    }

    @Override
    public void onClientConnectionSuccess() {
    }

    @Override
    public void onClientConnectionFail() {
    }

    @Override
    public void onServeurConnectionSuccess() {
    }

    @Override
    public void onServeurConnectionFail() {
    }

    @Override
    public void onBluetoothCommunicator(String messageReceive) {
    }

    @Override
    public void onBluetoothNotAviable() {
    }


}