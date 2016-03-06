package com.jukebox.jukeboxapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jukebox.jukeboxapp.Client.BluetoothConnector;
import com.jukebox.jukeboxapp.Manager.BluetoothManager;
import com.jukebox.jukeboxapp.Server.BluetoothServer;

public class HostActivity2 extends AppCompatActivity {
    private Button continueBTN;
    public static String con_Name;
    private EditText host;
    //BluetoothAdapter btAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host2);

//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        MultiBTActivity activity = new MultiBTActivity();
//        activity.onBluetoothStartDiscovery();
//
//        BluetoothServer server = new BluetoothServer(btAdapter, btAdapter.getAddress());
//
//        BluetoothManager manager = new BluetoothManager(activity);
//        manager.setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_3600_SEC);
//        manager.selectServerMode();
//        manager.scanAllBluetoothDevice();
//        manager.createClient(server.getServerAddress());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        continueBTN = (Button) findViewById(R.id.session_continue);
        continueBTN.setText("Continue");
        host = (EditText)findViewById(R.id.host_name);

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con_Name = host.getText().toString();
                Intent music = new Intent(HostActivity2.this, setupActivity.class);
                HostActivity2.this.startActivity(music);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
