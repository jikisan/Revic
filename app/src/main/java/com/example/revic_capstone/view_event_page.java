package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Models.Applications;
import Models.Events;
import Models.Ratings;
import Models.Users;
import Objects.TextModifier;

public class view_event_page extends AppCompatActivity {

    private ImageView iv_eventBannerPhoto, iv_userPhoto;
    private TextView tv_userName, tv_eventName, tv_eventCategory, tv_timeAvailable,
            tv_eventDescription, tv_dateSched, tv_applicantsCount, tv_userRatingCount
            , tv_noReviews, tv_eventPrice;
    private Button btn_apply, btn_applied;
    private FloatingActionButton btn_back;
    private ProgressBar progressBar;
    private RatingBar rb_userRating;
    private RecyclerView recyclerView_reviews;

    private List<Events> arrEvents = new ArrayList<>();
    private List<Ratings> arrRatings = new ArrayList<>();

    private FirebaseUser user;
    private DatabaseReference eventDatabase, userDatabase, applicationDatabase, ratingDatabase;

    private String myUserID, eventId, creatorUserId, timeCreated, dateCreated, eventImageUrl, eventName, applicantImageUrl, applicantName;
    private int applicantsCount;
    private long dateTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();

        eventId = getIntent().getStringExtra("eventId");

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        applicationDatabase = FirebaseDatabase.getInstance().getReference("Applications");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        setRef();
        generateEventsData();
        generateRatingAverage();
        generateApplicantsName();
        generateRecyclerLayout();
        clickListeners();
    }

    private void generateRatingAverage() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(eventId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0, averageRating = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;
                    }

                    averageRating = totalRating / counter;
                    String ratingCounter = "(" + String.valueOf(averageRating) + ")";

                    tv_userRatingCount.setText(ratingCounter);
                    rb_userRating.setRating((float) averageRating);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateRecyclerLayout() {

        if(arrRatings.isEmpty())
        {
            tv_noReviews.setVisibility(View.VISIBLE);
            recyclerView_reviews.setVisibility(View.GONE);
        }
    }

    private void clickListeners() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataToApplicationDB();
            }
        });

        btn_applied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void addDataToApplicationDB() {

        setUpDate();
        String status = "pending";

        Applications applications = new Applications(creatorUserId, myUserID, eventId, timeCreated, dateCreated,
                dateTimeInMillis, status, eventImageUrl, eventName, applicantImageUrl, applicantName);

        applicationDatabase.push().setValue(applications).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                int i = 1;
                updateEventsApplicants(i);
            }
        });

    }

    private void updateEventsApplicants(int i) {

        int newValue = applicantsCount + i;
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("applicants", newValue);
        eventDatabase.child(eventId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(view_event_page.this, "Applied!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_applied.setVisibility(View.VISIBLE);
        btn_apply.setVisibility(View.GONE);

    }

    private void generateEventsData() {

        eventDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Events events = snapshot.getValue(Events.class);

                if(events != null)
                {
                    eventImageUrl =  events.getImageUrl();
                    eventName = events.getEventName();
                    double eventPrice = events.getEventPrice();
                    int applicants = events.getApplicants();
                    String dateSched = events.getEventDateSched();
                    String startTime = events.getTimeStart();
                    String endTime = events.getTimeEnd();
                    String eventDescription = events.getEventDescription();
                    creatorUserId = events.getUserID();
                    applicantsCount = events.getApplicants();


                    Picasso.get()
                            .load(eventImageUrl)
                            .fit()
                            .centerCrop()
                            .into(iv_eventBannerPhoto);

                    tv_eventName.setText(eventName);
                    tv_eventPrice.setText("₱ "+eventPrice+" / Event");
                    tv_applicantsCount.setText(applicants + "");
                    tv_dateSched.setText(dateSched);
                    tv_timeAvailable.setText(startTime + " - " + endTime);
                    tv_eventDescription.setText(eventDescription);

                    if(myUserID.equals(creatorUserId))
                    {
                        btn_apply.setVisibility(View.GONE);
                        btn_applied.setVisibility(View.GONE);
                    }

                    generateUserData(creatorUserId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateUserData(String creatorUserId) {

            userDatabase.child(creatorUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {
                        Users users = snapshot.getValue(Users.class);

                        String fname = users.getFname();
                        String lname = users.getLname();
                        String fullName = fname + " " + lname;
                        String category = users.getCategory();
                        String userImageUrl = users.getImageUrl();

                        tv_userName.setText(fullName);
                        tv_eventCategory.setText(category);

                        Picasso.get().load(userImageUrl).into(iv_userPhoto);

                        checkApplicationStatus();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

    private void checkApplicationStatus() {

        Query query = applicationDatabase
                .orderByChild("eventId")
                        .equalTo(eventId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Applications applications = dataSnapshot.getValue(Applications.class);

                        assert applications != null;
                        String applicantUsersId = applications.getApplicantUsersId();

                        if(myUserID.equals(applicantUsersId))
                        {
                            btn_applied.setVisibility(View.VISIBLE);
                            btn_apply.setVisibility(View.GONE);
                        }


                    }


                }

                progressBar.setVisibility(View.GONE);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void generateApplicantsName() {

        userDatabase.child(myUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    applicantImageUrl= users.getImageUrl();

                    TextModifier textModifier = new TextModifier();

                    textModifier.setSentenceCase(users.getFname());
                    String fName = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lName = textModifier.getSentenceCase();

                    applicantImageUrl = users.getImageUrl();
                    applicantName = fName + " " + lName;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
//        Date dateInMillis = new Date(dateTimeInMillis);
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

    }

    private void setRef() {

        iv_eventBannerPhoto = findViewById(R.id.iv_eventBannerPhoto);
        iv_userPhoto = findViewById(R.id.userPhoto);

        tv_userName = findViewById(R.id.tv_userName);
        tv_eventName = findViewById(R.id.tv_eventName);
        tv_eventCategory = findViewById(R.id.tv_eventCategory);
        tv_dateSched = findViewById(R.id.tv_dateSched);
        tv_timeAvailable = findViewById(R.id.tv_timeAvailable);
        tv_eventDescription = findViewById(R.id.tv_eventDescription);
        tv_applicantsCount = findViewById(R.id.tv_applicantsCount);
        tv_userRatingCount = findViewById(R.id.tv_userRatingCount);
        tv_noReviews = findViewById(R.id.tv_noReviews);
        tv_eventPrice = findViewById(R.id.tv_eventPrice);

        recyclerView_reviews = findViewById(R.id.recyclerView_reviews);

        btn_apply = findViewById(R.id.btn_apply);
        btn_applied = findViewById(R.id.btn_applied);

        rb_userRating = findViewById(R.id.rb_userRating);

        progressBar = findViewById(R.id.progressBar);

        btn_back = findViewById(R.id.btn_back);
    }
}