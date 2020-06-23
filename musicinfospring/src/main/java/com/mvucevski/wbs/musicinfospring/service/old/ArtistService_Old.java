package com.mvucevski.wbs.musicinfospring.service.old;

import com.mvucevski.wbs.musicinfospring.CONSTANTS;
import com.mvucevski.wbs.musicinfospring.Helper;
import com.mvucevski.wbs.musicinfospring.model.Artist;
import com.mvucevski.wbs.musicinfospring.model.Award;
import com.mvucevski.wbs.musicinfospring.model.SimilarArtist;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mvucevski.wbs.musicinfospring.Helper.convertWikipediaToDBpedia;

@Service
public class ArtistService_Old {

    public String getArtistWikiDataURI(String artistName){
        artistName = artistName.replace(" The", " the");
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
                "  ?artist rdfs:label \"%1$s\"@en .\n" +
                "  }\n" +
                "}", artistName);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
            System.out.println("QUERRR" + qExec.getQuery());
            ResultSet resultSet = qExec.execSelect();
            if(!resultSet.hasNext()){
                return null;
            }

            QuerySolution result = resultSet.nextSolution();

            Resource artistResource = result.getResource("artist");
            return artistResource.getURI().replaceAll("http://www.wikidata.org/entity/", "");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public String getArtistWikipediaPage(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?wikipage ?title\n" +
                "WHERE {\n" +
                "?wikipage schema:about wd:%s ;\n" +
                "              schema:inLanguage ?lang ;\n" +
                "              schema:name ?title ;\n" +
                "              schema:isPartOf [ wikibase:wikiGroup \"wikipedia\" ] .\n" +
                "  FILTER(?lang = 'en') .\n" +
                "  FILTER (!CONTAINS(?title, ':')) .\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

    public HashMap<String,String> getArtistSocialLinks(String artistURI){


        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT *\n" +
                "WHERE\n" +
                "{ \n" +
                "  OPTIONAL  { wd:%1$s wdt:P1902 ?spotify. }\n" +
                "  OPTIONAL  { wd:%1$s wdt:P434 ?musicbrainz. }\n" +
                "  OPTIONAL  { wd:%1$s wdt:P2850 ?itunes. } \n" +
                "  OPTIONAL  { wd:%1$s wdt:P3040 ?soundcloud. } \n" +
                "  OPTIONAL  { wd:%1$s wdt:P2397 ?youtube. }\n" +
                "  OPTIONAL  { wd:%1$s wdt:P2002 ?twitter. } \n" +
                "  OPTIONAL  { wd:%1$s wdt:P2003 ?instagram. } \n" +
                "  OPTIONAL  { wd:%1$s wdt:P4198 ?googlemusic. } \n" +
                "  OPTIONAL  { wd:%1$s wdt:P2013 ?facebook. } \n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

                map.put(CONSTANTS.SocialLinks[i], value);
            }
            return map;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
        }
        return null;
    }

    public List<String> getArtistGenres(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?genreLabel \n" +
                "WHERE {\n" +
                "wd:%s wdt:P136 ?genre .\n" +
                "?genre rdfs:label ?genreLabel filter (lang(?genreLabel) = \"en\").\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

    public Artist getArtistInfo(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?wikipage ?title ?description ?image ?activeYear\n" +
                "WHERE {\n" +
                "OPTIONAL {?wikipage schema:about wd:%s ;\n" +
                "              schema:inLanguage ?lang ;\n" +
                "              schema:name ?title ;\n" +
                "              schema:isPartOf [ wikibase:wikiGroup \"wikipedia\" ] .\n" +
                "  FILTER(?lang = 'en') .\n" +
                "  FILTER (!CONTAINS(?title, ':')) . }\n" +
                "OPTIONAL{wd:%1$s schema:description ?description\n" +
                "                   FILTER (lang(?description) = \"en\").}\n" +
                "OPTIONAL {wd:%1$s wdt:P18 ?image .}\n" +
                "OPTIONAL {wd:%1$s wdt:P2031 ?activeYear .}\n" +
                "} ORDER BY ?activeYear LIMIT 1", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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
            artist.name = artistName;
            artist.DBPediaURI = convertWikipediaToDBpedia(wikipediaPageURI);
            artist.careerStartYearString = careerStartYearString;
            artist.imageURL = imageURL;
            artist.shortDescription = shortDesc;


            return artist;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getLocalizedMessage());
        }
        return null;
    }

    public Map<String, String> getArtistAlbums(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?album ?albumLabel WHERE {\n" +
                "?album wdt:P31/wdt:P279* wd:Q482994 ;\n" +
                "       wdt:P175 wd:%s ;\n" +
                "       rdfs:label ?albumLabel filter (lang(?albumLabel) = \"en\").\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

    public List<String> getArtistRecordLabels(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?recordL ?recordLabel WHERE {\n" +
                "       wd:%s wdt:P264 ?recordL .\n" +
                "       ?recordL rdfs:label ?recordLabel filter (lang(?recordLabel) = \"en\").\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

    public List<Award> getArtistAwards(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT ?awardLabel ?date ?forWorkLabel\n" +
                "WHERE {\n" +
                "  wd:%s p:P166 ?awardstatement .\n" +
                "  ?awardstatement ps:P166 ?award .\n" +
                "  ?award rdfs:label ?awardLabel filter (lang(?awardLabel) = \"en\") .\n" +
                "  OPTIONAL {?awardstatement pq:P585 ?date . }\n" +
                "  OPTIONAL {?awardstatement pq:P1686 ?forWork .\n" +
                "  ?forWork rdfs:label ?forWorkLabel filter (lang(?forWorkLabel) = \"en\")}\n" +
                "}ORDER BY DESC(?date)", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

    public List<String> getArtistBirthNameOrBandMembersNames(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format("SELECT DISTINCT ?name\n" +
                "WHERE {\n" +
                "  {wd:%s wdt:P527 ?member .\n" +
                "  ?member rdfs:label ?name filter (lang(?name) = \"en\") .}\n" +
                "UNION\n" +
                "  {wd:%1$s wdt:P1477 ?name .}\n" +
                "}", artistURI);

        Query query = QueryFactory.create(queryString);
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
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

    public List<SimilarArtist> getSimilarArtists(String artistURI){
        String queryString = CONSTANTS.WikiData_prefixes + String.format(
                "SELECT DISTINCT ?artist (SAMPLE(?aristLabel) as ?artistName) (SAMPLE(?genreLabel) as ?genreName)\n" +
                        "WHERE {\n" +
                        "wd:%s wdt:P136 ?genre .\n" +
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
        try(QueryExecution qExec = QueryExecutionFactory.sparqlService(CONSTANTS.WikiData_SPARQLEndpoint, query)){
            ResultSet resultSet = qExec.execSelect();

            List<SimilarArtist> similarArtists = new LinkedList<>();

            while(resultSet.hasNext()){
                QuerySolution result_row = resultSet.nextSolution();

                String similarArtistURI = result_row.getResource("artist").getURI();
                String similarArtistName = result_row.getLiteral("artistName").getString();
                String similarArtistGenre = result_row.getLiteral("genreName").getString();
                similarArtists.add(new SimilarArtist(similarArtistURI,similarArtistName,similarArtistGenre));
            }

            return similarArtists;
        }catch (Exception ex){
            System.out.println("EXCEPTION - ERROR");
            ex.getCause();
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return Collections.emptyList();
    }

    public String[] getFullArtistDescriptionFromRDF(String rdfURL){
        Model model = ModelFactory.createDefaultModel();

        model.read(Helper.getDataURLfromResource(rdfURL), "TURTLE");
        Resource resourceArtist = model.getResource(rdfURL);

        List<Statement> descriptions = resourceArtist.listProperties(new PropertyImpl("http://dbpedia.org/ontology/abstract")).toList();

        String artistDesc = "";
        Optional<Statement> statement = descriptions.stream().filter(e->e.getLanguage().equals("en")).findFirst();

        if(statement.isPresent()){
            artistDesc = statement.get().getLiteral().getString();
        }

        //Get Artist Image
        String imageURL = Helper.getArtistImageURL(resourceArtist);

        return new String[]{artistDesc, imageURL};
    }


}
