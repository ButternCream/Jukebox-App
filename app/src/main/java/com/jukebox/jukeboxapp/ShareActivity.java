package com.jukebox.jukeboxapp;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Mp3Filter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3"));
    }
}

public class ShareActivity extends AppCompatActivity {

    ListView List;

//    public ArrayList<String> GetFiles(String directorypath){
//        ArrayList<String> Myfiles = new ArrayList<String>();
//        File f = new File(directorypath);
//        f.mkdirs();
//        File[] files = f.listFiles();
//        if(files.length==0){
//            return null;
//        }
//        else{
//            for(int i=0;i<files.length;i++)
//                Myfiles.add(files[i].getName());
//        }
//        return Myfiles;
//    }

    private static final String MEDIA_PATH = Environment.getExternalStorageDirectory().toString()
            + "/Music/";
    private ArrayList<String> songs = new ArrayList<String>();
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = 0;
    private int previousPosition = currentPosition;
//
    private void playSong(String songPath) {
        try {

            if (previousPosition == currentPosition && mp.isPlaying())
            {
                mp.pause();
            }
            else if (previousPosition != currentPosition && mp.isPlaying())
            {
                mp.pause();
                mp.reset();
                mp.setDataSource(songPath);
                mp.prepare();
                mp.start();
            }
            else{
                mp.reset();
                mp.setDataSource(songPath);
                mp.prepare();
                mp.start();
            }
//            else{
//                mp.reset();
//                mp.setDataSource(songPath);
//                mp.prepare();
//                mp.start();
//            }


            // Setup listener so next song starts automatically
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    nextSong();
                }

            });

        } catch (IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
    }

    private void nextSong() {
        if (++currentPosition >= songs.size()) {
            // Last song, just reset currentPosition
            currentPosition = 0;
        } else {
            // Play next song
            playSong(MEDIA_PATH + songs.get(currentPosition));
        }
    }

    public void updateSongList() {
        File home = new File(MEDIA_PATH);
        if (home.listFiles(new Mp3Filter()).length > 0) {
            for (File file : home.listFiles(new Mp3Filter())) {
                songs.add(file.getName());
            }

            //ArrayAdapter<String> songList = new ArrayAdapter<String>(this,
             //       android.R.layout.simple_list_item_1, songs);
            //setListAdapter(songList);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);
        updateSongList();
        List = (ListView) findViewById(R.id.music_files);
        List.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs));
        List.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        previousPosition = currentPosition;
                        currentPosition = position;
                        playSong(MEDIA_PATH + songs.get(position));
                    }
                }
        );
//        String path = Environment.getExternalStorageDirectory().toString();
//        //Log.d("Files", "Path:" + path);
//
//        //File f = new File(path + "/Music");
//        //File[] list = f.listFiles();
//        //View inflatedView = getLayoutInflater().inflate(R.layout.share_startup, null);
//        List = (ListView) findViewById(R.id.music_files);
//        //text.setText("Hello!");
//        ArrayList<String> filesinfolder = GetFiles(path + "/Music");
//        //List = (ListView)findViewById(R.id.devices_list);
//        List.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                filesinfolder));



    }
}
