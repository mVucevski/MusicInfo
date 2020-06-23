package com.mvucevski.musicinfo_app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Song {
    @Expose
    private String WikiDataURI;
    @Expose
    private String DBPediaURI;
    @Expose
    private String date;
    @Expose
    private String albumWikiDataURI;
    @Expose
    private String albumName;
    @Expose
    private String videoURL;
    @Expose
    private String musicbrainzID;
    private String artistName;

    @Expose
    private List<String> musicAuthors;
    @Expose
    private List<String> lyricsAuthors;
    @Expose
    private String award;
    @Expose
    private String runTime;
    @Expose
    private String description;
    @Expose
    private String coverURL;


    public Song(String wikiDataURI, String DBPediaURI, String date, String albumWikiDataURI, String albumName, String videoURL, String musicbrainzID, String artistName, List<String> musicAuthors, List<String> lyricsAuthors, String award, String runTime, String description, String coverURL) {
        WikiDataURI = wikiDataURI;
        this.DBPediaURI = DBPediaURI;
        this.date = date;
        this.albumWikiDataURI = albumWikiDataURI;
        this.albumName = albumName;
        this.videoURL = videoURL;
        this.musicbrainzID = musicbrainzID;
        this.artistName = artistName;
        this.musicAuthors = musicAuthors;
        this.lyricsAuthors = lyricsAuthors;
        this.award = award;
        this.runTime = runTime;
        this.description = description;
        this.coverURL = coverURL;
    }

    public Song() { }

    public String getWikiDataURI() {
        return WikiDataURI;
    }

    public void setWikiDataURI(String wikiDataURI) {
        WikiDataURI = wikiDataURI;
    }

    public String getDBPediaURI() {
        return DBPediaURI;
    }

    public void setDBPediaURI(String DBPediaURI) {
        this.DBPediaURI = DBPediaURI;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlbumWikiDataURI() {
        return albumWikiDataURI;
    }

    public void setAlbumWikiDataURI(String albumWikiDataURI) {
        this.albumWikiDataURI = albumWikiDataURI;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getMusicbrainzID() {
        return musicbrainzID;
    }

    public void setMusicbrainzID(String musicbrainzID) {
        this.musicbrainzID = musicbrainzID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public List<String> getMusicAuthors() {
        return musicAuthors;
    }

    public void setMusicAuthors(List<String> musicAuthors) {
        this.musicAuthors = musicAuthors;
    }

    public List<String> getLyricsAuthors() {
        return lyricsAuthors;
    }

    public void setLyricsAuthors(List<String> lyricsAuthors) {
        this.lyricsAuthors = lyricsAuthors;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    @Override
    public String toString() {
        return "Song{" +
                "WikiDataURI='" + WikiDataURI + '\'' +
                ", DBPediaURI='" + DBPediaURI + '\'' +
                ", date='" + date + '\'' +
                ", albumWikiDataURI='" + albumWikiDataURI + '\'' +
                ", albumName='" + albumName + '\'' +
                ", videoURL='" + videoURL + '\'' +
                ", musicbrainzID='" + musicbrainzID + '\'' +
                ", artistName='" + artistName + '\'' +
                ", musicAuthors=" + musicAuthors +
                ", lyricsAuthors=" + lyricsAuthors +
                ", award='" + award + '\'' +
                ", runTime='" + runTime + '\'' +
                ", description='" + description + '\'' +
                ", coverURL='" + coverURL + '\'' +
                '}';
    }

    public String parseDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date;
        try {
            date = format.parse(dateString);

            format = new SimpleDateFormat("dd.MM.yyyy");
            String resultDateString = format.format(date);
            System.out.println("Date ->" + resultDateString);
            return resultDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}