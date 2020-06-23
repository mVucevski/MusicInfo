package com.mvucevski.wbs.musicinfospring.service.old;

import com.mvucevski.wbs.musicinfospring.CONSTANTS;
import com.mvucevski.wbs.musicinfospring.Helper;
import com.mvucevski.wbs.musicinfospring.model.Song;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService_Old {

    public String getSongURI(String artistName, String songName){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?song\n" +
                "WHERE{\n" +
                "    ?song rdfs:label \"%s\"@en .\n" +
                "    ?music wdt:P279+ wd:Q2188189 .\n" +
                "    ?song wdt:P31 ?music .\n" +
                "    ?song wdt:P175 ?artist .\n" +
                "    ?artist rdfs:label \"%s\"@en\n" +
                "}", songName, artistName);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return "";
            }

            QuerySolution result = resultSet.nextSolution();

            String songURI = "";
            String songURI_ID = "";
            Resource song = result.getResource("song");
            if(song != null){
                songURI = song.getURI();
                String[] splitedParts = songURI.split("/");
                songURI_ID = splitedParts[splitedParts.length - 1];
            }

            return songURI_ID;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return "";
    }

    public Song getSongInfo(String songURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT ?wikipage ?date ?album ?albumLabel ?videoURL ?musicbrainz\n" +
                "WHERE{\n" +
                "?wikipage schema:about wd:%s; ## song uri\n" +
                "              schema:inLanguage ?lang ;\n" +
                "              schema:name ?title ;\n" +
                "              schema:isPartOf [ wikibase:wikiGroup \"wikipedia\" ] .\n" +
                "FILTER(?lang = 'en') .\n" +
                "FILTER (!CONTAINS(?title, ':')) .\n" +
                "OPTIONAL {wd:%1$s wdt:P1651 ?videoURL . } ## song uri\n" +
                "               OPTIONAL {wd:%1$s  wdt:P577 ?date . }\n" +
                "               OPTIONAL {wd:%1$s  wdt:P361 ?album .\n" +
                "?album rdfs:label ?albumLabel filter (lang(?albumLabel) = \"en\") .}\n" +
                "OPTIONAL {wd:%1$s  wdt:P436 ?musicbrainz}\n" +
                "OPTIONAL {wd:%1$s wdt:P361 ?album .\n" +
                "?album wdt:P436 ?musicbrainz}\n" +
                "}", songURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

            return new Song(songURI,wikipediaPageURI,dateString,albumURI,albumString, videoURL, musicbrainzID);
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return null;
    }

    public Song getSongInfoFromRDF(String rdfURL) throws IOException {

        Model model = ModelFactory.createDefaultModel();

        model.read(Helper.getDataURLfromResource(rdfURL), "TURTLE");

        //model.write(System.out, "N-TRIPLES");

        Resource resourceSong = model.getResource(rdfURL);
        List<Statement> descriptions = resourceSong.listProperties(new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#comment")).toList();

        String songDesc = "";
        Optional<Statement> statement = descriptions.stream().filter(e->e.getLanguage().equals("en")).findFirst();
        if(statement.isPresent()){
            songDesc = statement.get().getLiteral().getString();
        }

        //System.out.println("SongDesc - " + songDesc);

        String runTime = "";
        Statement runTimeStatement = resourceSong.getProperty(new PropertyImpl("http://dbpedia.org/ontology/Work/runtime"));
        if(runTimeStatement != null){
            runTime = runTimeStatement.getLiteral().toString().replaceAll("\\^\\^.*", "");
        }
        //Dbpedia_Queries.convertRunTimeToCorrectTime(3.9434);
        //System.out.println("Runtime - " + runTime);

        String award = "";
        Statement awardStatement = resourceSong.getProperty(new PropertyImpl("http://dbpedia.org/property/award"));
        if(awardStatement != null){
            award = awardStatement.getLiteral().getString();
        }
        //Dbpedia_Queries.convertRunTimeToCorrectTime(3.9434);
        //System.out.println("Award - " + award);

        List<Statement> musicAuthorsStatements = resourceSong.listProperties(new PropertyImpl("http://dbpedia.org/property/music")).toList();
        List<String> musicAuthors = musicAuthorsStatements.stream().map(this::mapFromStatementToStringName).collect(Collectors.toList());

        List<Statement> lyricsAuthorsStatements = resourceSong.listProperties(new PropertyImpl("http://dbpedia.org/property/lyrics")).toList();
        List<String> lyricsAuthors = lyricsAuthorsStatements.stream().map(this::mapFromStatementToStringName).collect(Collectors.toList());

        //System.out.println("Music Authors:");
        musicAuthors.forEach(System.out::println);

        //System.out.println("Lyrics Authors:");
        lyricsAuthors.forEach(System.out::println);

        Song song = new Song();
        song.description = songDesc;
        song.lyricsAuthors = lyricsAuthors;
        song.musicAuthors = musicAuthors;
        song.runTime = runTime;
        song.award = award;

        return song;
    }

    public String mapFromStatementToStringName(Statement e){
        RDFNode node = e.getObject();
        if(node.isLiteral()){
            return e.getLiteral().getString();
        }
        else if(node.isResource()){
            Model model_tmp = ModelFactory.createDefaultModel();
            String uri = e.getResource().getURI();
            model_tmp.read(uri, "TURTLE");
            Resource res = model_tmp.getResource(uri);
            return res.getProperty(new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#label")).getLiteral().getString();
        }
        return "";
    }

}
