package com.techuva.councellorapp.contus_Corporate.likes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.ads.AdView;
import com.techuva.councellorapp.R;
import com.techuva.councellorapp.contus_Corporate.app.Constants;
import com.techuva.councellorapp.contus_Corporate.likes.PollLikesAdapter;
import com.techuva.councellorapp.contus_Corporate.residemenu.MenuActivity;
import com.techuva.councellorapp.contus_Corporate.responsemodel.LikesResponseModel;
import com.techuva.councellorapp.contus_Corporate.restclient.GetLikesRestClient;
import com.techuva.councellorapp.contus_Corporate.views.EndLessListView;
import com.techuva.councellorapp.contusfly_corporate.MApplication;
import com.techuva.councellorapp.contusfly_corporate.smoothprogressbar.SmoothProgressBar;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by user on 7/14/2015.
 */
public class PollLikes extends Activity implements EndLessListView.EndlessListener {
    //Endless list view
    private EndLessListView pollLikesList;
    //Activity class
    private PollLikes pollLikesActivity;
    //List model class
    private List<LikesResponseModel.Results.Data> dataResults;
    //Last PAGE
    private String getLastPage;
    //Current PAGE
    private String getCurrentPage;
    //Adapter
    private PollLikesAdapter adapter;
    //Poll id
    private String pollId;
    //Page
    private AdView mAdView;
    private int page = 1;
    //Smooth progress bar
    private SmoothProgressBar googleNow;
    //Relative layout
    private RelativeLayout relativeList;
    //Internet connection layout
    private RelativeLayout internetConnection;
    //Text view no likes
    private TextView pollNoLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        /**View are creating when the activity is started**/
        init();
    }
    /**
     * Instantiate the method
     */
    public void init() {
        /**Initializing the activity**/
        pollLikesActivity = this;
        pollLikesList = (EndLessListView) findViewById(R.id.listView);
        googleNow=(SmoothProgressBar)findViewById(R.id.google_now);
        relativeList = (RelativeLayout)findViewById(R.id.list);
        pollNoLikes =(TextView)findViewById(R.id.txtNoLikes);
        internetConnection = (RelativeLayout)findViewById(R.id.internetConnection);
        mAdView = (AdView) findViewById(R.id.adView);
        MApplication.googleAd(mAdView);
        //Poll id
        pollId = getIntent().getExtras().getString(Constants.POLL_ID);
        //Setting the listner
        pollLikesList.setListener(this);
        //Initializing the array list
        dataResults = new ArrayList<>();
        //Request for display the poll likes
        likesDisplayRequest();
    }

    /**
     * Request for display the poll likes
     */
    @Override
    public void onBackPressed() {
        MApplication.setBoolean(getApplicationContext(), Constants.SEARCH_BACKPRESS_BOOLEAN, true);
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    private void likesDisplayRequest() {
        //If net is connected
        if (MApplication.isNetConnected(pollLikesActivity)) {
            //view invisible
            pollNoLikes.setVisibility(View.INVISIBLE);
            //View gone
            internetConnection.setVisibility(View.GONE);
            //View visible
            relativeList.setVisibility(View.VISIBLE);
            /**Progree bar visiblity**/
            googleNow.setVisibility(View.VISIBLE);
            /**Progress bar start**/
            googleNow.progressiveStart();
            /**  Requesting the response from the given base url**/
            GetLikesRestClient.getInstance().getLikesDetailsCampaignPoll(new String("getPoll_likes"), new String(pollId), new String("10"), new Integer(page), new Callback<LikesResponseModel>() {
                @Override
                public void success(LikesResponseModel likesResponseModel, Response response) {
                    //If the succes matches with the value 1 then the datat is binded into the adapter
                    //else if get success equals 1 and PAGE 1 or get successqueals 0 and PAGE equal to 1 views will be visible
                    if (("1").equals(likesResponseModel.getSuccess())) {
                        likesDisplayResponse(likesResponseModel); //Method to set set the adapter
                    }else if(("1").equals(likesResponseModel.getSuccess())&&page==1||("0").equals(likesResponseModel.getSuccess())&&page==1){
                        pollNoLikes.setVisibility(View.VISIBLE); //View visible
                        relativeList.setVisibility(View.INVISIBLE);  //View invisible
                    }
                    //Progressabr stops
                    googleNow.progressiveStop();
                    //Visiblity gone
                    googleNow.setVisibility(View.GONE);
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    //Error message popups when the user cannot able to coonect with the server
                    MApplication.errorMessage(retrofitError, pollLikesActivity);
                    //Progressabr stops
                    googleNow.progressiveStop();
                    //Visiblity gone
                    googleNow.setVisibility(View.GONE);
                }
            });
        } else {
            //View visble
            internetConnection.setVisibility(View.VISIBLE);
            //View invlisible
            relativeList.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to bind the data in adapter
     * @param likesResponseModel likesResponseModel
     */
    private void likesDisplayResponse(LikesResponseModel likesResponseModel) {
        //Like user results from the response
            dataResults = likesResponseModel.getResults().getData();
        //Last PAGE from the response
            getLastPage = likesResponseModel.getResults().getLastPage();
        //Current PAGE response
            getCurrentPage = likesResponseModel.getResults().getCurrentPage();
        //If like user resuts is not empty sets the adapter
                if (!dataResults.isEmpty()) {
                    //If the current PAGE equals to 1 initialy setting the adapter
                    if (Integer.parseInt(getCurrentPage) == 1) {
                        //An Adapter object acts as a bridge between an AdapterView and the underlying data for that view.
                        // The Adapter provides access to the data items.
                        // The Adapter is also responsible for making a View for each item in the data set.
                        adapter = new PollLikesAdapter(pollLikesActivity, dataResults, R.layout.activity_likes_singleview);
                        //Set bottom footer view
                       pollLikesList.setLoadingView(R.layout.layout_loading);
                        //Sets the data behind this ListView.
                        pollLikesList.setPollLikesAdapter(adapter);
                    } else if (Integer.parseInt(getCurrentPage) <= Integer.parseInt(getLastPage)) {
                        //the data is added into the array adapterfrom the response
                        pollLikesList.addPollLikesData(dataResults);
                    }
                }
        }


    /**
     * Calling this method from xml file when performing the click on the ACTION
     *
     * @param likeViewClickAction
     */
    public void onClick(final View likeViewClickAction) {
        if(likeViewClickAction.getId()==R.id.imagBackArrow){
           onBackPressed();
        }
    }

    @Override
    public void loadData() {
        //Increamenting the PAGE number on scrolling
           page++;
        //Request for display the poll likes
        likesDisplayRequest();
    }
}
