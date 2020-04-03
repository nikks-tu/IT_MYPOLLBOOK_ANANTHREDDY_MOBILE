package com.techuva.councellorapp.contus_Corporate.userpolls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.gms.ads.AdView;
import com.techuva.councellorapp.R;
import com.techuva.councellorapp.contus_Corporate.activity.CustomRequest;
import com.techuva.councellorapp.contus_Corporate.activity.VolleyResponseListener;
import com.techuva.councellorapp.contus_Corporate.app.Constants;
import com.techuva.councellorapp.contus_Corporate.responsemodel.UserPollResponseModel;
import com.techuva.councellorapp.contus_Corporate.restclient.UserPollRestClient;
import com.techuva.councellorapp.contus_Corporate.views.EndLessListView;
import com.techuva.councellorapp.contusfly_corporate.MApplication;
import com.techuva.councellorapp.contusfly_corporate.smoothprogressbar.SmoothProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserPolls extends Fragment implements EndLessListView.EndlessListener {
    //Endlesslistview
    private EndLessListView listUserPolls;
    //Footerview
    private View footerView;
    //Adview
    private AdView mAdView;
    //Userid
    private String userId;
    //Userpollcustom adapter
    private UserPollCustomAdapter userPollCustomAdapter;
    //Get the last page
    private String getLastPageResponse;
    //page
    private int page;
    //Get the current page
    private String getCurrentPageResponse;
    //Smooth progress bar
    private SmoothProgressBar userPollGoogleNow;
    //Relative layout
    private RelativeLayout internetConnection;
    //Relative layout
    private RelativeLayout relativeList;
    //Button retry
    private TextView btnRetryUserPoll;
    //no user poll
    private TextView noUserPoll;
    Context context;
    //user poll response
    private List<UserPollResponseModel.Results.Data> userPollResponse;
    //like count
    private ArrayList<Integer> userPollLikeCount = new ArrayList<Integer>();
    //likes user
    private ArrayList<Integer> userPollLikesUser = new ArrayList<Integer>();
    //Comments count
    private ArrayList<Integer> userPollcommentsCount = new ArrayList<Integer>();
    //poll id answer check
    private ArrayList<String> userPollIdAnwserCheck = new ArrayList<String>();
    //answer
    private ArrayList<String> userPollIdAnwser = new ArrayList<String>();
    //Fragment
    private Activity userPollsFragment;
    //participate count
    private ArrayList<Integer> userPollParticipateCount = new ArrayList<Integer>();
    //model
    private String model;
    int pageno=1;
    //swipe refresh layout
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UserPollOnFragmentInteractionListner mListener;
    private Activity activity;
    private MApplication mApplication;
    Boolean versionFlag=true;
    int TotalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_userpoll, null);
        listUserPolls =  root.findViewById(R.id.listView);
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.empty_footer, null, false);
        mAdView =  root.findViewById(R.id.adView);
        userPollGoogleNow =  root.findViewById(R.id.google_now);
        internetConnection =  root.findViewById(R.id.internetConnection);
        relativeList =  root.findViewById(R.id.list);
        noUserPoll =  root.findViewById(R.id.noCampaignResults);
        mSwipeRefreshLayout =  root.findViewById(R.id.activity_main_swipe_refresh_layout);
        btnRetryUserPoll =  root.findViewById(R.id.internetRetry);
        userPollsFragment = getActivity();
        mApplication = new MApplication();

        Toolbar toolbar =  getActivity().findViewById(R.id.mToolbar);
        toolbar.findViewById(R.id.imgEdit).setVisibility(View.GONE);
        ImageView search=  getActivity().findViewById(R.id.imgSearch);
        search.setVisibility(View.VISIBLE);
        ImageView imgEdit2=  getActivity().findViewById(R.id.imgEdit2);
        imgEdit2.setVisibility(View.GONE);
        ImageView imgEdit1=  getActivity().findViewById(R.id.imgEdit1);
        imgEdit1.setVisibility(View.GONE);
        ImageView imgEdit=  getActivity().findViewById(R.id.imgEdit);
        imgEdit.setVisibility(View.GONE);

        ImageView annouse_search=  getActivity().findViewById(R.id.annouse_search);
        annouse_search.setVisibility(View.GONE);

        LinearLayout userpoll =  getActivity().findViewById(R.id.userpoll);
        userpoll.setVisibility(View.VISIBLE);

        TextView userpoll_title =getActivity().findViewById(R.id.userpolltext);
        userpoll_title.setTextColor(getResources().getColor(R.color.blue_color));

        page = 1;
        MApplication.setBoolean(userPollsFragment, Constants.SEARCH_BACKPRESS_BOOLEAN, false);
        //Model of the mobile using
        model = android.os.Build.MODEL;
        //if matches samsung s3
        if (Constants.SAMSUNG_S3.equals(model)) {
            //er-child layout information associated with RelativeLayout.
            RelativeLayout.LayoutParams userPollLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            userPollLayout.setMargins(20, 20, 5, 0);
            //Set the layout parameters associated with this view.
            listUserPolls.setLayoutParams(userPollLayout);
        }
        ///Add a fixed view to appear at the bottom of the list.
        listUserPolls.addFooterView(footerView);
        //Getting the user id
        userId = MApplication.getString(userPollsFragment, Constants.USER_ID);
        //set lisner
        listUserPolls.setListener(this);
        //request fro the api
        userPollRequest();
        //validateAppVersion();

        showVersionPopup();

        //Interface definition for a callback to be invoked when a view is clicked.
        btnRetryUserPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnUserPollFragment("", "");
                }
            }
        });
