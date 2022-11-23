package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.Events;
import Models.Ratings;
import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class rate_event_and_user_page extends AppCompatActivity {

    private List<Ratings> arrRatingsUser = new ArrayList<>();
    private List<Ratings> arrRatingsEvent = new ArrayList<>();

    private ImageView iv_userPhoto, iv_eventPhoto;
    private TextView tv_backBtn, tv_userName, tv_eventName;
    private EditText et_ratingCommentUser, et_ratingCommentEvent;
    private Button btn_ratingSubmit;
    private RatingBar ratingBarUser, ratingBarEvent;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, contractDatabase, userDatabase, ratingDatabase;

    private String myUserId, eventId, contractId, creatorId, applicationId,
            myUserName, userName, eventName, myCategory;
    private int userRatingCount = 0;
    private int eventRatingCount = 0;
    private double userRatings = 0;
    private double eventRatings = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_event_and_user_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        contractId = getIntent().getStringExtra("contractId");
        creatorId = getIntent().getStringExtra("creatorId");
        eventId = getIntent().getStringExtra("eventId");

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        setRef();
        generateUserData();
        generateEventData();
        generateReviewData();
        generateMyData();
        clickListeners();
    }

    private void clickListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        btn_ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float getRatingUser = ratingBarUser.getRating();
                float getRatingEvent = ratingBarEvent.getRating();

                new SweetAlertDialog(rate_event_and_user_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("FINISH REVIEW")
                        .setCancelText("Back")
                        .setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                final ProgressDialog progressDialog = new ProgressDialog(rate_event_and_user_page.this);
                                progressDialog.setTitle("Processing review...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                submitRating(getRatingUser, getRatingEvent);

                            }
                        })
                        .setContentText("Submit your rating \n and reviews?")
                        .show();


            }
        });
    }

    private void generateMyData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    String myFname = users.getFname();
                    String myLname = users.getLname();
                    myUserName = myFname + " " + myLname;
                    myCategory = users.getCategory();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateReviewData() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(creatorId);

        ratingDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        String ratingOfId = ratings.getRatingOfId();

                        if(ratingOfId.equals(creatorId))
                        {
                            arrRatingsUser.add(ratings);
                        }
                        else if(ratingOfId.equals(eventId))
                        {
                            arrRatingsEvent.add(ratings);
                        }

                    }

                    userRatingCount = arrRatingsUser.size();
                    eventRatingCount = arrRatingsEvent.size();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateUserData() {

        userDatabase.child(creatorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    String imageUrl = users.getImageUrl();
                    String fname = users.getFname();
                    String lname = users.getLname();
                    userName = fname + " " + lname;

                    Picasso.get().load(imageUrl).into(iv_userPhoto);
                    tv_userName.setText(userName);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateEventData() {

        eventDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Events events = snapshot.getValue(Events.class);

                    String imageUrl = events.getImageUrl();
                    eventName = events.getEventName();

                    Picasso.get().load(imageUrl).into(iv_eventPhoto);
                    tv_eventName.setText(eventName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        iv_eventPhoto = findViewById(R.id.iv_eventPhoto);

        tv_backBtn = findViewById(R.id.tv_backBtn);
        tv_userName = findViewById(R.id.tv_userName);
        tv_eventName = findViewById(R.id.tv_eventName);

        et_ratingCommentUser = findViewById(R.id.et_ratingCommentUser);
        et_ratingCommentEvent = findViewById(R.id.et_ratingCommentEvent);

        ratingBarUser = findViewById(R.id.ratingBarUser);
        ratingBarEvent = findViewById(R.id.ratingBarEvent);

        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);

    }


    private void submitRating(float getRatingUser, float getRatingEvent) {

        String commentUsers = et_ratingCommentUser.getText().toString();
        String commentEvent = et_ratingCommentEvent.getText().toString();
        String ratingTypeUser = "users";
        String ratingTypeEvent = "events";

        Ratings ratingsUsers = new Ratings(creatorId, userName, myUserId, myUserName, getRatingUser, commentUsers, ratingTypeUser, contractId);
        Ratings ratingsEvents = new Ratings(eventId, eventName, myUserId, myUserName, getRatingEvent, commentEvent, ratingTypeEvent, contractId);

        ratingDatabase.push().setValue(ratingsUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    ratingDatabase.push().setValue(ratingsEvents).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                updateContract();
                            }

                        }
                    });
                }

            }
        });

    }

    private void updateContract() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("ratedByMusician", true);
        contractDatabase.child(contractId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateCreatorData();
            }
        });

    }

    private void updateCreatorData() {

        int i = 1;
        int newUserRatingCount = userRatingCount + i;


        HashMap<String, Object> hashMapUsers = new HashMap<String, Object>();
        hashMapUsers.put("rating", newUserRatingCount);

        userDatabase.child(creatorId).updateChildren(hashMapUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    updateEventData();
                }


            }
        });
    }

    private void updateEventData() {

        int i = 1;
        int newEventRatingCount = eventRatingCount + i;


        HashMap<String, Object> hashMapEvents = new HashMap<String, Object>();
        hashMapEvents.put("ratings", newEventRatingCount);

        eventDatabase.child(eventId).updateChildren(hashMapEvents).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(rate_event_and_user_page.this, homepage.class);
                    intent.putExtra("pageNumber", "5");
                    intent.putExtra("myCategory", myCategory);
                    intent.putExtra("myPosts", "1");
                    startActivity(intent);
                }
            }
        });
    }
}