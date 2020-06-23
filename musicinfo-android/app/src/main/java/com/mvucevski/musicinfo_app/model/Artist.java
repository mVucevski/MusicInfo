package com.mvucevski.musicinfo_app.model;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Artist {
    @Expose
    private String WikiDataURI;
    @Expose
    private String DBPediaURI;
    @Expose
    private String shortDescription;
    @Expose
    private String longDescription;
    @Expose
    private String imageURL;
    @Expose
    private String name;
    @Expose
    private int careerStartYear;
    @Expose
    private String careerStartYearString;
    @Expose
    private List<String> genres;
    @Expose
    private List<Album> albums;
    @Expose
    private List<Award> awards;
    @Expose
    private List<String> similarArtists;
    @Expose
    private HashMap<String, String> socialLinks;
    @Expose
    private List<String> recordLabels;
    @Expose
    private String location;

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
        location = "";
    }

    public Artist(List<Award> awards, String wikiDataURI, String DBPediaURI, String shortDescription, String longDescription, String imageURL, int careerStartYear, List<String> genres, List<Album> albums, List<String> similarArtists, HashMap<String, String> socialLinks, List<String> recordLabels, String location) {
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
        this.location = location;
    }

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCareerStartYear() {
        return careerStartYear;
    }

    public void setCareerStartYear(int careerStartYear) {
        this.careerStartYear = careerStartYear;
    }

    public String getCareerStartYearString() {
        return careerStartYearString;
    }

    public void setCareerStartYearString(String careerStartYearString) {
        this.careerStartYearString = careerStartYearString;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<String> getAlbumsNames(){
        List<String> albumNames = new LinkedList<>();
        for(Album a : albums){
            albumNames.add(a.name);
        }
        return albumNames;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Award> getAwards() {
        return awards;
    }

    public List<String> getAwardsString(){
        List<String> awardsStrings = new LinkedList<>();
        for(Award a : awards){
            awardsStrings.add(a.toString());
        }
        return awardsStrings;
    }

    public void setAwards(List<Award> awards) {
        this.awards = awards;
    }

    public List<String> getSimilarArtists() {
        return similarArtists;
    }

    public void setSimilarArtists(List<String> similarArtists) {
        this.similarArtists = similarArtists;
    }

    public HashMap<String, String> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(HashMap<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public List<String> getRecordLabels() {
        return recordLabels;
    }

    public void setRecordLabels(List<String> recordLabels) {
        this.recordLabels = recordLabels;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
