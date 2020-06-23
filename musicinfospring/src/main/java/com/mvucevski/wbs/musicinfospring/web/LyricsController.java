package com.mvucevski.wbs.musicinfospring.web;

import com.mvucevski.wbs.musicinfospring.service.LyricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class LyricsController {

    @Autowired
    private LyricsService lyricsService;

    @RequestMapping("/lyrics/{artistname}/{songname}")
    public String getSongLyrics(@PathVariable String artistname, @PathVariable String songname) throws IOException {
        String lyrics = lyricsService.getSongLyrics(artistname + " - " + songname);
        System.out.println(lyrics);
        return lyrics;
    }
}
