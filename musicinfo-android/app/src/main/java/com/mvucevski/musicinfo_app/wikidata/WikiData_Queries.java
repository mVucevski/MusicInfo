package com.mvucevski.musicinfo_app.wikidata;

import com.mvucevski.musicinfo_app.CONSTANTS;
import com.mvucevski.musicinfo_app.model.Artist;
import com.mvucevski.musicinfo_app.model.Award;
import com.mvucevski.musicinfo_app.model.Song;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.*;

public class WikiData_Queries {

    // Check if musician or band exists
    public static Boolean artistExists(String artist){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("ASK\n" +
                "WHERE\n" +
                "{\n" +
                "  {?band wdt:P279+ wd:Q2088357 . # get all subclasses of musical ensamble\n" +
                "  ?finalBand wdt:P31 ?band .\n" +
                "  ?finalBand rdfs:label \"%s\"@en .\n" +
                "  ?finalBand rdfs:label ?aristLabel filter (lang(?aristLabel) = \"en\").\n" +
                "  }\n" +
                "  UNION\n" +
                "  {\n" +
                "  VALUES ?professions {wd:Q177220 wd:Q639669} # singer, musician\n" +
                "  ?singer wdt:P31 wd:Q5 . # human\n" +
                "  ?singer wdt:P106 ?professions .\n" +
                "  ?singer rdfs:label \"%s\"@en .\n" +
                "  ?singer rdfs:label ?aristLabel filter (lang(?aristLabel) = \"en\").\n" +
                "  }\n" +
                "}", artist, artist);

        Query query = QueryFactory.create(queryString);

        try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            return qExec.execAsk();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static String getArtistURI(String artist){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?artist\n" +
                "WHERE\n" +
                "{\n" +
                "  {?band wdt:P279+ wd:Q2088357 . # get all subclasses of musical ensamble\n" +
                "  ?artist wdt:P31 ?band .\n" +
                "  ?artist rdfs:label \"%s\"@en .\n" +
                "  }\n" +
                "  UNION\n" +
                "  {\n" +
                "  VALUES ?professions {wd:Q177220 wd:Q639669} # singer, musician\n" +
                "  ?artist wdt:P31 wd:Q5 . # human\n" +
                "  ?artist wdt:P106 ?professions .\n" +
                "  ?artist rdfs:label \"%s\"@en .\n" +
                "  }\n" +
                "}", artist, artist);

        Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
        try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();

            Resource artistResource = result.getResource("artist");
            return artistResource.getURI();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static HashMap<String,String> getArtistLinks(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT *\n" +
                "WHERE\n" +
                "{ \n" +
                "  OPTIONAL  { %1$s wdt:P1902 ?spotify. }\n" +
                "  OPTIONAL  { %1$s wdt:P434 ?musicbrainz. }\n" +
                "  OPTIONAL  { %1$s wdt:P2850 ?itunes. } \n" +
                "  OPTIONAL  { %1$s wdt:P3040 ?soundcloud. } \n" +
                "  OPTIONAL  { %1$s wdt:P2397 ?youtube. }\n" +
                "  OPTIONAL  { %1$s wdt:P2002 ?twitter. } \n" +
                "  OPTIONAL  { %1$s wdt:P2003 ?instagram. } \n" +
                "  OPTIONAL  { %1$s wdt:P4198 ?googlemusic. } \n" +
                "  OPTIONAL  { %1$s wdt:P2013 ?facebook. } \n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();
            int num_links = CONSTANTS.SocialLinks.length;
            HashMap<String, String> map = new HashMap<>();

            for(int i=0; i<num_links; i++){


                Literal literal = result.getLiteral(CONSTANTS.SocialLinks[i]);
                String value = "";
                if(literal != null){
                    value = literal.getString();
                }

                //System.out.println("KEY: " + CONSTANTS.SocialLinks[i] + " | value: " + value);
                map.put(CONSTANTS.SocialLinks[i], value);
            }
            return map;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
        }
        return null;
    }

    public static String[] getArtistDescAndImage(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT ?description ?image\n" +
                "WHERE\n" +
                "{\n" +
                "      %s schema:description ?description filter (lang(?description) = \"en\").\n" +
                "      OPTIONAL {%1$s wdt:P18 ?image .}\n" +
                "} LIMIT 1", artistURI);

        Query query = QueryFactory.create(queryString);
        try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();
            String shortDesc = result.getLiteral("description").getString();
            String imageName = result.getResource("image").getURI();

            if(imageName == null)
                imageName = "";

