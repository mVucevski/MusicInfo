package com.mvucevski.musicinfo_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.mvucevski.musicinfo_app.R;
import com.mvucevski.musicinfo_app.lyrics.GetLyricsTask;

import java.util.concurrent.ExecutionException;

public class LyricsFragment extends Fragment {

    private View rootView;
    private String artistName, songName;
    private TextView tvLyricsTitle, tvSongLyrics;
    private String lyrics;
    private GetLyricsTask getLyricsTask;

    public LyricsFragment(String artistName, String songName){
        this.artistName = artistName;
        this.songName = songName;
        lyrics = "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lyrics,container,false);

        tvLyricsTitle = rootView.findViewById(R.id.tvLyricsTitle);
        tvSongLyrics = rootView.findViewById(R.id.tvSongLyrics);

        getLyricsTask = new GetLyricsTask(tvSongLyrics);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(lyrics!=null && lyrics.length() > 0){
            tvLyricsTitle.setText(artistName + " - " + songName);
            tvSongLyrics.setText(lyrics);
        }else {
            if(artistName.isEmpty() || songName.isEmpty()){
                tvLyricsTitle.setText("No Active Song | The service isn't available at the moment!");
                tvSongLyrics.setText("");
            }else{
                updateSongLyrics(artistName, songName);
            }
        }
    }

    public void updateSongLyrics(String artistName, String songName) {
            this.artistName = artistName;
            this.songName = songName;

            tvLyricsTitle.setText(artistName + " - " + songName);

            System.out.println("UPDATE LYRICS - updateSongLyrics: " + artistName + " - " + songName);
            try{
                lyrics = getLyricsTask.execute(artistName + " - " + songName).get();
            }catch (Exception ex){
                lyrics = "";
            }
    }




}
