package com.mvucevski.wbs.musicinfospring.web;

import com.mvucevski.wbs.musicinfospring.model.Song;
import com.mvucevski.wbs.musicinfospring.service.old.SongService_Old;
import com.mvucevski.wbs.musicinfospring.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.mvucevski.wbs.musicinfospring.Helper.getCoverArtURLFromMusicBrainz;


@RestController
public class SongController {

    @Autowired
    private SongService_Old songServiceOld;

    @Autowired
    private SongService songService;

    @RequestMapping("/song/{artistname}/{songname}")
    public String getSongWikiDataURI(@PathVariable String artistname, @PathVariable String songname){
        String songURI = songService.getSongURI(artistname,songname);
        return songURI != null ? songURI : "";
       // return "Q131182";
    }

    @RequestMapping("/song/{wikidataURI}")
    public Song getSongInfo(@PathVariable String wikidataURI) throws IOException {
       Song song = songServiceOld.getSongInfo(wikidataURI);

       if(song!=null){
           if(!song.musicbrainzID.isEmpty()){
               song.coverURL =  getCoverArtURLFromMusicBrainz(song.musicbrainzID);
           }

           Song song2 = songServiceOld.getSongInfoFromRDF(song.DBPediaURI);
           if(song2!=null){
               song.description = song2.description;
               song.lyricsAuthors = song2.lyricsAuthors;
               song.musicAuthors = song2.musicAuthors;
               song.runTime = song2.runTime;
               song.award = song2.award;
           }
       }

       return song;
    }

    @RequestMapping("/song/{wikidataURI}/all")
    public Song getFullSongInfo(@PathVariable String wikidataURI) throws IOException {

        return songService.getCompleteSongBundle(wikidataURI);
//        Song song = new Song();
//        song.WikiDataURI = "Q153029";
//                song.DBPediaURI = "http://dbpedia.org/resource/Poker_Face_(Lady_Gaga_song)";
//        song.date = "2008-09-29T00:00:00Z";
//        song.albumWikiDataURI= "http://www.wikidata.org/entity/Q131182";
//        song.albumName = "The Fame";
//        song.videoURL= "bESGLojNYSo";
//        song.musicbrainzID= "df1e409f-5fc6-3235-a509-dbee65ed6e23";
//        song.artistName= null;
//        song.musicAuthors= new ArrayList<>();
//        song.lyricsAuthors= new ArrayList<>();
//        song.award= "Platinum";
//        song.runTime= convertRunTimeToCorrectTime(Double.parseDouble("3.966666666666667"));
//        song.description= "\"Poker Face\" is a song by American singer Lady Gaga from her debut studio album, The Fame (2008). Produced by RedOne, it was released as the album's second single in late 2008 for some markets and in early 2009 for the rest of the world. \"Poker Face\" is an uptempo synthpop song in the key of G♯ minor, following in the footsteps of her previous single \"Just Dance\", but with a darker musical tone. The main idea behind the song is bisexuality and was a tribute by Gaga to her rock and roll boyfriends. Lyrically, the track is about a woman engaged in the practice of cockteasing.";
//        song.coverURL= "https://i.imgur.com/zV2Pk7Q.jpg";//"http://coverartarchive.org/release/30c35c54-1f5c-4b3b-911a-c239ac4e08f5/11873899480.jpg";
//
//        return song;
    }

}
