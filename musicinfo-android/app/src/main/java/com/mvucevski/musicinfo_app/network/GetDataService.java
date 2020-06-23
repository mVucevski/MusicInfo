package com.mvucevski.musicinfo_app.network;

import com.mvucevski.musicinfo_app.model.Artist;
import com.mvucevski.musicinfo_app.model.Song;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("song/{artistName}/{songName}")
    Call<ResponseBody> getSongURI(@Path("artistName") String artistName, @Path("songName") String songName);

    @GET("song/{wikiDataURI}/all")
    Call<Song> getSong(@Path("wikiDataURI") String wikiDataURI);

    @GET("artist/{artistName}")
    Call<ResponseBody> getArtistURI(@Path("artistName") String artistName);

    @GET("artist/{wikiDataURI}/all")
    Call<Artist> getArtist(@Path("wikiDataURI") String wikiDataURI);


}
