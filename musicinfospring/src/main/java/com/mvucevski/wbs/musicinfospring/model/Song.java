package com.mvucevski.wbs.musicinfospring.model;

import java.util.Date;
import java.util.List;

public class Song {
    public String WikiDataURI;
    public String DBPediaURI;
    public String date;
    public String albumWikiDataURI;
    public String albumName;
    public String videoURL;
    public String musicbrainzID;
    public String artistName;

    public List<String> musicAuthors;
    public List<String> lyricsAuthors;
    public String award;
    public String runTime;
    public String description;
    public String coverURL;

    public Song(String wikiDataURI, String wikiPediaURI, String date, String albumWikiDataURI, String albumName, String videoURL, String musicbrainzID) {
        WikiDataURI = wikiDataURI;
        this.DBPediaURI = wikiPediaURI.replaceAll("en.wikipedia.org/wiki", "dbpedia.org/resource").replaceAll("https://", "http://");
        this.date = date;
        this.albumWikiDataURI = albumWikiDataURI;
        this.albumName = albumName;
        this.videoURL = videoURL;
        this.musicbrainzID = musicbrainzID;
    }

    public Song() {
        this.DBPediaURI = "";
        this.date = "";
        this.albumWikiDataURI = "";
        this.albumName = "";
        this.videoURL = "";
        this.musicbrainzID = "";
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
                '}';
    }
}
