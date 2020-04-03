package com.techuva.councellorapp.contus_Corporate.userpolls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.techuva.councellorapp.BuildConfig;
import com.techuva.councellorapp.R;
import com.techuva.councellorapp.contus_Corporate.activity.SplashScreenActivity;
import com.techuva.councellorapp.contus_Corporate.activity.Welcome;
import com.techuva.councellorapp.contus_Corporate.app.Constants;
import com.techuva.councellorapp.contus_Corporate.residemenu.MenuActivity;
import com.techuva.councellorapp.contusfly_corporate.MApplication;
import com.techuva.councellorapp.contusfly_corporate.api_interface.AppVersionDataInterface;
import com.techuva.councellorapp.contusfly_corporate.model.AppVersionPostParameters;
import com.techuva.new_changes_corporate.AddComplaint;
import com.techuva.new_changes_corporate.adapters.ComplaintsAdapter;
import com.techuva.new_changes_corporate.models.ComplaintDetailModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午1:33
 * Mail: specialcyci@gmail.com
 */
public class ComplaintFragment extends Fragment {
//parent view
    private View parentView;
    FloatingActionButton fab_add_complaint;
    RecyclerView rcv_complaints;
    LinearLayout ll_no_record;
    TextView tv_no_data;
    Context context;
    int userId;
    ArrayList<ComplaintDetailModel> complaintList;

    ComplaintsAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_complaint, container, false);
        //get the views
        Window window = getActivity().getWindow();
        //Changinf the status bar color and hiding the back arror
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Changinf the status bar color and hiding the back arror
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //Changinf the status bar color and hiding the back arror
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Changinf the status bar color and hiding the back arror
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.color_primary));
        }
        context = getContext();
        userId = Integer.parseInt(MApplication.getString(context, Constants.USER_ID));
        complaintList = new ArrayList<>();
        fab_add_complaint = parentView.findViewById(R.id.fab_add_complaint);
        rcv_complaints = parentView.findViewById(R.id.rcv_complaints);
        ll_no_record = parentView.findViewById(R.id.ll_no_record);
        tv_no_data = parentView.findViewById(R.id.tv_no_data);
        serviceCall();



        fab_add_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddComplaint.class);
                startActivity(intent);
            }
        });
        return parentView;
    }

    /**
     * setUpViews
     */
    private void setUpViews() {
        Log.e("homefragment","homefragment");
        //Calling the activity
        MenuActivity parentActivity = (MenuActivity) getActivity();
        //Getting the reside menu
         parentActivity.getResideMenu();

    }

    public void serviceCall(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIVE_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AppVersionDataInterface service = retrofit.create(AppVersionDataInterface.class);
        Call<JsonElement> call = service.getUserComplaints(new AppVersionPostParameters(userId));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.body()!=null)
                {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    int success = jsonObject.get("success").getAsInt();
                    if(success==1){
                        JsonArray result = jsonObject.get("results").getAsJsonArray();
                        if(result.size()>0)
                        {
                            for (int i=0; i<result.size(); i++)
                            {
                                JsonObject object = result.get(i).getAsJsonObject();
                                ComplaintDetailModel model = new ComplaintDetailModel();
                                if(!object.get("id").isJsonNull())
                                {
                                    model.setId(object.get("id").getAsInt());
                                }
                                if(!object.get("title").isJsonNull())
                                {
                                    model.setTitle(object.get("title").getAsString());
                                }
                                if(!object.get("complaint").isJsonNull())
                                {
                                    model.setComplaint(object.get("complaint").getAsString());
                                }
                                if(!object.get("image_url").isJsonNull())
                                {
                                    model.setImageUrl(object.get("image_url").getAsString());
                                }
                                if(!object.get("created_at").isJsonNull())
                                {
                                    model.setCreatedAt(object.get("created_at").getAsString());
                                }
                                if(!object.get("created_by").isJsonNull())
                                {
                                    model.setCreatedBy(object.get("created_by").getAsInt());
                                }
                                if(!object.get("updated_at").isJsonNull())
                                {
                                    model.setUpdatedAt(object.get("updated_at").getAsString());
                                }
                                if(!object.get("status").isJsonNull())
                                {
                                    model.setStatus(object.get("status").getAsInt());
                                }
                                complaintList.add(model);
                                if(complaintList.size()>0)
                                {
                                    ll_no_record.setVisibility(View.GONE);
                                    rcv_complaints.setVisibility(View.VISIBLE);
                                    adapter= new ComplaintsAdapter(complaintList);
                                    LinearLayoutManager manager = new LinearLayoutManager(context);
                                    rcv_complaints.setLayoutManager(manager);
                                    rcv_complaints.setAdapter(adapter);
                                }
                                else {
                                    ll_no_record.setVisibility(View.VISIBLE);
                                    rcv_complaints.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                //Toast.makeText(context, "Error" +t, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        //Setting up the views
        setUpViews();
    }
}