            return new String[]{shortDesc, imageName};
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return null;
    }

    public static List<String> getArtistGenres(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?genreLabel \n" +
                "WHERE {\n" +
                "%s wdt:P136 ?genre .\n" +
                "?genre rdfs:label ?genreLabel filter (lang(?genreLabel) = \"en\").\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();

            List<String> genres = new LinkedList<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String genre = result_row.getLiteral("genreLabel").getString();
                genres.add(genre);

            }

            return genres;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return Collections.emptyList();
    }

    public static String getArtistWikipediaPage(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?wikipage ?title\n" +
                "WHERE {\n" +
                "?wikipage schema:about %s ;\n" +
                "              schema:inLanguage ?lang ;\n" +
                "              schema:name ?title ;\n" +
                "              schema:isPartOf [ wikibase:wikiGroup \"wikipedia\" ] .\n" +
                "  FILTER(?lang = 'en') .\n" +
                "  FILTER (!CONTAINS(?title, ':')) .\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();

            Resource wikipage = result.getResource("wikipage");
            return wikipage.getURI();
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return null;
    }

    public static Map<?, ?> getArtistAlbums(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?album ?albumLabel WHERE {\n" +
                "?album wdt:P31/wdt:P279* wd:Q482994 ;\n" +
                "       wdt:P175 %s ;\n" +
                "       rdfs:label ?albumLabel filter (lang(?albumLabel) = \"en\").\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();

            HashMap<String, String> albums = new HashMap<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String albumName = result_row.getLiteral("albumLabel").getString();
                String albumURI = result_row.getResource("album").getURI();
                albums.put(albumURI,albumName);
            }

            return albums;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return Collections.emptyMap();
    }

    public static List<String> getArtistRecordLabels(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?recordL ?recordLabel WHERE {\n" +
                "       %s wdt:P264 ?recordL .\n" +
                "       ?recordL rdfs:label ?recordLabel filter (lang(?recordLabel) = \"en\").\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();

            List<String> recordLabels = new LinkedList<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String recordLabel = result_row.getLiteral("recordLabel").getString();
                recordLabels.add(recordLabel);

            }

            return recordLabels;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return Collections.emptyList();
    }

    public static List<Award> getArtistAwards(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT ?awardLabel ?date ?forWorkLabel\n" +
                "WHERE {\n" +
                "  %s p:P166 ?awardstatement .\n" +
                "  ?awardstatement ps:P166 ?award .\n" +
                "  ?award rdfs:label ?awardLabel filter (lang(?awardLabel) = \"en\") .\n" +
                "  OPTIONAL {?awardstatement pq:P585 ?date . }\n" +
                "  OPTIONAL {?awardstatement pq:P1686 ?forWork .\n" +
                "  ?forWork rdfs:label ?forWorkLabel filter (lang(?forWorkLabel) = \"en\")}\n" +
                "}ORDER BY DESC(?date)", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();

            List<Award> awards = new ArrayList<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String awardTitle = result_row.getLiteral("awardLabel").getString();
                Literal awardForLiteral = result_row.getLiteral("forWorkLabel");
                Literal dateLiteral = result_row.getLiteral("date");

                String awardFor = "";
                String date = "";
                if(awardForLiteral != null){
                     awardFor = awardForLiteral.getString();
                }
                if(dateLiteral != null){
                    date = dateLiteral.getString();
                }

                Award award = new Award(awardTitle,awardFor,date);
                awards.add(award);
            }

            return awards;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return Collections.emptyList();
    }

    public static List<String> getArtistBirthNameOrBandMembersNames(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?name\n" +
                "WHERE {\n" +
                "  {%s wdt:P527 ?member .\n" +
                "  ?member rdfs:label ?name filter (lang(?name) = \"en\") .}\n" +
                "UNION\n" +
                "  {%1$s wdt:P1477 ?name .}\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();

            List<String> membersNames = new LinkedList<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String recordLabel = result_row.getLiteral("name").getString();
                membersNames.add(recordLabel);
            }

            return membersNames;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return Collections.emptyList();
    }

    public static String getArtistStartYear(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT ?activeYear\n" +
                "WHERE {\n" +
                "%s wdt:P2031 ?activeYear .\n" +
                "} ORDER BY ?activeYear LIMIT 1", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();

            Literal date = result.getLiteral("activeYear");
            String dateString = "";
            if(date!=null)
                dateString = date.getString();

            return dateString;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return "";
    }

    public static List<String> getSimilarArtists(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format(
                "SELECT DISTINCT ?artist (SAMPLE(?aristLabel) as ?artistName) (SAMPLE(?genreLabel) as ?genreName)\n" +
                "WHERE {\n" +
                "%s wdt:P136 ?genre .\n" +
                "?artist wdt:P136 ?genre .\n" +
                "?artist rdfs:label ?aristLabel .\n" +
                "filter (lang(?aristLabel) = \"en\").\n" +
                "?genre rdfs:label ?genreLabel\n" +
                "filter (lang(?genreLabel) = \"en\").\n" +
                "{SELECT ?artist ?genre\n" +
                "WHERE{\n" +
                "{?artist wdt:P31 wd:Q5 .} # human\n" +
                "UNION\n" +
                "{?band wdt:P279+ wd:Q2088357 . # musical ensamble\n" +
                "?artist wdt:P31 ?band .}\n" +
                "}}}\n" +
                "GROUP BY ?artist\n" +
                "ORDER BY RAND() LIMIT 10", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();

            List<String> similarArtists = new LinkedList<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String similarArtistURI = result_row.getResource("artist").getURI();

                similarArtists.add(similarArtistURI);
            }

            return similarArtists;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }

        return Collections.emptyList();
    }

    public static String getSongURI(String songName, String artistName){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?song\n" +
                "WHERE{\n" +
                "    ?song rdfs:label \"%s\"@en .\n" +
                "    ?music wdt:P279+ wd:Q2188189 .\n" +
                "    ?song wdt:P31 ?music .\n" +
                "    ?song wdt:P175 ?artist .\n" +
                "    ?artist rdfs:label \"%s\"@en\n" +
                "}", songName, artistName);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return "";
            }

            QuerySolution result = resultSet.nextSolution();

            String songURI = "";
            Resource song = result.getResource("song");
            if(song != null){
                songURI = song.getURI();
            }

            return songURI;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return "";
    }

    public static Song getSongInfo(String songURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT ?wikipage ?date ?album ?albumLabel ?videoURL ?musicbrainz\n" +
                "WHERE{\n" +
                "?wikipage schema:about %s; ## song uri\n" +
                "              schema:inLanguage ?lang ;\n" +
                "              schema:name ?title ;\n" +
                "              schema:isPartOf [ wikibase:wikiGroup \"wikipedia\" ] .\n" +
                "FILTER(?lang = 'en') .\n" +
                "FILTER (!CONTAINS(?title, ':')) .\n" +
                "OPTIONAL {%1$s wdt:P1651 ?videoURL . } ## song uri\n" +
                "               OPTIONAL {%1$s  wdt:P577 ?date . }\n" +
                "               OPTIONAL {%1$s  wdt:P361 ?album .\n" +
                "?album rdfs:label ?albumLabel filter (lang(?albumLabel) = \"en\") .}\n" +
                "OPTIONAL {%1$s  wdt:P436 ?musicbrainz}\n" +
                "}", songURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();

            String wikipediaPageURI = result.getResource("wikipage").getURI();
            String dateString = "";
            String albumURI = "";
            String albumString = "";
            String videoURL = "";
            String musicbrainzID = "";
            Literal date = result.getLiteral("date");
            Resource albumRes = result.getResource("album");
            Literal albumLiteral = result.getLiteral("albumLabel");
            Literal videoLiteral = result.getLiteral("videoURL");
            Literal musicbrainzLiteral = result.getLiteral("musicbrainz");

            if(date!=null) dateString = date.getString();
            if(albumRes!=null) albumURI = albumRes.getURI();
            if(albumLiteral!=null) albumString = albumLiteral.getString();
            if(videoLiteral!=null) videoURL = videoLiteral.getString();
            if(musicbrainzLiteral!=null) musicbrainzID = musicbrainzLiteral.getString();

            return null;
            //return new Song(songURI,wikipediaPageURI,dateString,albumURI,albumString, videoURL, musicbrainzID);
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return null;
    }

    public static Artist getArtistInfo(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?wikipage ?title ?description ?image ?activeYear\n" +
                "WHERE {\n" +
                "OPTIONAL {?wikipage schema:about %s ;\n" +
                "              schema:inLanguage ?lang ;\n" +
                "              schema:name ?title ;\n" +
                "              schema:isPartOf [ wikibase:wikiGroup \"wikipedia\" ] .\n" +
                "  FILTER(?lang = 'en') .\n" +
                "  FILTER (!CONTAINS(?title, ':')) . }\n" +
                "OPTIONAL{%1$s schema:description ?description\n" +
                "                   FILTER (lang(?description) = \"en\").}\n" +
                "OPTIONAL {%1$s wdt:P18 ?image .}\n" +
                "OPTIONAL {%1$s wdt:P2031 ?activeYear .}\n" +
                "} ORDER BY ?activeYear LIMIT 1", artistURI);

        Query query = QueryFactory.create(queryString);
         try{
            QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query);
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();

            String wikipediaPageURI = "";
            String careerStartYearString = "";
            String shortDesc = "";
            String imageURL = "";
            String artistName = "";

            Resource wikipediaPageResource = result.getResource("wikipage");
            Literal careerStartYearLiteral = result.getLiteral("activeYear");
            Literal artistNameLiteral = result.getLiteral("title");
            Resource imageResource = result.getResource("image");
            Literal shortDescLiteral = result.getLiteral("description");

            if(careerStartYearLiteral!=null) careerStartYearString = careerStartYearLiteral.getString();
            if(wikipediaPageResource!=null) wikipediaPageURI = wikipediaPageResource.getURI();
            if(shortDescLiteral!=null) shortDesc = shortDescLiteral.getString();
            if(imageResource!=null) imageURL = imageResource.getURI();
            if(artistNameLiteral!=null) artistName = artistNameLiteral.getString();

            Artist artist = new Artist();
//            artist.name = artistName;
//            artist.DBPediaURI = convertWikipediaToDBpedia(wikipediaPageURI);
//            artist.careerStartYearString = careerStartYearString;
//            artist.imageURL = imageURL;
//            artist.shortDescription = shortDesc;


            return artist;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return null;
    }

    public static String convertWikipediaToDBpedia(String wikipediaPage){
        String dbpediaPage = wikipediaPage.replaceAll("en.wikipedia.org/wiki", "dbpedia.org/resource");
        return dbpediaPage;
    }

}