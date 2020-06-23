package com.mvucevski.wbs.musicinfospring;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Helper {
    public static String convertWikipediaToDBpedia(String wikipediaPage){
        String dbpediaPage = wikipediaPage.replaceAll("en.wikipedia.org/wiki", "dbpedia.org/resource")
                .replaceAll("https://", "http://");
        return dbpediaPage;
    }

    public static String getDataURLfromResource(String string){
        String result = string.replace("/resource/", "/data/");
        result += ".ttl";
        return result;
    }

    public static String getArtistImageURL(Resource artistResource){
        Statement statement = artistResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/thumbnail"));
        if(statement!=null){
            return statement.getObject().toString();
        }
        return "";
    }

    public static String getCoverArtURLFromMusicBrainz(String musicbrainzID) throws IOException {
        try {
            JSONObject json = new JSONObject(IOUtils.toString(new URL("http://coverartarchive.org/release-group/" + musicbrainzID), StandardCharsets.UTF_8));
            String url = json.getJSONArray("images").getJSONObject(0).getString("image");
            //System.out.println(url);
            return url;
        }catch(Exception ex){
            return "";
        }
    }

    public static String convertRunTimeToCorrectTime(double num){
        double sec = num - (int) num;
        sec *= 60;
        //String time = (int) num + ":" + (int)sec;
        String time = (int) num + " min " + (int)sec + " sec";
        //System.out.println(time);
        return time;
    }

    public static LocalDateTime parseDate(String dateString){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return LocalDateTime.from(f.parse(dateString));
    }


}
