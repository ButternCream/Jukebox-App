package com.jukebox.jukeboxapp;

import android.app.ActionBar;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class StartupActivity extends AppCompatActivity {
    private Button hostButton;
    private Button shareButton;


    //Initialize bluetooth adapter
    private BluetoothAdapter btAdapter;

    //Get the state of the bluetooth device and return it
    public BroadcastReceiver getBluetoothState() {
        BroadcastReceiver bluetoothState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String prevStateExtra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
                String stateExtra = BluetoothAdapter.EXTRA_STATE;
                int state = intent.getIntExtra(stateExtra, -1);
                //int previous_state = intent.getIntExtra(prevStateExtra, -1);
                String toastText = "";
                switch (state) {
                    case (BluetoothAdapter.STATE_ON): {
                        toastText = "Bluetooth on";
                        Toast.makeText(StartupActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case (BluetoothAdapter.STATE_OFF): {
                        toastText = "Bluetooth off";
                        Toast.makeText(StartupActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
        return bluetoothState;
    }// End get bluetooth state

    //Check the bluetooth state
    public void checkBluetoothState() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            String address = btAdapter.getAddress();
            //btAdapter.setName("Testing");
            String name = btAdapter.getName();
            String status = name + " : " + address;
            Toast.makeText(StartupActivity.this, status, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(StartupActivity.this, "Bluetooth not on", Toast.LENGTH_SHORT).show();
        }
    } //End check bluetooth state



    // The OnCreate method to initialize the app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);



        //Setup variables & run functions
        final BroadcastReceiver state = getBluetoothState();
        checkBluetoothState();
        hostButton = (Button) findViewById(R.id.host_button);
        shareButton = (Button) findViewById(R.id.share_button);

        //Host button onClick listener
        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                IntentFilter filter = new IntentFilter(actionStateChanged);
                registerReceiver(state, filter);
                startActivityForResult(new Intent(actionRequestEnable), 0);
                if (btAdapter.isEnabled()) {
                    //setContentView(R.layout.host_startup);
                    Intent host = new Intent(StartupActivity.this, HostActivity2.class);
                    StartupActivity.this.startActivity(host);
                }
            }
        }); //End host OnClick

        //Share button OnClick listener
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                IntentFilter filter = new IntentFilter(actionStateChanged);
                registerReceiver(state, filter);
                startActivityForResult(new Intent(actionRequestEnable), 0);
                if (btAdapter.isEnabled()) {
                    Intent share = new Intent(StartupActivity.this, devicesActivity.class);
                    StartupActivity.this.startActivity(share);
                }
            }
        });//End share OnClick

    }
}

