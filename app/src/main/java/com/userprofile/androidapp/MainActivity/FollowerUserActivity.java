package com.userprofile.androidapp.MainActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.userprofile.androidapp.APIMasterInterface.ApiInterface;
import com.userprofile.androidapp.APIMasterInterface.RetroClient;
import com.userprofile.androidapp.Adpter.ShowFollowerUserList;
import com.userprofile.androidapp.Adpter.ShowUserList;
import com.userprofile.androidapp.R;
import com.userprofile.androidapp.ResponseModel.ReponseUserListModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerUserActivity extends AppCompatActivity {
    private ApiInterface apiInterface;
    ProgressDialog pDialog;
    List<ReponseUserListModel> reponseUserListModel;
    RecyclerView userList;
    SearchView edit_search;
    ShowFollowerUserList adapter;
    ImageView img_back;
    String name,photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followeruserlayout);
        userList=findViewById(R.id.userList);
        img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        receiveIntentValue();
        edit_search=findViewById(R.id.edit_search);
        if(haveNetworkConnection()) {
            callApiUserProfileList();
        }else {
            internetAlert();
        }
        edit_search.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edit_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void receiveIntentValue() {
        name = getIntent().getStringExtra("name");
        photo = getIntent().getStringExtra("photo");
    }
    private void callApiUserProfileList(){
        pDialog = new ProgressDialog(FollowerUserActivity.this);
        pDialog.setMessage("Processing.. Please wait..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        apiInterface = RetroClient.getApiClient().create(ApiInterface.class);
        Call<List<ReponseUserListModel>> call = apiInterface.getFollowerList(name);
        call.enqueue(new Callback<List<ReponseUserListModel>>(){

            public void onResponse(Call<List<ReponseUserListModel>> call, Response<List<ReponseUserListModel>> response) {
                pDialog.dismiss();
                try {
                    if(response.body()!=null) {
                        reponseUserListModel=new ArrayList<>();
                        reponseUserListModel.addAll(response.body());

                        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowerUserActivity.this);
                        adapter = new ShowFollowerUserList(FollowerUserActivity.this, reponseUserListModel);
                        userList.setAdapter(adapter);
                        userList.setLayoutManager(layoutManager);
                    }else {
                        Toast.makeText(FollowerUserActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(FollowerUserActivity.this, "Something went wrong in response, please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReponseUserListModel>> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(FollowerUserActivity.this, "Something went wrong, please try again...!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private void internetAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                FollowerUserActivity.this);

        alertDialog.setTitle("इंटरनेट इशारा");
        //alertDialog.setTitle("INTERNET");
        //alertDialog.setMessage("PLEASE START INTERNET CONNECTION");
        alertDialog.setMessage("कृपया इंटरनेट सुरु करा.");

        alertDialog.setNegativeButton("ठीक आहे",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}