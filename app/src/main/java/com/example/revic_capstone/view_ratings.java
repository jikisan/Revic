package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterReviewsItem;
import Models.Events;
import Models.Ratings;

public class view_ratings extends AppCompatActivity {

    private RecyclerView recyclerView_reviews;
    private TextView tv_noReviews, tv_back;

    private List<Ratings> arrRatings = new ArrayList<>();
    private AdapterReviewsItem adapterReviewsItem;

    private DatabaseReference ratingDatabase;

    private String ratingOfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ratings);

        ratingOfId = getIntent().getStringExtra("ratingOfId");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");


        setRef();
        generateRecyclerLayout();
        clickListeners();
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void generateRecyclerLayout() {

        recyclerView_reviews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view_ratings.this);
        recyclerView_reviews.setLayoutManager(linearLayoutManager);

        arrRatings = new ArrayList<>();
        adapterReviewsItem = new AdapterReviewsItem(arrRatings);
        recyclerView_reviews.setAdapter(adapterReviewsItem);

        getViewHolderValues();


    }

    private void getViewHolderValues() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(ratingOfId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())                {

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);

                        arrRatings.add(ratings);
                    }

                }

                if(arrRatings.isEmpty())
                {
                    tv_noReviews.setVisibility(View.VISIBLE);
                    recyclerView_reviews.setVisibility(View.GONE);
                }

                adapterReviewsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        recyclerView_reviews = findViewById(R.id.recyclerView_reviews);
        tv_noReviews = findViewById(R.id.tv_noReviews);
        tv_back = findViewById(R.id.tv_back);
    }
}