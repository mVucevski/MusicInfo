package com.mvucevski.wbs.musicinfospring.web;

import com.mvucevski.wbs.musicinfospring.CONSTANTS;
import com.mvucevski.wbs.musicinfospring.model.Artist;
import com.mvucevski.wbs.musicinfospring.model.Award;
import com.mvucevski.wbs.musicinfospring.model.SimilarArtist;
import com.mvucevski.wbs.musicinfospring.service.old.ArtistService_Old;
import com.mvucevski.wbs.musicinfospring.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.mvucevski.wbs.musicinfospring.CONSTANTS.SocialLinks;

@RestController
public class ArtistController {

    @Autowired
    private ArtistService_Old artistServiceOld;

    @Autowired
    private ArtistService artistService;

    @RequestMapping("/artist/{name}")
    public String getArtistWikiDataURI(@PathVariable String name){
        System.out.println("GET ARTIST WIKIDAA URI:" + name);

        String artistURI = artistService.getArtistWikiDataURI(name);
        if(artistURI == null){
            System.out.println("ERROR: Artist DOESNT EXIST");
            return "";
        }
            return artistURI;
        //return "Q56379599";
    }

    @RequestMapping("/artist/{wikidataURI}/sociallinks")
    public HashMap<String,String> getArtistSocial(@PathVariable String wikidataURI){
        HashMap<String, String> socialLinks_IDs = artistServiceOld.getArtistSocialLinks(wikidataURI);
        if(socialLinks_IDs != null){
            for(int i = 0; i< SocialLinks.length; i++){
                String value = socialLinks_IDs.get(SocialLinks[i]);
                if(value.length()>0)
                    socialLinks_IDs.replace(SocialLinks[i], CONSTANTS.SocialLinks_URL[i] + value);
            }
            // example => key: youtube | value: youtube.com/artist_channel_id
            //socialLinks_IDs.keySet().forEach(i-> System.out.println(i + ": " + socialLinks_IDs.get(i)));
            return socialLinks_IDs;
        }
        return null;
    }

    @RequestMapping("/artist/{wikidataURI}/genres")
    public List<String> getArtistGenres(@PathVariable String wikidataURI){
        List<String> genres = artistServiceOld.getArtistGenres(wikidataURI);
        if(genres != null)
            return genres;
        return Collections.emptyList();
    }

    @RequestMapping("/artist/{wikidataURI}/info")
    public Artist getArtistInfo(@PathVariable String wikidataURI){
        Artist artist = artistServiceOld.getArtistInfo(wikidataURI);
        if(artist!=null){
            String[] descAndImage = artistServiceOld.getFullArtistDescriptionFromRDF(artist.DBPediaURI);
            artist.longDescription = descAndImage[0];
            artist.imageURL = descAndImage[1];
        }
        return artist;
    }

    @RequestMapping("/artist/{wikidataURI}/albums")
    public Map<String, String>  getArtistAlbums(@PathVariable String wikidataURI){
        Map<String, String> albums = artistServiceOld.getArtistAlbums(wikidataURI);
        return albums;
    }

    @RequestMapping("/artist/{wikidataURI}/recordlabels")
    public List<String> getArtistRecordLabels(@PathVariable String wikidataURI){
        List<String> recordlabels = artistServiceOld.getArtistRecordLabels(wikidataURI);
        if(recordlabels != null)
            return recordlabels;
        return Collections.emptyList();
    }

    @RequestMapping("/artist/{wikidataURI}/awards")
    public List<Award> getArtistAwards(@PathVariable String wikidataURI){
        List<Award> awards = artistServiceOld.getArtistAwards(wikidataURI);
        return awards;
    }

    @RequestMapping("/artist/{wikidataURI}/artistFullNames")
    public List<String> getArtistOrBandMembersFullNames(@PathVariable String wikidataURI){
        List<String> names = artistServiceOld.getArtistBirthNameOrBandMembersNames(wikidataURI);
        if(names != null)
            return names;
        return Collections.emptyList();
    }

