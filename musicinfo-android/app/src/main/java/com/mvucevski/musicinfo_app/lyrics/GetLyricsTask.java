package com.mvucevski.musicinfo_app.lyrics;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

public class GetLyricsTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private TextView tvSongLyrics;

    public GetLyricsTask(TextView tvSongLyrics) {
        this.tvSongLyrics = tvSongLyrics;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Lyrics lyricsObj = new Lyrics();
            System.out.println("UPDATE LYRICS - doInBackground: " + strings[0]);
            String lyrics = lyricsObj.getSongLyrics(strings[0]);

            return lyrics;
            //return "This is the lyrics of the song. PLACEHOLDER";
        } catch (Exception e) {
            System.out.println(e.toString());

            return "";
            //return "Something went wrong :(";
        }
    }

    protected void onPostExecute(String result) {
        if(result != null){
            tvSongLyrics.setText(result);
        }else{
            tvSongLyrics.setText("");
        }
    }
}

