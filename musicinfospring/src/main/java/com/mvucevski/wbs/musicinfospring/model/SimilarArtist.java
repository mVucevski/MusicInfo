package com.mvucevski.wbs.musicinfospring.model;

public class SimilarArtist {
    public String wikiDataURI;
    public String name;
    public String genre;

    public SimilarArtist(String wikiDataURI, String name, String genre) {
        this.wikiDataURI = wikiDataURI;
        this.name = name;
        this.genre = genre;
    }
}