//swipe refresh layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mListener != null) {
                    mListener.OnUserPollFragment("", "");
                }  //my poll request
            }
        });

        return root;
    }

    private void showVersionPopup() {

        /*LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
        final View view = inflater.inflate(R.layout.versionpopup, null);
        alertDialog.setView(view);
        final android.support.v7.app.AlertDialog alert = alertDialog.show();
        TextView  updateText=(TextView) view.findViewById(R.id.id_updateText);
        TextView update = (TextView) view.findViewById(R.id.id_update);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        updateText.setText(result.getString("msg"));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.polls.polls&hl=en" )));

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alert != null && alert.isShowing()) {
                    alert.dismiss();
                }
            }
        });

        alert.show();
        alert.setCancelable(false);*/
    }



    @Override
    public void loadData() {
        Log.e("load", "load");
        //Increamenting the PAGE number on scrolling
        page++;
        //Request for display the campaign
        userPollRequest();
    }

    /**
     * This method is used requesting the user poll and binding into the adapter
     */
    private void userPollRequest() {
        Log.e("page", page + "");

        ImageView imgEdit2=  getActivity().findViewById(R.id.imgEdit2);
        imgEdit2.setVisibility(View.GONE);
        ImageView imgEdit1=  getActivity().findViewById(R.id.imgEdit1);
        imgEdit1.setVisibility(View.GONE);
        ImageView imgEdit=  getActivity().findViewById(R.id.imgEdit);
        imgEdit.setVisibility(View.GONE);
        //If net is connected
        if (MApplication.isNetConnected(userPollsFragment)) {
            //View gone
            internetConnection.setVisibility(View.GONE);
            //View visible
            relativeList.setVisibility(View.VISIBLE);
            /*Progress bar visibility**/
            userPollGoogleNow.setVisibility(View.VISIBLE);

            userPollGoogleNow.progressiveStart();
           /* SharedPreferences pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
            String deviceId = pref.getString("regId", null);*/
           String deviceId = MApplication.getString(context, Constants.FCM_REG_ID);
            /*  Requesting the response from the given base url**/
            UserPollRestClient.getInstance().postUserApi("getUserPolls", Integer.valueOf(page), userId, deviceId
                    , new Callback<UserPollResponseModel>() {
                        @Override
                        public void success(UserPollResponseModel userPollResponseModel, Response response) {
                            //If the success value is 1 then the below functionality will take place
                            //else if  get success equals 1 and PAGE 1 or get successqueals 0 and PAGE equal to 1 views will be visible
                            response(userPollResponseModel);
                        }
                        @Override
                        public void failure(RetrofitError retrofitError) {
                            //Failure the response
                            MApplication.errorMessage(retrofitError, userPollsFragment);
                            //Google progress stop
                            userPollGoogleNow.progressiveStop();
                            //Google progress stop
                            userPollGoogleNow.setVisibility(View.GONE);
                            // MApplication.materialdesignDialogStop();
                            //swipe refresh false
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });


        } else {
            //internconnection visible
            internetConnection.setVisibility(View.VISIBLE);
            //visiblity gone
            relativeList.setVisibility(View.GONE);
        }

    }

    private void response(UserPollResponseModel userPollResponseModel) {
        //Google progrss stop
        userPollGoogleNow.progressiveStop();
        //Google progrss stop
        userPollGoogleNow.setVisibility(View.GONE);
        // MApplication.materialdesignDialogStop();
        //swipe refresh false
        mSwipeRefreshLayout.setRefreshing(false);

        if (("1").equals(userPollResponseModel.getSuccess())) {
            if (userPollsFragment != null) {
                MApplication.setBoolean(userPollsFragment, Constants.SEARCH_BACKPRESS_BOOLEAN, false);
            }
            //clear the array list
            userPollLikeCount.clear();
            //clear the array list
            userPollLikesUser.clear();
            //clear the array list
            userPollcommentsCount.clear();
            userPollParticipateCount.clear();
            //Getting the details from the response
            userPollResponse = userPollResponseModel.getResults().getData();
            //last page count
            getLastPageResponse = userPollResponseModel.getResults().getLastPage();
            //current page count
            getCurrentPageResponse = userPollResponseModel.getResults().getCurrentPage();
            Intent getCount = new Intent("get_user_poll_details");
            getCount.putExtra("admin_count", userPollResponse.get(0).getAdminCount());
            getCount.putExtra("user_count", userPollResponse.get(0).getUserCount());
            LocalBroadcastManager.getInstance(mApplication).sendBroadcast(new Intent(getCount));

            //If the user poll response is not empty
            if (!userPollResponse.isEmpty()) {
                //If get the current PAGE equal to 1
                if (Integer.parseInt(getCurrentPageResponse) == 1) {
                    if (userPollsFragment != null) {
                        //An Adapter object acts as a bridge between an AdapterView and the underlying data for that view.
                        // The Adapter provides access to the data items.
                        // The Adapter is also responsible for making a View for each item in the data set.
                        userPollCustomAdapter = new UserPollCustomAdapter(userPollsFragment, userPollResponse, R.layout.publicpoll_singleview, userId, listUserPolls);
                        //Set bottom footer view
                        listUserPolls.setLoadingView(R.layout.layout_loading);
                        //Sets the data behind this ListView.
                        listUserPolls.setUserPollAdapter(userPollCustomAdapter);
                    }
                } else if (Integer.parseInt(getCurrentPageResponse) <= Integer.parseInt(getLastPageResponse)) {
                    //the data is added into the array adapterfrom the response
                    listUserPolls.addUserPollData(userPollResponse);
                    //clear the array list
                    userPollLikeCount.clear();
                    //clear the array list
                    userPollLikesUser.clear();
                    //clear the array list
                    userPollcommentsCount.clear();
                    userPollParticipateCount.clear();
                    //Loading the details from the arraylist
                    userPollcommentsCount = MApplication.loadArray(userPollsFragment, userPollcommentsCount, Constants.USER_POLL_COMMENTS_COUNT, Constants.USER_POLL_COMMENTS_SIZE);
                    //Loading the details from the arraylist
                    userPollLikesUser = MApplication.loadArray(userPollsFragment, userPollLikesUser, Constants.USER_POLL_LIKES_USER_ARRAY, Constants.USER_POLL_LIKES_USER_SIZE);
                    //Loading the details from the arraylist
                    userPollLikeCount = MApplication.loadArray(userPollsFragment, userPollLikeCount, Constants.USER_POLL_LIKES_COUNT_ARRAY, Constants.USER_POLL_LIKES_COUNT_SIZE);
                    //Loading the details from the arraylist
                    userPollParticipateCount = MApplication.loadArray(userPollsFragment, userPollParticipateCount, Constants.USER_POLL_PARTICIPATE_COUNT_ARRAY, Constants.USER_POLL_PARTICIPATE_COUNT_SIZE);
                    //Loading the details from the arraylist
                    userPollIdAnwserCheck = MApplication.loadStringArray(userPollsFragment, userPollIdAnwserCheck, Constants.USER_POLL_ID_ANSWER_ARRAY, Constants.USER_POLL_ID_ANSWER_SIZE);
                    //Loading the details from the arraylist
                    userPollIdAnwser = MApplication.loadStringArray(userPollsFragment, userPollIdAnwser, Constants.USER_POLL_ID_ANSWER_SELECTED_ARRAY, Constants.USER_POLL_ID_SELECTED_SIZE);
                }
            }
            /**
             * Looping the details from the prefernce
             */
            for (int i = 0; userPollResponse.size() > i; i++) {
                //Adding the comments count in array list
                userPollcommentsCount.add(Integer.valueOf(userPollResponseModel.getResults().getData().get(i).getCampaignCommentsCounts()));
                //Saving the string array in prefernce
                MApplication.saveArray(userPollsFragment, userPollcommentsCount, Constants.USER_POLL_COMMENTS_COUNT, Constants.USER_POLL_COMMENTS_SIZE);
                //Adding the comments count in array list
                userPollLikesUser.add(Integer.valueOf(userPollResponseModel.getResults().getData().get(i).getCampaignUserLikes()));
                //Saving the string array in prefernce
                MApplication.saveArray(userPollsFragment, userPollLikesUser, Constants.USER_POLL_LIKES_USER_ARRAY, Constants.USER_POLL_LIKES_USER_SIZE);
                //Adding the comments count in array list
                userPollLikeCount.add(Integer.valueOf(userPollResponseModel.getResults().getData().get(i).getCampaignLikesCounts()));
                //Saving the string array in prefernce
                MApplication.saveArray(userPollsFragment, userPollLikeCount, Constants.USER_POLL_LIKES_COUNT_ARRAY, Constants.USER_POLL_LIKES_COUNT_SIZE);
                //Adding the comments count in array list
                userPollParticipateCount.add(Integer.valueOf(userPollResponseModel.getResults().getData().get(i).getPollParticipateCount()));
                //Saving the string array in prefernce
                MApplication.saveArray(userPollsFragment, userPollParticipateCount, Constants.USER_POLL_PARTICIPATE_COUNT_ARRAY, Constants.USER_POLL_PARTICIPATE_COUNT_SIZE);
                if (!userPollResponseModel.getResults().getData().get(i).getUserParticipatePolls().isEmpty()) {
                    for (int j = 0; userPollResponseModel.getResults().getData().get(i).getUserParticipatePolls().size() > j; j++) {
                        if (userPollResponseModel.getResults().getData().get(i).getUserParticipatePolls().get(j).getUserId().equals(userId)) {
                            //adding the response to the array listt
                            userPollIdAnwserCheck.add(userPollResponseModel.getResults().getData().get(i).getUserParticipatePolls().get(j).getPollId());
                            //adding the response to the array listt
                            userPollIdAnwser.add(userPollResponseModel.getResults().getData().get(i).getUserParticipatePolls().get(j).getPollAnswer());
                            //Save the string array in prefernce
                            MApplication.saveStringArray(userPollsFragment, userPollIdAnwserCheck, Constants.USER_POLL_ID_ANSWER_ARRAY, Constants.USER_POLL_ID_ANSWER_SIZE);
                            //Save the string array in prefernce
                            MApplication.saveStringArray(userPollsFragment, userPollIdAnwser, Constants.USER_POLL_ID_ANSWER_SELECTED_ARRAY, Constants.USER_POLL_ID_SELECTED_SIZE);
                        }
                    }
                }
            }
        } else if (("0").equals(userPollResponseModel.getSuccess()) && page == 1) {
            //visible
            noUserPoll.setVisibility(View.VISIBLE);
            //invisible
            internetConnection.setVisibility(View.INVISIBLE);
            //invisible
            relativeList.setVisibility(View.INVISIBLE);
        }
        //   MApplication.materialdesignDialogStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        //If adapter is not null
        if (userPollCustomAdapter != null) {
            //adapter notify data set changed
            userPollCustomAdapter.notifyDataSetChanged();
        }
        //Googkle ad
        MApplication.googleAd(mAdView);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // MApplication.materialdesignDialogStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface UserPollOnFragmentInteractionListner {
        void OnUserPollFragment(String type, String id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

       /* ImageView imgEdit2=  getActivity().findViewById(R.id.imgEdit2);
        imgEdit2.setVisibility(View.GONE);
        ImageView imgEdit1=  getActivity().findViewById(R.id.imgEdit1);
        imgEdit1.setVisibility(View.GONE);
        ImageView imgEdit=  getActivity().findViewById(R.id.imgEdit);
        imgEdit.setVisibility(View.GONE);*/

        if (context instanceof Activity) {
            userPollsFragment = (Activity) context;
        }
        try {
            mListener = (UserPollOnFragmentInteractionListner) userPollsFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public void getAnnouncement_list(final boolean more)
    {
        EditText search_et= getActivity().findViewById(R.id.search_et);
        JSONObject obj = new JSONObject();
        try {
            obj.put("action","getAnnouncements");
            obj.put("user_id",userId);
            obj.put("page",pageno);
            obj.put("search_key",search_et.getText().toString());
            Log.v("...", obj.toString());
        }
        catch (Exception ae)
        {

        }

        CustomRequest.makeJsonObjectRequest(getActivity(), Constants.GET_ANNOUNCEMENT,obj, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
//            dialog.dismiss();
                Log.i("onErrormessage", "message= " +message);
            }
            @Override
            public void onResponse(JSONObject response) {
                JSONObject result = response.optJSONObject("results");
                //stopping swipe refresh
                try {
                    //Log.i("PCCmessage", "message " + result.getString("msg"));
                    int success=response.optInt("success");

                    if(success==1)
                    {
                        TotalCount =result.optInt("total");
                        Intent getCount = new Intent("get_user_poll_details");
                        getCount.putExtra("admin_count", userPollResponse.get(0).getAdminCount());
                        getCount.putExtra("user_count", userPollResponse.get(0).getUserCount());
                        LocalBroadcastManager.getInstance(mApplication).sendBroadcast(new Intent(getCount));
                    }
                    else
                    {
                        Toast.makeText(getContext(),result.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }





}
