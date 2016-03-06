package com.jukebox.jukeboxapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity {

    ListView List;

    public ArrayList<String> GetFiles(String directorypath){
        ArrayList<String> Myfiles = new ArrayList<String>();
        File f = new File(directorypath);
        f.mkdirs();
        File[] files = f.listFiles();
        if(files.length==0){
            return null;
        }
        else{
            for(int i=0;i<files.length;i++)
                Myfiles.add(files[i].getName());
        }
        return Myfiles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);

        String path = Environment.getExternalStorageDirectory().toString();
        //Log.d("Files", "Path:" + path);

        //File f = new File(path + "/Music");
        //File[] list = f.listFiles();
        //View inflatedView = getLayoutInflater().inflate(R.layout.share_startup, null);
        List = (ListView) findViewById(R.id.music_files);
        //text.setText("Hello!");
        ArrayList<String> filesinfolder = GetFiles(path + "/Music");
        //List = (ListView)findViewById(R.id.devices_list);
        List.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                filesinfolder));
    }
}
