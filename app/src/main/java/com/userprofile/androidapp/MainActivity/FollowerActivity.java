package com.userprofile.androidapp.MainActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.userprofile.androidapp.APIMasterInterface.ApiInterface;
import com.userprofile.androidapp.APIMasterInterface.RetroClient;
import com.userprofile.androidapp.Adpter.ShowFollowerList;
import com.userprofile.androidapp.R;
import com.userprofile.androidapp.ResponseModel.ReponseUserListModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerActivity extends AppCompatActivity implements View.OnClickListener   {
    private ApiInterface apiInterface;
    ProgressDialog pDialog;
    List<ReponseUserListModel> reponseUserListModel;
   RecyclerView userList_followers;

    String name, photo;
    TextView txt_location, txt_follower_count, txt_MailId, txt_personalBlog, txt_twitter, txt_companyName,txt_followers_login,
            txt_followers_name;
    TextView txt_followerclick;
    CircleImageView iv_profile_follower;
    ImageView imgshare, img_back;
    LinearLayout dataview;
    ShowFollowerList adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers_activity);
        findbyviewid();
        img_back.setOnClickListener(this);
        txt_followerclick.setOnClickListener(this);
        imgshare.setOnClickListener(this);
        receiveIntentValue();
        Picasso.with(FollowerActivity.this).load(photo).into(iv_profile_follower);
        if (haveNetworkConnection()) {
            callApiPersonalData(name);
        } else {
            internetAlert();
        }
    }

    private void findbyviewid() {
        userList_followers = findViewById(R.id.userList_followers);
        txt_followers_name = findViewById(R.id.txt_followers_name);
        txt_followers_login = findViewById(R.id.txt_followers_login);
        img_back = findViewById(R.id.img_back);
        dataview = findViewById(R.id.dataview);
        iv_profile_follower = findViewById(R.id.iv_profile_follower);
        txt_followerclick = findViewById(R.id.txt_followerclick);
        txt_companyName = findViewById(R.id.txt_companyName);
        txt_location = findViewById(R.id.txt_location);
        txt_MailId = findViewById(R.id.txt_MailId);
        txt_personalBlog = findViewById(R.id.txt_personalBlog);
        txt_twitter = findViewById(R.id.txt_twitter);
        txt_follower_count = findViewById(R.id.txt_follower_count);
        imgshare = findViewById(R.id.imgshare);
        SearchView searchView = (SearchView) findViewById(R.id.edit_search);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void callApiPersonalData(String name) {

        apiInterface = RetroClient.getApiClient().create(ApiInterface.class);
        Call<ReponseUserListModel> call = apiInterface.getPersonData(name);
        call.enqueue(new Callback<ReponseUserListModel>() {

            public void onResponse(Call<ReponseUserListModel> call, Response<ReponseUserListModel> response) {
                try {
                    if (response.body() != null) {
                        reponseUserListModel = new ArrayList<>();
                        reponseUserListModel.add(response.body());
                        txt_followers_name.setText(reponseUserListModel.get(0).getName());
                        txt_followers_login.setText(reponseUserListModel.get(0).getLogin());

                        if (reponseUserListModel.get(0).getCompany() == null) {
                            txt_companyName.setText("");
                        } else {
                            txt_companyName.setText(reponseUserListModel.get(0).getCompany());
                        }
                        if (reponseUserListModel.get(0).getLocation() == null) {
                            txt_location.setText("");
                        } else {
                            txt_location.setText(reponseUserListModel.get(0).getLocation());
                        }

                        if (reponseUserListModel.get(0).getEmail() == null) {
                            txt_MailId.setText("");
                        } else {
                            txt_MailId.setText(reponseUserListModel.get(0).getEmail());
                        }
                        if (reponseUserListModel.get(0).getBlog() == null) {
                            txt_personalBlog.setText("");
                        } else {
                            txt_personalBlog.setText(reponseUserListModel.get(0).getBlog());
                        }
                        if (reponseUserListModel.get(0).getTwitter_username() == null) {
                            txt_twitter.setText("");
                        } else {
                            txt_twitter.setText(reponseUserListModel.get(0).getTwitter_username());
                        }
                        callApiFollowerList(reponseUserListModel.get(0).getLogin());
                    } else {
                        Toast.makeText(FollowerActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(FollowerActivity.this, "Something went wrong in response, please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponseUserListModel> call, Throwable t) {
                Toast.makeText(FollowerActivity.this, "Something went wrong, please try again...!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApiFollowerList(String name) {
        pDialog = new ProgressDialog(FollowerActivity.this);
        pDialog.setMessage("Processing.. Please wait..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        apiInterface = RetroClient.getApiClient().create(ApiInterface.class);
        Call<List<ReponseUserListModel>> call = apiInterface.getFollowerList(name);
        call.enqueue(new Callback<List<ReponseUserListModel>>() {

            public void onResponse(Call<List<ReponseUserListModel>> call, Response<List<ReponseUserListModel>> response) {
                pDialog.dismiss();
                try {
                    if (response.body() != null) {
                        reponseUserListModel = new ArrayList<>();
                        reponseUserListModel.addAll(response.body());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowerActivity.this);
                         adapter = new ShowFollowerList(FollowerActivity.this, reponseUserListModel);
                         userList_followers.setAdapter(adapter);
                         userList_followers.setLayoutManager(layoutManager);
                         txt_follower_count.setText(String.valueOf(reponseUserListModel.size()));

                    } else {
                        Toast.makeText(FollowerActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(FollowerActivity.this, "Something went wrong in response, please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReponseUserListModel>> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(FollowerActivity.this, "Something went wrong, please try again...!", Toast.LENGTH_SHORT).show();
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
                FollowerActivity.this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;

            case R.id.imgshare:
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, txt_personalBlog.getText().toString().trim());
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Whatsapp have not been installed.", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}