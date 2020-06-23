package com.mvucevski.musicinfo_app.lyrics;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Lyrics {
    private String songLyricsURL;
    private String songLyrics;

    public Lyrics(){
        this.songLyricsURL = "";
        this.songLyrics = "";
    }

    // Method 1 to find the lyrics url of a song
    // Find song lyrics via search and pick the 1st result
    public String searchSongLyricsURL(String searchString) throws IOException {
        String search_url_prefix = "https://search.azlyrics.com/search.php?q=";

        String searchS = searchString.split("\\(")[0].replaceAll(" ", "+");
        String searchURL = search_url_prefix + searchS;

        Document docSearch = Jsoup.connect(searchURL).get();
        Element searchResult = docSearch.select("table.table-condensed:nth-child(2) tr:nth-child(1) td a").first();

        if(searchResult == null){
            System.out.println(this.getClass().getName() + ":SONG NOT FOUND");
        }else{
            String lyricsURL = searchResult.attr("href");
            //System.out.println(lyricsURL);
            this.songLyricsURL = lyricsURL;

            System.out.println("SEARCH SONG LYRICS URL: " + lyricsURL);

            return lyricsURL;
        }
        return null;
    }

    // Method 2 to find the lyrics url of a song
    // Directly get the song lyrics url
    public String getSongLyricsURL(String songName){
        String[] song_name = songName.replaceAll("\\s+", "").split("\\(")[0].split("-");
        String song = song_name[1].toLowerCase();
        String artist = song_name[0].toLowerCase();
        String songURL = String.format("https://www.azlyrics.com/lyrics/%s/%s.html", artist, song);

        System.out.println("GET SONG LYRICS URL: " + songURL);

        this.songLyricsURL = songURL;
        return songURL;
    }

    public Document findSongLyricsPage(String songName) throws IOException {
        Document lyricsDoc = null;
        String songLyricsURL = getSongLyricsURL(songName);

        try{
            lyricsDoc = Jsoup.connect(songLyricsURL).get();
        }catch (Exception ex){
            songLyricsURL = searchSongLyricsURL(songName);
            lyricsDoc = Jsoup.connect(songLyricsURL).get();
            return lyricsDoc;
        }
        return lyricsDoc;
    }


    public String getSongLyrics(String songName) throws IOException {
        Document lyricsDoc = findSongLyricsPage(songName);

        if(lyricsDoc==null){
            return "";
        }

        Elements mainContainer = lyricsDoc.select("div.col-xs-12.col-lg-8.text-center");
        Element el_Lyrics = mainContainer.select("div:not([class])").first();

        if(el_Lyrics==null){
            System.out.println("SONG NOT FOUND");
            return null;
        }

        /*
        // Method 1
        String prettyLyrics = el_Lyrics.toString()
                .replaceAll(" <br> ", "")
                .replaceAll(".*> ", "")
                .replaceAll("</div>", "")
                .trim();
        */
        // Method 2
        String textLyrics = el_Lyrics.wholeText().trim();
        String prettyLyrics = textLyrics.replaceAll("\\s{18}", "\n");  // Same Result As => text_lyrics.replaceAll("                ", "\n");

        this.songLyrics = prettyLyrics;

        return prettyLyrics;
    }
}
