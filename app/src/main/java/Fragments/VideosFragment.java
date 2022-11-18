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
import Models.Posts;
import Models.Videos;

public class VideosFragment extends Fragment {

    private RecyclerView rv_videos;
    private AdapterVideoItem adapterVideoItem;
    private TextView tv_noVideos;

    private DatabaseReference userDatabase, postsDatabase;
    private FirebaseUser user;

    private String userID, userIdFromSearch;

    private List<Posts> arrVideos = new ArrayList<Posts>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        postsDatabase = FirebaseDatabase.getInstance().getReference("Posts");
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

                String vidLink = arrVideos.get(position).getFileUrl();
                String vidName = arrVideos.get(position).getFileName();

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


        Query query = postsDatabase
                .orderByChild("fileType")
                .equalTo("video");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Posts posts = dataSnapshot.getValue(Posts.class);
                    String postsUserId = posts.getUserId();

                    if(userID.equals(postsUserId))
                    {
                        arrVideos.add(posts);
                    }
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