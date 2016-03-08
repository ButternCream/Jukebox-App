package com.jukebox.jukeboxapp;


import android.media.MediaPlayer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import com.jukebox.jukeboxapp.SongAdapter;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;
import com.jukebox.jukeboxapp.MusicService.MusicBinder;
import android.widget.MediaController.MediaPlayerControl;

class Mp3Filter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3"));
    }
}

public class ShareActivity extends AppCompatActivity implements MediaPlayerControl{

    private ListView songView;
    private static final String MEDIA_PATH = Environment.getExternalStorageDirectory().toString() + "/Music/";
    private ArrayList<Song> songList;
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = 0;
    //private int previousPosition = currentPosition;
    private MusicController controller;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private boolean paused=false, playbackPaused=false;


//
//    private void playSong(String songPath) {
//        try {
//
////            if (previousPosition == currentPosition && mp.isPlaying())
////            {
////                mp.pause();
////            }
////            else if (previousPosition != currentPosition && mp.isPlaying())
////            {
////                mp.pause();
////                mp.reset();
////                mp.setDataSource(songPath);
////                mp.prepare();
////                mp.start();
////            }
////            else{
//                mp.reset();
//                mp.setDataSource(songPath);
//                mp.prepare();
//                mp.start();
//
//
//            // Setup listener so next song starts automatically
//            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                public void onCompletion(MediaPlayer arg0) {
//                    nextSong();
//                }
//
//            });
//
//        } catch (IOException e) {
//            Log.v(getString(R.string.app_name), e.getMessage());
//        }
//    }
//
//    private void nextSong() {
//        if (++currentPosition >= songs.size()) {
//            // Last song, just reset currentPosition
//            currentPosition = 0;
//        } else {
//            // Play next song
//            playSong(MEDIA_PATH + songs.get(currentPosition));
//        }
//    }
//
//    private void prevSong() {
//        if (--currentPosition < 0) {
//            // Last song, just reset currentPosition
//            currentPosition = songs.size() - 1;
//        } else {
//            // Play next song
//            playSong(MEDIA_PATH + songs.get(currentPosition));
//        }
//    }
//
//
//    public void updateSongList() {
//        File home = new File(MEDIA_PATH);
//        if (home.listFiles(new Mp3Filter()).length > 0) {
//            for (File file : home.listFiles(new Mp3Filter())) {
//                songs.add(file.getName());
//            }
//
//            //ArrayAdapter<String> songList = new ArrayAdapter<String>(this,
//             //       android.R.layout.simple_list_item_1, songs);
//            //setListAdapter(songList);
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
        setController();

        // CREATES TOOLBAR
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        updateSongList();
//        List = (ListView) findViewById(R.id.music_files);
//        List.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs));
//        List.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        //previousPosition = currentPosition;
//                        currentPosition = position;
//                        playSong(MEDIA_PATH + songs.get(position));
//                    }
//                }
//        );
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mp.isPlaying()){
//                    mp.pause();
//                } else{
//                    mp.start();
//                }
//            }
//        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                musicSrv.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    private void setController(){
        //set the controller up
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        musicSrv.setShuffle();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play next
    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
