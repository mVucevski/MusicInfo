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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alespero.expandablecardview.ExpandableCardView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.mvucevski.musicinfo_app.CONSTANTS;
import com.mvucevski.musicinfo_app.R;
import com.mvucevski.musicinfo_app.adapter.AwardsAdapter;
import com.mvucevski.musicinfo_app.model.Artist;
import com.mvucevski.musicinfo_app.model.Award;
import com.mvucevski.musicinfo_app.model.Song;
import com.mvucevski.musicinfo_app.network.GetDataService;
import com.mvucevski.musicinfo_app.network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import at.blogc.android.views.ExpandableTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mvucevski.musicinfo_app.CONSTANTS.SocialLinks;
import static com.mvucevski.musicinfo_app.CONSTANTS.SocialLinks_URL;

public class ArtistFragment extends Fragment {

    private ProgressDialog progressDialog;
    private View rootView;
    private boolean loaded, loadError, isUpdated;
    private Artist currentArtist;
    private FragmentActivity context;
    private String artistName, songName;

    public ArtistFragment(String artistName, String songName){
        this.artistName = artistName;
        this.songName = songName;
        loaded = false;
        loadError = false;
        isUpdated = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        if(!loaded){
            loaded = true;
            if(artistName.length()>0)
                getArtistWikiDataURI(artistName);
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
            getArtistWikiDataURI(artistName);
        }else if(currentArtist!=null) {
            updateFragmentLayout(currentArtist);
        }else if(artistName.length() == 0 && songName.length() == 0) {
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

    private void updateFragmentLayout(Artist artist){
        System.out.println("ARTIST_FRAGMENT: updategRAFMETLAYOUT");
        Log.i("ARTIST_FRAGMENT", artist.toString());
        loadError = false;
        isUpdated = false;

        View card0 = rootView.findViewById(R.id.card0);
        card0.setVisibility(View.GONE);

        View card1 = rootView.findViewById(R.id.card1);
        TextView topTitle = card1.findViewById(R.id.tvCardTitle);
        topTitle.setText(artistName);
        TextView subTitle = card1.findViewById(R.id.sub_text);
        subTitle.setText(artist.getShortDescription());
        card1.setVisibility(View.VISIBLE);


        Picasso.Builder builder = new Picasso.Builder(getContext());
        builder.downloader(new OkHttp3Downloader(getContext()));
        if(!artist.getImageURL().isEmpty()){
            builder.build().load(artist.getImageURL())
                    .placeholder((R.drawable.ic_launcher_background))
                    .error(R.drawable.ic_launcher_background)
                    .into((ImageView) card1.findViewById(R.id.media_image));
        }else{
            ImageView iwTopImage = card1.findViewById(R.id.media_image);
            iwTopImage.setImageDrawable(getResources().getDrawable(R.drawable.image_not_available));
        }

        //Card 2
        View card2 = rootView.findViewById(R.id.card2);
        if(artist.getLongDescription() == null || artist.getLongDescription().isEmpty()){
            card2.setVisibility(View.GONE);
        }else{
            TextView tvTitle = card2.findViewById(R.id.tvCardTitle);
            tvTitle.setText("About the artist");
            TextView tvSubtitle = card2.findViewById(R.id.tvCardSubtitle);
            tvSubtitle.setText("Description");

            Button buttonToggle = card2.findViewById(R.id.button_toggle);
            ExpandableTextView tvDesc = card2.findViewById(R.id.tvCardContent);
            tvDesc.setText(artist.getLongDescription());

            tvDesc.setInterpolator(new OvershootInterpolator());
            buttonToggle.setOnClickListener(v->{
                buttonToggle.setText(tvDesc.isExpanded() ? "Show More" : "Show Less");
                tvDesc.toggle();
            });

            buttonToggle.setVisibility(View.VISIBLE);
            card2.setVisibility(View.VISIBLE);
        }

        //Card 3 - To-Do = fixed, need to be list
        View card3 = rootView.findViewById(R.id.card3);
        if(artist.getName().isEmpty()){
            card3.setVisibility(View.GONE);
        }else{
            TextView tvCardTitle = card3.findViewById(R.id.tvCardTitle);
            tvCardTitle.setText("Artist name:");
            TextView tvContent = card3.findViewById(R.id.tvCardContent);
            tvContent.setText(artist.getName());
            card3.setVisibility(View.VISIBLE);
        }

        //Card 4 - Year
        View card4 = rootView.findViewById(R.id.card4);
        if(artist.getName() == null || artist.getName().isEmpty()){
            card4.setVisibility(View.GONE);
        }else{
            TextView tvCardTitle = card4.findViewById(R.id.tvCardTitle);
            tvCardTitle.setText("Career start:");
            TextView tvContent = card4.findViewById(R.id.tvCardContent);
            tvContent.setText(artist.getCareerStartYear() + "");
            card4.setVisibility(View.VISIBLE);
        }

        //Card 4.1 - Country
        View card4_country = rootView.findViewById(R.id.card4_country);
        if(artist.getLocation() == null || artist.getLocation().isEmpty()){
            card4_country.setVisibility(View.GONE);
        }else{
            TextView tvCardTitle = card4_country.findViewById(R.id.tvCardTitle);
            tvCardTitle.setText("Country:");
            TextView tvContent = card4_country.findViewById(R.id.tvCardContent);
            tvContent.setText(artist.getLocation());
            card4_country.setVisibility(View.VISIBLE);
        }

        // Card 5 - genres list
        View card5 = rootView.findViewById(R.id.card5);
        if(artist.getGenres() == null || artist.getGenres().isEmpty()){
            card5.setVisibility(View.GONE);
        }else{
            TextView cardTitle = card5.findViewById(R.id.tvCardTitle);
            cardTitle.setText("Genres");
            TextView cardList = card5.findViewById(R.id.tvCardContent);
            cardList.setText(TextUtils.join("\n", artist.getGenres()));
            card5.setVisibility(View.VISIBLE);
        }

        // Card 6 - record labels list
        View card6 = rootView.findViewById(R.id.card6);
        if(artist.getRecordLabels() == null || artist.getRecordLabels().isEmpty()){
            card6.setVisibility(View.GONE);
        }else{
            TextView cardTitle = card6.findViewById(R.id.tvCardTitle);
            cardTitle.setText("Record Labels");
            TextView cardList = card6.findViewById(R.id.tvCardContent);
            cardList.setText(TextUtils.join("\n", artist.getRecordLabels()));
            card6.setVisibility(View.VISIBLE);
        }

        //Card 7 - Albums - list
        View card7 = rootView.findViewById(R.id.card7);
        if(artist.getAlbums() == null || artist.getAlbums().isEmpty()){
            card7.setVisibility(View.GONE);
        }else{
            TextView cardTitle = card7.findViewById(R.id.tvCardTitle);
            cardTitle.setText("Albums");
            TextView cardList = card7.findViewById(R.id.tvCardContent);
            cardList.setText(TextUtils.join("\n", artist.getAlbumsNames()));
            card7.setVisibility(View.VISIBLE);
        }


        //Card 8 - Awards - ListView or RecyclerView
        View card8 = rootView.findViewById(R.id.card8);
        if(artist.getAwards() == null || artist.getAwards().isEmpty()){
            card8.setVisibility(View.GONE);
        }else{
            TextView cardTitle = card8.findViewById(R.id.tvAwardListTitle);
            cardTitle.setText("Awards");
            ExpandableLayout expandableLayout = card8.findViewById(R.id.expandable_layout);
            ImageView tvButtonToggle = card8.findViewById(R.id.ivArrowExpand);
            tvButtonToggle.setOnClickListener(view -> {
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                    tvButtonToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                } else {
                    expandableLayout.expand();
                    tvButtonToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                }
            });


            //ExpandableCardView expandableCard = card8.findViewById(R.id.expandableCard);
//            TextView cardTitle = card8.findViewById(R.id.tvAwardListTitle);
//            cardTitle.setText("Awards");
            // TextView cardList = card8.findViewById(R.id.tvCardContent);
            //cardList.setText(TextUtils.join("\n", artist.getAwardsString()));
            updateAwardsList(artist.getAwards(), card8);
            card8.setVisibility(View.VISIBLE);
        }

        View card9 = rootView.findViewById(R.id.card9);
        if(artist.getSocialLinks().values().isEmpty()){
            card9.setVisibility(View.GONE);
        }else{
            LinearLayout viewYT = card9.findViewById(R.id.viewYouTube);
            String linkID = artist.getSocialLinks().get("youtube");
            if(linkID == null || linkID.isEmpty()){
                viewYT.setVisibility(View.GONE);
            }else{
                viewYT.setOnClickListener(v->openAnotherApp("vnd.youtube://user/channel/", linkID, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.YOUTUBE.ordinal()]));
            }

            LinearLayout viewSP= card9.findViewById(R.id.viewSpotify);
            String linkSP_ID = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.SPOTIFY.ordinal()]);
            if(linkSP_ID == null || linkSP_ID.isEmpty()){
                viewSP.setVisibility(View.GONE);
            }else{
                viewSP.setOnClickListener(v->openAnotherApp("spotify:artist:", linkSP_ID, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.SPOTIFY.ordinal()]));
            }

            LinearLayout viewSC= card9.findViewById(R.id.viewSoundCloud);
            String linkSC = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.SOUNDCLOUD.ordinal()]);
            if(linkSC == null || linkSC.isEmpty()){
                viewSC.setVisibility(View.GONE);
            }else{
                viewSC.setOnClickListener(v->openAnotherApp("soundcloud://users::", linkSC, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.SOUNDCLOUD.ordinal()]));
            }

            LinearLayout viewGP= card9.findViewById(R.id.viewGooglePlay);
            String linkGP = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.GOOGLEPLAY.ordinal()]);
            if(linkGP == null || linkGP.isEmpty()){
                viewGP.setVisibility(View.GONE);
            }else{
                viewGP.setOnClickListener(v->openAnotherApp("market://details?id=", linkGP, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.GOOGLEPLAY.ordinal()]));
            }

            LinearLayout viewIT= card9.findViewById(R.id.viewITunes);
            String linkIT = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.ITUNES.ordinal()]);
            if(linkGP == null || linkGP.isEmpty()){
                viewIT.setVisibility(View.GONE);
            }else{
                viewIT.setOnClickListener(v->openAnotherApp("", linkIT, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.ITUNES.ordinal()]));
            }

            LinearLayout viewMB= card9.findViewById(R.id.viewMusicBrainz);
            String linkMB = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.MUSICBRAINZ.ordinal()]);
            if(linkMB == null || linkMB.isEmpty()){
                viewMB.setVisibility(View.GONE);
            }else{
                viewMB.setOnClickListener(v->openAnotherApp("", linkMB, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.MUSICBRAINZ.ordinal()]));
            }

            LinearLayout viewTW= card9.findViewById(R.id.viewTwitter);
            String linkTW = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.TWITTER.ordinal()]);
            if(linkTW == null || linkTW.isEmpty()){
                viewTW.setVisibility(View.GONE);
            }else{
                viewTW.setOnClickListener(v->openAnotherApp("twitter://user?screen_name=", linkTW, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.TWITTER.ordinal()]));
            }

            LinearLayout viewFB= card9.findViewById(R.id.viewFacebook);
            String linkFB = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.FACEBOOK.ordinal()]);
            if(linkFB == null || linkFB.isEmpty()){
                viewFB.setVisibility(View.GONE);
            }else{
                viewFB.setOnClickListener(v->openAnotherApp("fb://facewebmodal/f?href=https://www.facebook.com/", linkFB, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.FACEBOOK.ordinal()]));
            }

            LinearLayout viewIG= card9.findViewById(R.id.viewInstagram);
            String linkIG = artist.getSocialLinks().get(SocialLinks[CONSTANTS.SocialLinks_Enum.INSTAGRAM.ordinal()]);
            if(linkIG == null || linkIG.isEmpty()){
                viewIG.setVisibility(View.GONE);
            }else{
                viewIG.setOnClickListener(v->openAnotherApp("http://instagram.com/_u/", linkIG, SocialLinks_URL[CONSTANTS.SocialLinks_Enum.INSTAGRAM.ordinal()]));
            }

            card9.setVisibility(View.VISIBLE);
        }

    }

    private void getArtistInfo(String artistURI){
        loadingInProgress();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Artist> call = service.getArtist(artistURI);

        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                progressDialog.dismiss();

                //addToDataBase(response.body().getSearchMovies());
                //generateDataList(response.body().getSearchMovies());

                System.out.println("ARTIST_FRAGMENT - ARTIST INFO: " + call.request().url());
                Log.i("ARTIST_FRAGMENT", String.valueOf(call.request().url()));
                Artist responseArtist = response.body();
                if(responseArtist == null){
                    System.out.println(response.raw().toString());
                }else{
                    currentArtist = responseArtist;
                    updateFragmentLayout(responseArtist);
                }
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable throwable) {
                showError(call, throwable);
            }
        });
    }

    private void getArtistWikiDataURI(String artistName){
        loadingInProgress();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = service.getArtistURI(artistName);

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
                    getArtistInfo(wikiDataURI);
                }else{
                    Log.i("ARTIST_FRAGMENT", "No URI FOUND");
                    View card0 = rootView.findViewById(R.id.card0);
                    TextView topTitle = card0.findViewById(R.id.tvCardTitle);
                    topTitle.setText("The service isn't available at the moment!");
                    card0.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                showError(call, throwable);
            }
        });
    }

    private void loadingInProgress(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void updateAwardsList(List<Award> awards, View card) {
        RecyclerView rvAwardsList = card.findViewById(R.id.rvAwardList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        rvAwardsList.setLayoutManager(layoutManager);
        AwardsAdapter awardsAdapter = new AwardsAdapter(awards, context);
        rvAwardsList.setAdapter(awardsAdapter);
    }


    private void showError(Call<?> call, Throwable throwable){
        System.out.println("ARTIST_FRAGMENT: ERROR URL: " + call.request().url());
        System.out.println("ARTIST_FRAGMENT: ERROR: " + throwable.getMessage());
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

    private void openAnotherApp(String appID, String linkID, String link) {
        if(appID.isEmpty()){
            openWebLink(linkID, link);
        }else {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appID + linkID));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                openWebLink(linkID, link);
            }
        }
    }

    private void openWebLink(String linkID, String link){
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(link + linkID));
        startActivity(webIntent);
    }

    public void setNewArtist(String artistName){
        if(!artistName.equals(this.artistName) || currentArtist == null){
            this.artistName = artistName;
            isUpdated = true;

            if(this.isVisible()){
                getArtistWikiDataURI(artistName);
            }
        }

    }
}