package com.jukebox.jukeboxapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class setupActivity extends AppCompatActivity {
    private TextView conName;
    private Button queueBTN;
    private Button myMusic;
    private ListView qL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        conName = (TextView)findViewById(R.id.connection_name);
        queueBTN = (Button) findViewById(R.id.queu_button);
        myMusic = (Button) findViewById(R.id.my_music);

        queueBTN.setText("Play/Pause Queue");
        myMusic.setText("My Music");

        conName.setText(HostActivity2.conName_var);

        queueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO SOMETHING
            }
        });

        myMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.music_list);
                Intent goToMusic = new Intent(setupActivity.this, ShareActivity.class);
                setupActivity.this.startActivity(goToMusic);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
