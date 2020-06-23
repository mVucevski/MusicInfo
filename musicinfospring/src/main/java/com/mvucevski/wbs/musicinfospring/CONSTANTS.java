package com.mvucevski.wbs.musicinfospring;

public class CONSTANTS {
    public static String WikiData_SPARQLEndpoint = "https://query.wikidata.org/bigdata/namespace/wdq/sparql";
    public static String WikiData_prefixes =
                            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                            "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
                            "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
                            "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
                            "PREFIX schema: <http://schema.org/>\n" +
                            "PREFIX p: <http://www.wikidata.org/prop/>\n" +
                            "PREFIX ps: <http://www.wikidata.org/prop/statement/>\n" +
                            "PREFIX pq: <http://www.wikidata.org/prop/qualifier/>";

    public static String[] SocialLinks =
            {"spotify", "musicbrainz", "itunes", "soundcloud",
            "youtube", "twitter","instagram", "googlemusic","facebook"};
    public static String[] SocialLinks_URL =
            {"https://open.spotify.com/artist/", "https://musicbrainz.org/artist/", "https://itunes.apple.com/artist/",
                    "https://soundcloud.com/", "https://youtube.com/channel/", "https://twitter.com/",
                    "https://www.instagram.com/", "https://play.google.com/store/music/artist?id=","https://www.facebook.com/"};
    public static String WikiData_Images_Path = "https://commons.wikimedia.org/wiki/";

}
