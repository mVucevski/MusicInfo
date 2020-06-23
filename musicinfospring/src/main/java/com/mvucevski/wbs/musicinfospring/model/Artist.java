package com.mvucevski.wbs.musicinfospring.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Artist {
    public List<Award> awards;
    public String WikiDataURI;
    public String DBPediaURI;
    public String shortDescription;
    public String longDescription;
    public String imageURL;
    public String name;
    public int careerStartYear;
    public String careerStartYearString;
    public List<String> genres;
    public List<Album> albums;
    public List<String> similarArtists;
    public HashMap<String, String> socialLinks;
    public List<String> recordLabels;
    public String location;

    public Artist() {
        awards = new LinkedList<>();
        genres = new LinkedList<>();
        albums = new LinkedList<>();
        similarArtists = new LinkedList<>();
        recordLabels = new LinkedList<>();
        socialLinks = new HashMap<>();
        WikiDataURI = "";
        DBPediaURI = "";
        shortDescription = "";
        longDescription = "";
        imageURL = "";
        name = "";
        careerStartYear = -1;
        location= "";
    }

    public Artist(List<Award> awards, String wikiDataURI, String DBPediaURI, String shortDescription, String longDescription, String imageURL, int careerStartYear, List<String> genres, List<Album> albums, List<String> similarArtists, HashMap<String, String> socialLinks, List<String> recordLabels) {
        this.awards = awards;
        WikiDataURI = wikiDataURI;
        this.DBPediaURI = DBPediaURI;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.imageURL = imageURL;
        this.careerStartYear = careerStartYear;
        this.genres = genres;
        this.albums = albums;
        this.similarArtists = similarArtists;
        this.socialLinks = socialLinks;
        this.recordLabels = recordLabels;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "awards=" + awards +
                ", WikiDataURI='" + WikiDataURI + '\'' +
                ", DBPediaURI='" + DBPediaURI + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", name='" + name + '\'' +
                ", careerStartYear=" + careerStartYear +
                ", careerStartYearString='" + careerStartYearString + '\'' +
                ", genres=" + genres +
                ", albums=" + albums +
                ", similarArtists=" + similarArtists +
                ", socialLinks=" + socialLinks +
                ", recordLabels=" + recordLabels +
                '}';
    }
}
