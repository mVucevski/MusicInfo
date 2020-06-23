package com.mvucevski.musicinfo_app.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.mvucevski.musicinfo_app.R;
import com.mvucevski.musicinfo_app.model.Artist;
import com.mvucevski.musicinfo_app.model.Song;
import com.mvucevski.musicinfo_app.network.GetDataService;
import com.mvucevski.musicinfo_app.network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import at.blogc.android.views.ExpandableTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongFragment extends Fragment {

    private ProgressDialog progressDialog;
    private View rootView;
    private boolean loaded, loadError, isUpdated;
    private Song currentSong;
    private FragmentActivity context;
    private String artistName, songName;


    public SongFragment(String artistName, String songName){
        this.artistName = artistName;
        this.songName = songName;
        loaded = false;
        loadError = false;
        isUpdated = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_song, container, false);

        if(!loaded){
            loaded = true;
            if(songName.length()>0 && artistName.length()>0)
                getSongWikiDataURI(artistName, songName);
        }
        context = getActivity();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isUpdated){
            getSongWikiDataURI(artistName, songName);
        }
        else if(currentSong!=null){
            updateFragmentLayout(currentSong);
        }else if(artistName.length() == 0 && songName.length() == 0){
            View card0 = rootView.findViewById(R.id.card0);
            TextView topTitle = card0.findViewById(R.id.tvCardTitle);
            topTitle.setText("No active song!");
            card0.setVisibility(View.VISIBLE);
        }else if(loadError){
            View card0 = rootView.findViewById(R.id.card0);
            TextView topTitle = card0.findViewById(R.id.tvCardTitle);
            topTitle.setText("The service isn't available at the moment!");
            card0.setVisibility(View.VISIBLE);
        }

    }

    private void updateFragmentLayout(Song song){
        //Handle song fragment elements
        Log.i("SONG_FRAGMENT", song.toString());
        loadError = false;
        isUpdated = false;

        View card0 = rootView.findViewById(R.id.card0);
        card0.setVisibility(View.GONE);

        View card1 = rootView.findViewById(R.id.card1);
        TextView topTitle = card1.findViewById(R.id.tvCardTitle);
        topTitle.setText(songName);
        TextView topSubTitle = card1.findViewById(R.id.sub_text);
        topSubTitle.setText("By " + artistName);
        Picasso.Builder builder = new Picasso.Builder(getContext());
        builder.downloader(new OkHttp3Downloader(getContext()));

        if(!song.getCoverURL().isEmpty()){
            builder.build().load(song.getCoverURL())
                    .placeholder((R.drawable.ic_launcher_background))
                    .error(R.drawable.ic_launcher_background)
                    .into((ImageView) card1.findViewById(R.id.media_image));
        }else{
            ImageView iwTopImage = card1.findViewById(R.id.media_image);
            iwTopImage.setImageDrawable(getResources().getDrawable(R.drawable.image_not_available));
        }

        card1.setVisibility(View.VISIBLE);

        View card2 = rootView.findViewById(R.id.card2);

        if(song.getDescription() == null || song.getDescription().length() == 0){
            card2.setVisibility(View.GONE);
        }else{
            TextView tvTitle = card2.findViewById(R.id.tvCardTitle);
            TextView tvSubtitle = card2.findViewById(R.id.tvCardSubtitle);

            tvTitle.setText("About the song");
            tvSubtitle.setText("Description");

            ExpandableTextView tvDesc = card2.findViewById(R.id.tvCardContent);
            tvDesc.setText(song.getDescription());

            Button buttonToggle = card2.findViewById(R.id.button_toggle);
            card2.setVisibility(View.VISIBLE);

            tvDesc.setInterpolator(new OvershootInterpolator());

            buttonToggle.setOnClickListener(v->{
                buttonToggle.setText(tvDesc.isExpanded() ? "Show More" : "Show Less");
                tvDesc.toggle();
            });

            buttonToggle.setVisibility(View.VISIBLE);
        }

        // Testing - Card 3
        rootView.findViewById(R.id.card3).setVisibility(View.GONE);

        // Award(s) card
        View card4 = rootView.findViewById(R.id.card4);
        if(song.getAward().isEmpty()){
            card4.setVisibility(View.GONE);
        }else{
            TextView tvAward = card4.findViewById(R.id.tvCardTitle);
            tvAward.setText(song.getAward());
            ImageView imgAward = card4.findViewById(R.id.media_image);

            switch (song.getAward()){
                case "Gold":
                    imgAward.setImageResource(R.drawable.gold_award);
                    break;
                case "Platinum":
                    imgAward.setImageResource(R.drawable.platinum_award);
                    break;
                default:
                    card4.setVisibility(View.GONE);
                    break;
            }
            card4.setVisibility(View.VISIBLE);
        }

        View card5 = rootView.findViewById(R.id.card5);
        if(song.getRunTime().isEmpty()){
            card5.setVisibility(View.GONE);
        }else{
            TextView tvCardTitle = card5.findViewById(R.id.tvCardTitle);
            tvCardTitle.setText("Duration:");
            TextView tvContent = card5.findViewById(R.id.tvCardContent);
            tvContent.setText(song.getRunTime());
            card5.setVisibility(View.VISIBLE);
        }

        View card6 = rootView.findViewById(R.id.card6);
        if(song.getDate().isEmpty()){
            card6.setVisibility(View.GONE);
        }else{
            TextView tvCardTitle = card6.findViewById(R.id.tvCardTitle);
            tvCardTitle.setText("Release Date:");
            TextView tvContent = card6.findViewById(R.id.tvCardContent);
            tvContent.setText(song.parseDate(song.getDate()));
            card6.setVisibility(View.VISIBLE);
        }

        // Card with list
        View card7 = rootView.findViewById(R.id.card7);
        if(song.getLyricsAuthors().isEmpty()){
            card7.setVisibility(View.GONE);
        }else{
            TextView cardTitle = card7.findViewById(R.id.tvCardTitle);
            cardTitle.setText("Lyrics Authors");
            TextView cardList = card7.findViewById(R.id.tvCardContent);
            cardList.setText(TextUtils.join("\n", song.getLyricsAuthors()));
            card7.setVisibility(View.VISIBLE);
        }

        // Card with list
        View card8 = rootView.findViewById(R.id.card8);
        if(song.getMusicAuthors().isEmpty()){
            card8.setVisibility(View.GONE);
        }else{
            TextView cardTitle = card8.findViewById(R.id.tvCardTitle);
            cardTitle.setText("Music Authors");
            TextView cardList = card8.findViewById(R.id.tvCardContent);
            cardList.setText(TextUtils.join("\n", song.getMusicAuthors()));
            card8.setVisibility(View.VISIBLE);
        }

        // Music Video Link Card
        View card9 = rootView.findViewById(R.id.card9);
        if(song.getVideoURL() == null || song.getVideoURL().isEmpty()){
            card9.setVisibility(View.GONE);
        }else{
            card9.setOnClickListener(v->watchYoutubeVideo(song.getVideoURL()));
            card9.setVisibility(View.VISIBLE);
        }


    }

    private void getSongInfo(String songURI){
        loadingInProgress();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Song> call = service.getSong(songURI);

        call.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                progressDialog.dismiss();

                //addToDataBase(response.body().getSearchMovies());
                //generateDataList(response.body().getSearchMovies());

                System.out.println("SONG_FRAGMENT - SONG INFO: " + call.request().url());
                Log.i("SONG_FRAGMENT", String.valueOf(call.request().url()));
                Song responseSong = response.body();
                if( responseSong == null){
                    System.out.println(response.raw().toString());
                }else{
                    currentSong = responseSong;
                    updateFragmentLayout(responseSong);
                }


            }

            @Override
            public void onFailure(Call<Song> call, Throwable throwable) {
                showError(call, throwable);
            }
        });
    }

    private void getSongWikiDataURI(String artistName, String songName){
                loadingInProgress();

                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                Call<ResponseBody> call = service.getSongURI(artistName, songName);

                // Log.i("MIKI_ERROR", "Url: "+service.toString());
                // System.out.println("Url: "+call.request().url());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();

                        String wikiDataURI = null;

                        if( response.body() != null){
                            try {
                                wikiDataURI = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if(wikiDataURI!=null && !wikiDataURI.isEmpty()) {
                            getSongInfo(wikiDataURI);
                        }else{
                            Log.i("SONG_FRAGMENT", "URL: " +  call.request().url());
                            Log.i("SONG_FRAGMENT", "No URI FOUND");
                            View card0 = rootView.findViewById(R.id.card0);
                            TextView topTitle = card0.findViewById(R.id.tvCardTitle);
                            topTitle.setText("Couldn't find info about the song");
                            card0.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        showError(call, throwable);
                    }
                });
    }

    private void showError(Call<?> call, Throwable throwable){
        System.out.println("SONG_FRAGMENT: ERROR URL: " + call.request().url());
        System.out.println("SONG_FRAGMENT: ERROR: " + throwable.getMessage());
        progressDialog.dismiss();
        Toast.makeText(getContext(), "EYYY...Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
        // System.out.println("Url: "+call.request().url());
        //  System.out.println(t.getCause());
        //  System.out.println(t.getMessage());

        View card0 = rootView.findViewById(R.id.card0);
        TextView topTitle = card0.findViewById(R.id.tvCardTitle);
        topTitle.setText("The service isn't available at the moment!");
        card0.setVisibility(View.VISIBLE);

        loadError = true;
    }

    private void loadingInProgress(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public void setNewSong(String artistName, String songName){
        this.artistName = artistName;
        this.songName = songName;
        isUpdated = true;

        if(this.isVisible()){
            getSongWikiDataURI(artistName, songName);
        }
    }

}
