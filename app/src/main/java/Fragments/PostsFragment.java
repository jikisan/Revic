package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.revic_capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapters.AdapterEventsItem;
import Adapters.AdapterMostConnectedItem;
import Adapters.AdapterPostsItem;
import Models.Events;
import Models.Posts;
import Models.Users;

public class PostsFragment extends Fragment {

    private List<Posts> arrPosts = new ArrayList<>();
    private List<Users> arrUsers = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView recyclerView_users;
    private AdapterPostsItem adapterPostsItem;
    private TextView tv_noPhotos;

    private FirebaseUser user;
    private DatabaseReference userDatabase, postDatabase;

    private String myUserId, myPosts, userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        myPosts = getActivity().getIntent().getStringExtra("myPosts");
        userID = getActivity().getIntent().getStringExtra("userID");

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        postDatabase = FirebaseDatabase.getInstance().getReference("Posts");


        setRef(view);
        generateRecyclerLayout();
        return view;
    }

    private void generateRecyclerLayout() {

        recyclerView_users.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_users.setLayoutManager(linearLayoutManager);

        adapterPostsItem = new AdapterPostsItem(arrPosts, arrUsers, getContext());
        recyclerView_users.setAdapter(adapterPostsItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrPosts.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Posts posts = dataSnapshot.getValue(Posts.class);
                        String postUsersId = posts.getUserId();

                        if(myPosts != null && myPosts.equals("1"))
                        {
                            if(myUserId.equals(postUsersId))
                            {
//                                generateUsersData(postUsersId);
                                arrPosts.add(posts);
                            }

                        }else if(myPosts != null && myPosts.equals("2"))
                        {
                            if(userID.equals(postUsersId))
                            {
//                                generateUsersData(postUsersId);
                                arrPosts.add(posts);
                            }

                         }else if(myPosts == null || myPosts.isEmpty())
                         {
//                            generateUsersData(postUsersId);
                            arrPosts.add(posts);
                        }

                    }

                    if(arrPosts.isEmpty())
                    {
                        recyclerView_users.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        tv_noPhotos.setVisibility(View.VISIBLE);
                    }

                }

                Collections.reverse(arrPosts);
                progressBar.setVisibility(View.GONE);
                adapterPostsItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

//    private void generateUsersData(String postUsersId) {
//
//        userDatabase.child(postUsersId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.exists())
//                {
//                    Users users = snapshot.getValue(Users.class);
//                    arrUsers.add(users);
//                }
//
//                Collections.reverse(arrUsers);
//                Collections.reverse(arrPosts);
//
//                progressBar.setVisibility(View.GONE);
//                adapterPostsItem.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        tv_noPhotos = view.findViewById(R.id.tv_noPhotos);
        recyclerView_users = view.findViewById(R.id.recyclerView_users);

    }
}