    @RequestMapping("/artist/{wikidataURI}/wikipedia")
    public String getArtistWikipediaURI(@PathVariable String wikidataURI){
        String artistWIKI = artistServiceOld.getArtistWikipediaPage(wikidataURI);
        if(artistWIKI == null){
            System.out.println("ERROR: Artist DOESNT EXIST");
            return "ERROR";
        }
        return artistWIKI;
    }

    @RequestMapping("/artist/{wikidataURI}/similarArtists")
    public List<SimilarArtist> getSimilarArtists(@PathVariable String wikidataURI){
        List<SimilarArtist> similarArtists = artistService.getSimilarArtists(wikidataURI);
        if(similarArtists != null)
            return similarArtists;
        return Collections.emptyList();
    }

    @RequestMapping("/artist/{wikidataURI}/all")
    public Artist getFullArtistInfo(@PathVariable String wikidataURI) throws Exception {
//        Artist artist = new Artist();
//
//        Award aw1 = new Award("Academy Award for Best Original Song", "Shallow", "2008-09-29T00:00:00Z");
//        Award aw2 = new Award("American Music Award for Favorite Pop/Rock Female Artist", "", "2017-01-01T00:00:00Z");
//
//        artist.awards = new LinkedList<>(Arrays.asList(aw1,aw2));
//        artist.genres = new LinkedList<>(Arrays.asList("country music",
//                "art pop",
//                "Europop",
//                "electropop",
//                "dance-pop",
//                "synth-pop",
//                "electronic dance muusic"));
//
//        Album a1 = new Album();
//        a1.name = "Chromatica";
//        a1.WikiDataURI = "http://www.wikidata.org/entity/Q86919359";
//
//        Album a2 = new Album();
//        a2.name = "Born This Way";
//        a2.WikiDataURI = "http://www.wikidata.org/entity/Q164646";
//
//        artist.albums = new LinkedList<>(Arrays.asList(a1,a2));
//        artist.similarArtists = new LinkedList<>();
//        artist.recordLabels = new LinkedList<>(Arrays.asList("Interscope Records", "Universal Music Group"));
//
//        artist.socialLinks = new HashMap<>();
//        artist.socialLinks.put(CONSTANTS.SocialLinks[0],"1HY2Jd0NmPuamShAr6KMms");
//        artist.socialLinks.put(CONSTANTS.SocialLinks[1],"650e7db6-b795-4eb5-a702-5ea2fc46c848");
//        artist.socialLinks.put(CONSTANTS.SocialLinks[3],"ladygaga");
//
//        artist.WikiDataURI = "Q56379599";
//        artist.DBPediaURI = "http://dbpedia.org/resource/Lady_Gaga";
//        artist.shortDescription = "American singer, songwriter, and actress";
//        artist.longDescription = "Stefani Joanne Angelina Germanotta (/ˈstɛfəniː dʒɜːrməˈnɒtə/ STEF-ə-nee jur-mə-NOT-ə; born March 28, 1986), known professionally as Lady Gaga, is an American singer, songwriter, and actress. She performed initially in theater, appearing in high school plays, and studied at CAP21 through New York University's Tisch School of the Arts before dropping out to pursue a musical career. After leaving a rock band, participating in the Lower East Side's avant garde performance art circuit, and being dropped from a contract with Def Jam Recordings, Gaga worked as a songwriter for Sony/ATV Music Publishing. From there, recording artist Akon noticed her vocal abilities and helped her to sign a joint deal with Interscope Records and his own KonLive Distribution. Her debut album The Fame (2008) was a critical and commercial success that produced global chart-topping singles such as \\\"Just Dance\\\" and \\\"Poker Face\\\". A follow-up extended play (EP), The Fame Monster (2009)";
//        artist.imageURL = "http://i.imgur.com/AF6DMJ1.jpg";//"http://commons.wikimedia.org/wiki/Special:FilePath/TIFF%202018%20Lady%20Gaga%20cropped%20version.jpg";
//        artist.name = "Lady Gaga Name";
//        artist.careerStartYear = 2005;
//        artist.careerStartYearString = "2005-01-01T00:00:00Z";

        //return artist;
        return artistService.getCompleteArtistBundle(wikidataURI);
    }



}
