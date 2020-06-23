package com.mvucevski.musicinfo_app.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

//import android.app.FragmentManager;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mvucevski.musicinfo_app.R;
import com.mvucevski.musicinfo_app.fragment.ArtistFragment;
import com.mvucevski.musicinfo_app.fragment.LyricsFragment;
import com.mvucevski.musicinfo_app.fragment.SongFragment;
import com.mvucevski.musicinfo_app.service.MyNotificationService;

import java.util.Objects;

import static com.mvucevski.musicinfo_app.CONSTANTS.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.mvucevski.musicinfo_app.CONSTANTS.NOTIFICATIONS_KEY;
import static com.mvucevski.musicinfo_app.CONSTANTS.SERVICECMD;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main2Activity";
    private final String lyrics_fragment_tag = "LyricsFragment";
    private final String artist_fragment_tag = "ArtistFragment";
    private final String song_fragment_tag = "SongFragment";

    private AHBottomNavigation bottomNavigation;
    private int lastSelectedFragmentIndex;
    private SongFragment songFragment;
    private ArtistFragment artistFragment;
    private LyricsFragment lyricsFragment;
    private String currentFragmentTag;

    private String songName, artistName;

    private BroadcastReceiver broadcastReceiver;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        songName = "";
        artistName = "";

        initNavBar();
        initFragments();
        initBroadcastReceiver();
    }

    @Override
    public void onStart() {
        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (!manager.isMusicActive()) {
           // tvSongName.setText("No Music is playing!!!!!");
        }else{
            Log.i(TAG, "Call showNotifications");
            showNotifications();
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                Log.i(TAG, "Below OREO");
//                //showNotifications();
//            }else{
//                Log.i(TAG, "Above and eq to OREO");
//                //getNotificationAPI26Plus();
//                // showNotifications();
//            }


        }
        super.onStart();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                System.out.println("BORADCASTE RECIEVER: " + action);

                if(action!= null && action.equals(NOTIFICATIONS_KEY)){
                    StatusBarNotification[] notifications = (StatusBarNotification[]) intent.getExtras().get("notifications");

                    for (StatusBarNotification notification : notifications) {
                        if(notification.getPackageName().contains("android.music")){
                            String songTitle = notification.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                            String songArtist = notification.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                            System.out.println("BROADCAST NOTIFICATION UPDATE SONG: " + songArtist + " - " + songTitle);
                            updateSong(songArtist, songTitle);
                            break;
                        }
                    }
                }else{
                    String artist = intent.getStringExtra("artist");
                    String track = intent.getStringExtra("track");
                    System.out.println("BROADCAST SONG STATE CHANGED - UPDATE SONG: " + artist + " - " + track);
                    updateSong(artist, track);
                }

            }
        };


        IntentFilter iF = new IntentFilter();
        iF.addAction(SERVICECMD);
        iF.addAction("com.android.music.metachanged"); // new song is playing
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.android.music.updateprogress");
        iF.addAction("com.example.broadcast.MY_NOTIFICATION");

        registerReceiver(broadcastReceiver, iF);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(NOTIFICATIONS_KEY));
    }


    public void showNotifications() {
        if (isNotificationServiceEnabled()) {
            Log.i(TAG, "Notification enabled -- trying to fetch it");
            getNotifications();
        } else {
            Log.i(TAG, "Notification disabled -- Opening settings");
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String allNames = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (allNames != null && !allNames.isEmpty()) {
            for (String name : allNames.split(":")) {
                if (getPackageName().equals(ComponentName.unflattenFromString(name).getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void getNotifications() {
        Log.i(TAG, "Waiting for MyNotificationService");
        //MyNotificationService myNotificationService = MyNotificationService.get();
        MyNotificationService myNotificationService = MyNotificationService.get();
        if(myNotificationService == null)
            return ;
        Log.i(TAG, "Active Notifications: [");
        StatusBarNotification[] activeNotifications = myNotificationService.getActiveNotifications();
        Log.i(TAG, "Notfications Nuber:" + activeNotifications.length);

        for (StatusBarNotification notification : activeNotifications) {
            Log.i(TAG, "Notfications:");
            Log.i(TAG, notification.getPackageName());
            if(notification.getPackageName().contains("android.music")){
                String songTitle = notification.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                String songArtist = notification.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();

                updateSong(songArtist, songTitle);
                break;
            }
        }

    }

    private void initNavBar(){
        Objects.requireNonNull(getSupportActionBar()).hide();
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_nav);

        //Create Item
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Lyrics", R.drawable.lyrics_24dp, android.R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Song", R.drawable.song_24dp, android.R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Artist", R.drawable.artist_24dp, android.R.color.white);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
    }

    private void initFragments(){
        FragmentManager fManager = getSupportFragmentManager();

        //poceten fragment
        lyricsFragment = new LyricsFragment(artistName, songName);
        fManager.beginTransaction().add(R.id.fragment_container,
                lyricsFragment, lyrics_fragment_tag).commit();
        lastSelectedFragmentIndex = 0;
        currentFragmentTag = lyrics_fragment_tag;

        bottomNavigation.setOnTabSelectedListener((pos,isSelected)->{
            //Toast.makeText(MainActivity.this, "Start activity: " + pos, Toast.LENGTH_SHORT).show();

            Fragment selectedFragment = null;

            switch (pos){
                case 0:
                    if(lastSelectedFragmentIndex != 0) {
                        if(lyricsFragment==null){
                            lyricsFragment = new LyricsFragment(artistName, songName);
                        }

                        selectedFragment = lyricsFragment;
                        lastSelectedFragmentIndex = 0;
                        currentFragmentTag = lyrics_fragment_tag;
                    }
                    break;
                case 1:
                    if(lastSelectedFragmentIndex != 1) {
                        if(songFragment==null)
                            songFragment = new SongFragment(artistName, songName);

                        selectedFragment = songFragment;
                        lastSelectedFragmentIndex = 1;
                        currentFragmentTag = song_fragment_tag;
                    }
                    break;
                case 2:
                    if(lastSelectedFragmentIndex != 2) {
                        if(artistFragment==null){
                            artistFragment = new ArtistFragment(artistName, songName);
                        }

                        selectedFragment = artistFragment;
                        lastSelectedFragmentIndex = 2;
                        currentFragmentTag = artist_fragment_tag;
                    }
                    break;
            }

            if(selectedFragment!=null){
                Fragment fragment = fManager.findFragmentByTag(currentFragmentTag);

                // If fragment doesn't exist yet, create one
                if (fragment == null) {
                    fManager.beginTransaction().replace(R.id.fragment_container,selectedFragment, currentFragmentTag)
                            .commit();
                }
                else { // re-use the old fragment
                    fManager.beginTransaction().replace(R.id.fragment_container,fragment, currentFragmentTag)
                            .commit();
                }

            }
            return true;
        });
    }

    private void updateSong(String artistName, String songName){
        if(artistName == null || songName == null){
            return ;
        }

        if(artistName.equals(this.artistName) && songName.equals(this.songName)){
            return ;
        }

        if(artistName.length() == 0 || songName.length() == 0)
            return ;


        //tvSongName.setText(String.format("%s - %s", artistName, songName));

        if(lyricsFragment!=null){
            lyricsFragment.updateSongLyrics(artistName, songName);
        }
        if(songFragment!=null){
            songFragment.setNewSong(artistName, songName);
        }
        if(artistFragment!=null){
            artistFragment.setNewArtist(artistName);
        }

        this.artistName = artistName;
        this.songName = songName;
    }
}