package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.video_view_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterPhotoItem;
import Adapters.AdapterVideoItem;
import Models.Photos;
import Models.Videos;

public class VideosFragment extends Fragment {

    private RecyclerView rv_videos;
    private AdapterVideoItem adapterVideoItem;
    private TextView tv_noVideos;

    private DatabaseReference userDatabase, videoDatabase;
    private FirebaseUser user;

    private String userID, userIdFromSearch;

    private List<Videos> arrVideos = new ArrayList<Videos>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        videoDatabase = FirebaseDatabase.getInstance().getReference("Videos");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userIdFromSearch = getActivity().getIntent().getStringExtra("userID");

        setRef(view);
        generateRecyclerLayout();
        clickListeners();
        return view;
    }

    private void clickListeners() {

        adapterVideoItem.setOnItemClickListener(new AdapterVideoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String vidLink = arrVideos.get(position).getLink();
                String vidName = arrVideos.get(position).getVideoName();

                Intent intent = new Intent(getActivity(), video_view_page.class);
                intent.putExtra("userID", userID);
                intent.putExtra("current position", position);
                intent.putExtra("vidName", vidName);
                intent.putExtra("vidLink", vidLink);
                startActivity(intent);
            }
        });

    }

    private void generateRecyclerLayout() {

        rv_videos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        rv_videos.setLayoutManager(gridLayoutManager);

        adapterVideoItem = new AdapterVideoItem(arrVideos, getContext());
        rv_videos.setAdapter(adapterVideoItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        if (userIdFromSearch != null) {
            userID = userIdFromSearch;
        }
        else
        {
            userID = user.getUid();
        }


        Query query = videoDatabase
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Videos videos = dataSnapshot.getValue(Videos.class);
                    arrVideos.add(videos);
                }

                if(arrVideos.isEmpty())
                {
                    rv_videos.setVisibility(View.GONE);
                    tv_noVideos.setVisibility(View.VISIBLE);
                }


                adapterVideoItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setRef(View view) {

        rv_videos = view.findViewById(R.id.rv_videos);
        tv_noVideos = view.findViewById(R.id.tv_noVideos);
    }

}