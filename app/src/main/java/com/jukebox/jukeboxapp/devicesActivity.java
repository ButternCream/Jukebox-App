package com.jukebox.jukeboxapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class devicesActivity extends AppCompatActivity {
    BluetoothAdapter adapter;
    ArrayList<BluetoothDevice> allDevices = new ArrayList<BluetoothDevice>();
    ArrayList<String>allDeviceNames = new ArrayList<String>();
    ListView list;
    //private static final UUID MY_UUID = UUID.fromString("20687DAD-B023-F19E-2F60-A135554CC3FD");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        adapter = BluetoothAdapter.getDefaultAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(devicesActivity.this, Integer.toString(adapter.getState()), Toast.LENGTH_LONG).show();

        clearPairs();

        Toast.makeText(devicesActivity.this,"Cleared Pairs",Toast.LENGTH_LONG).show();




        //SCAN FOR BLUETOOTH DEVICES
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        //adapter.cancelDiscovery();
        adapter.startDiscovery();
        //DISPLAY SCANNED DEVICES TO LISTVIEW
        list = (ListView)findViewById(R.id.BTallDevices);





        //PAIRING
        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairReceiver,intent);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(devicesActivity.this,"Searching...",Toast.LENGTH_SHORT).show();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(devicesActivity.this,"Done.",Toast.LENGTH_LONG).show();
             //   Toast.makeText(devicesActivity.this,String.valueOf(allDevices.size()), Toast.LENGTH_SHORT).show();
                allDeviceNames = getNames(allDevices);
                //list = (ListView)findViewById(R.id.allDevices);
                list.setAdapter(new ArrayAdapter<String>(devicesActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1 , allDeviceNames));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(devicesActivity.this,allDevices.get(position).getAddress(),Toast.LENGTH_SHORT).show();
                        pairDevice(allDevices.get(position));
                    }
                });

            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //Toast.makeText(devicesActivity.this,"DEVICE FOUND",Toast.LENGTH_LONG).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null){
                    allDevices.add(device);
                    Toast.makeText(devicesActivity.this,device.getName(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(devicesActivity.this,"NULL",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private ArrayList<String> getNames(ArrayList<BluetoothDevice> devices){
        allDeviceNames = new ArrayList<String>();
        for(int i =0;i <devices.size(); i++) {
            if (devices.get(i) != null){
                allDeviceNames.add(devices.get(i).getName());
                Toast.makeText(devicesActivity.this, "Added: " + allDeviceNames.get(i), Toast.LENGTH_LONG).show();
            }

        }
        Toast.makeText(devicesActivity.this,"Returning device names",Toast.LENGTH_LONG).show();
        return allDeviceNames;
    }



    private void pairDevice(BluetoothDevice device){
        try{
            Method method = device.getClass().getMethod("createBond",(Class[]) null);
            method.invoke(device,(Object[])null);
            Toast.makeText(devicesActivity.this,device.getName(),Toast.LENGTH_LONG).show();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device){
        try{
            Method method = device.getClass().getMethod("removeBond",(Class[]) null);
            method.invoke(device,(Object[])null);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,BluetoothDevice.ERROR);
                if(state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING){
                    Toast.makeText(devicesActivity.this,"PAIRED",Toast.LENGTH_LONG).show();
                    setContentView(R.layout.music_list);
                    Intent musicLib = new Intent(devicesActivity.this, ShareActivity.class);
                    devicesActivity.this.startActivity(musicLib);
                }
                else if(state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(devicesActivity.this,"UNPAIRED",Toast.LENGTH_LONG).show();

                }
            }
        }
    };

    void clearPairs(){
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if(pairedDevices.size() >0){
            for(BluetoothDevice device : pairedDevices){
                try{
                    unpairDevice(device);
                }
                catch (Exception e){
                    Log.e("failed",e.getMessage());
                }
            }
        }
    }


}


