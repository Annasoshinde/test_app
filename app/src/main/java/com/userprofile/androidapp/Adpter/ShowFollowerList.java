package com.userprofile.androidapp.Adpter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.userprofile.androidapp.MainActivity.FollowerActivity;
import com.userprofile.androidapp.MainActivity.FollowerUserActivity;
import com.userprofile.androidapp.R;
import com.userprofile.androidapp.ResponseModel.ReponseUserListModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowFollowerList extends RecyclerView.Adapter<ShowFollowerList.ViewHolder> implements Filterable {


    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private List<ReponseUserListModel> itemList;
    private List<ReponseUserListModel> exampleListFull;
    String getData;
    // Constructor of the class
    private Context context;

    public ShowFollowerList(Context context, List<ReponseUserListModel> itemList) {
        this.context = context;
        this.itemList = itemList;
        exampleListFull = new ArrayList<>(itemList);

    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layput_show_followerlist, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int listPosition) {

        try {

            holder.txt_name.setText(itemList.get(listPosition).getLogin());
            Picasso.with(context).load(itemList.get(listPosition).getAvatar_url()).into(holder.iv_profile);
            holder.txt_followers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FollowerUserActivity.class);
                    intent.putExtra("name", holder.txt_name.getText().toString());
                    intent.putExtra("photo", String.valueOf(itemList.get(listPosition).getAvatar_url()));
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Static inner class to initialize the views of rows
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txt_name,txt_followers;
        CircleImageView iv_profile;

        ViewHolder(View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile);
            txt_followers = (TextView) itemView.findViewById(R.id.txt_followers);

        }

    }
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ReponseUserListModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ReponseUserListModel item : exampleListFull) {
                    if (item.getLogin().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}