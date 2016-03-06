package com.jukebox.jukeboxapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class setupActivity extends AppCompatActivity {
    private TextView conName;
    private Button queuBTN;
    private Button musicBTN;
    private ListView qL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        conName = (TextView) findViewById(R.id.connection_name);
        musicBTN = (Button) findViewById(R.id.my_music);
        queuBTN = (Button)findViewById(R.id.queu_button);
        qL = (ListView) findViewById(R.id.queu_list);

        conName.setText(HostActivity2.con_Name);
        queuBTN.setText("Begin Queu");
        musicBTN.setText("My Music");

        queuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO SOMETHING>....
            }
        });

        musicBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO SOETHTIEHRF...

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
