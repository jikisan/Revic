package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterMostConnectedItem;
import Adapters.AdapterReviewsItem;
import Models.Connections;
import Models.Ratings;
import Models.Users;

public class view_connections extends AppCompatActivity {

    private RecyclerView recyclerView_reviews;
    private TextView tv_noReviews, tv_back;

    private List<Connections> arrRatings = new ArrayList<>();
    private List<Users> arrUsers = new ArrayList<>();
    private List<String> arrUserId = new ArrayList<>();
    private AdapterMostConnectedItem adapterMostConnectedItem = new AdapterMostConnectedItem(arrUsers);

    private DatabaseReference connectionDatabase, userDatabase;
    private FirebaseUser user;

    private String myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_connections);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        connectionDatabase = FirebaseDatabase.getInstance().getReference("Connections");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef();
        generateUserId();
        clickListeners();

    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        adapterMostConnectedItem.setOnItemClickListener(new AdapterMostConnectedItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String userID = arrUsers.get(position).getUsersId();
                Intent intent = new Intent(view_connections.this, profile_page.class);
                intent.putExtra("userID", userID);
                intent.putExtra("myPosts", "2");
                startActivity(intent);
            }
        });

    }

    private void generateUserId() {


        connectionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrUsers.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Connections connections = dataSnapshot.getValue(Connections.class);

                        String user1 = connections.getUser1();
                        String user2 = connections.getUser2();

                        if(myUserId.equals(user1))
                        {
                            arrUserId.add(user2);

                        }
                        else if(myUserId.equals(user2))
                        {
                            arrUserId.add(user1);
                        }

                    }

                    generateRecyclerLayout();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateRecyclerLayout() {
        recyclerView_reviews.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view_connections.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView_reviews.setLayoutManager(gridLayoutManager);
        recyclerView_reviews.setAdapter(adapterMostConnectedItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        if(!arrUserId.isEmpty())
        {
            for(int i = 0 ; i<arrUserId.size(); i++)
            {
                String userID = arrUserId.get(i).toString();

                userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Users users = snapshot.getValue(Users.class);

                        if (users != null)
                        {
                            arrUsers.add(users);

                        }

                        adapterMostConnectedItem.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else{
            tv_noReviews.setVisibility(View.VISIBLE);
        }




    }

    private void setRef() {

        recyclerView_reviews = findViewById(R.id.recyclerView_reviews);
        tv_noReviews = findViewById(R.id.tv_noReviews);
        tv_back = findViewById(R.id.tv_back);
    }
}