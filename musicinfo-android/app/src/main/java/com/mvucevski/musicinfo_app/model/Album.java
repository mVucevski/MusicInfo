package com.mvucevski.musicinfo_app.model;

import java.util.LinkedList;
import java.util.List;

public class Album {
    public String WikiDataURI;
    public String DBPediaURI;
    public String coverURL;
    public String name;
    public List<String> songs;
    public String musicbrainzID;

    public Album() {
        WikiDataURI = "";
        DBPediaURI = "";
        coverURL = "";
        name = "";
        songs = new LinkedList<>();
        musicbrainzID = "";
    }

    public Album(String wikiDataURI, String DBPediaURI, String coverURL, String name, List<String> songs, String musicbrainzID) {
        this.WikiDataURI = wikiDataURI;
        this.DBPediaURI = DBPediaURI;
        this.coverURL = coverURL;
        this.name = name;
        this.songs = songs;
        this.musicbrainzID = musicbrainzID;
    }
}
