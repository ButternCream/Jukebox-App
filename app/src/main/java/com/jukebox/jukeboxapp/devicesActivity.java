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

import com.jukebox.jukeboxapp.Client.BluetoothClient;
import com.jukebox.jukeboxapp.Manager.BluetoothManager;
import com.ramimartin.multibluetooth.bluetooth.client.BluetoothConnector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class devicesActivity extends AppCompatActivity {
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevice> allDevices = new ArrayList<BluetoothDevice>();
    ArrayList<String>allDeviceNames = new ArrayList<String>();
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

//        MultiBTActivity activity = new MultiBTActivity();
//        activity.onBluetoothStartDiscovery();
//        BluetoothClient client = new BluetoothClient(adapter, adapter.getAddress());
//
//        client.run();
//        BluetoothConnector connector = new BluetoothConnector(client.getbluetoothdevice(), true, client.getbluetoothadapter(), client.getuuid());
//        BluetoothManager manager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
//        manager.setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_120_SEC);
//        manager.selectClientMode();
//        manager.createClient(client.getadressmac());


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
        clearPairs();



        //SCAN FOR BLUETOOTH DEVIDES
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        adapter.startDiscovery();
        //DISPLAY SCANNED DEVICES TO LISTVIEW
        list = (ListView)findViewById(R.id.allDevices);





        //PAIRING
        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairReceiver,intent);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(devicesActivity.this,"Searching...",Toast.LENGTH_LONG).show();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(devicesActivity.this,"Done.",Toast.LENGTH_LONG).show();
             //   Toast.makeText(devicesActivity.this,String.valueOf(allDevices.size()), Toast.LENGTH_SHORT).show();
                allDeviceNames = getNames(allDevices);
                list = (ListView)findViewById(R.id.allDevices);
                list.setAdapter(new ArrayAdapter<String>(devicesActivity.this, android.R.layout.simple_list_item_1, allDeviceNames));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(devicesActivity.this,allDevices.get(position).getName(),Toast.LENGTH_SHORT).show();
                        pairDevice(allDevices.get(position));
                    }
                });

            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //Toast.makeText(devicesActivity.this,"DEVICE FOUND",Toast.LENGTH_LONG).show();
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                allDevices.add(device);
                //Toast.makeText(devicesActivity.this,device.getName(),Toast.LENGTH_LONG).show();
            }
        }
    };

    private ArrayList<String> getNames(ArrayList<BluetoothDevice> devices){
        ArrayList<String> names = new ArrayList<String>();
        for(int i =0;i <devices.size(); i++) {
            names.add(devices.get(i).getName());
        }
        return names;
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


