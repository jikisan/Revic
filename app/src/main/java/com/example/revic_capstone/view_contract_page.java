package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import Models.Contracts;
import Models.Events;
import Models.Ratings;
import Models.Users;

public class view_contract_page extends AppCompatActivity {

    private TextView tv_ongoing, tv_terminated, tv_done,
        tv_userName, tv_category,
        tv_userRatingCount, tv_fname, tv_lname, tv_contactNum, tv_gender,
        tv_eventName, tv_eventAddress, tv_eventDate, tv_eventTimeStart,
            tv_eventTimeEnd, tv_eventDescription, tv_eventPrice;
    private ImageView iv_userPhoto, iv_backBtn;
    private Button btn_terminate, btn_complete, btn_rate;
    private LinearLayout linearLayout7;
    private RatingBar rb_userRating;

    private List<Events> arrEvents = new ArrayList<>();
    private List<String> arrEventsId = new ArrayList<>();

    private ProgressBar progressBar;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, contractDatabase, userDatabase, ratingDatabase;

    private String myUserId, eventId, contractId, employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contract_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        contractId = getIntent().getStringExtra("contractId");
        employeeId = getIntent().getStringExtra("employeeId");
        eventId = getIntent().getStringExtra("eventId");

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        setRef();
        generateContractData();
        generateMusicianData();
        generateRatingAverage();
        clickListeners();
    }

    private void generateRatingAverage() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(employeeId);

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
                    String ratingCounter = "(" + String.valueOf(counter) + ")";
                    tv_userRatingCount.setText(ratingCounter);
                    rb_userRating.setRating((float) averageRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListeners() {

        iv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_contract_page.this, homepage.class);
                intent.putExtra("pageNumber", "5");
                intent.putExtra("myCategory", "Event Organizer");
                intent.putExtra("myPosts", "1");
                startActivity(intent);
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view_contract_page.this, "Contract Completed", Toast.LENGTH_SHORT).show();

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("contractStatus", "done");
                contractDatabase.child(contractId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        linearLayout7.setVisibility(View.INVISIBLE);
                        btn_rate.setVisibility(View.VISIBLE);

                        tv_terminated.setVisibility(View.INVISIBLE);
                        tv_ongoing.setVisibility(View.INVISIBLE);
                        tv_done.setVisibility(View.VISIBLE);
                    }
                });




            }
        });

        btn_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view_contract_page.this, "Contract Terminated", Toast.LENGTH_SHORT).show();

                String newValue = "terminated";
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("contractStatus", newValue);

                contractDatabase.child(contractId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        linearLayout7.setVisibility(View.INVISIBLE);

                        tv_done.setVisibility(View.INVISIBLE);
                        tv_ongoing.setVisibility(View.INVISIBLE);
                        tv_terminated.setVisibility(View.VISIBLE);

                    }
                });

            }
        });

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), rate_musician_page.class);
                intent.putExtra("contractId", contractId);
                intent.putExtra("eventId", eventId);
                intent.putExtra("employeeId", employeeId);
                view.getContext().startActivity(intent);

            }
        });
    }

    private void generateContractData() {

        contractDatabase.child(contractId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Contracts contracts = snapshot.getValue(Contracts.class);

                    String contractStatus = contracts.getContractStatus();
                    Boolean isRatedByMusician = contracts.getRatedByMusician();
                    Boolean isRatedByCreator = contracts.getRatedByCreator();

                    if(contractStatus.toLowerCase(Locale.ROOT).equals("ongoing"))
                    {
                        tv_ongoing.setVisibility(View.VISIBLE);


                    } else if(contractStatus.toLowerCase(Locale.ROOT).equals("terminated"))
                    {
                        tv_terminated.setVisibility(View.VISIBLE);
                        linearLayout7.setVisibility(View.INVISIBLE);

                    }else if(contractStatus.toLowerCase(Locale.ROOT).equals("done"))
                    {
                        tv_done.setVisibility(View.VISIBLE);
                        linearLayout7.setVisibility(View.INVISIBLE);

                        if(!isRatedByCreator){
                            btn_rate.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateMusicianData() {

        userDatabase.child(employeeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    String imageUrl = users.getImageUrl();
                    long rating = users.getRating();
                    String fname = users.getFname();
                    String lname = users.getLname();
                    String contactNum = users.getContactNum();
                    String gender = users.getGender();

                    Picasso.get().load(imageUrl).into(iv_userPhoto);
                    tv_userName.setText(fname + " " + lname);
                    tv_userRatingCount.setText(rating + "");
                    tv_fname.setText(fname);
                    tv_lname.setText(lname);
                    tv_contactNum.setText(contactNum);

                    if( gender == null)
                    {
                        tv_gender.setText("Not Defined");
                    }
                    else{
                        tv_gender.setText(gender);
                    }

                    generateEventData();

                }

                progressBar.setVisibility(View.GONE);
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

                    String eventName = events.getEventName();
                    double eventPrice = events.getEventPrice();
                    String eventAddress = events.getEventAddress();
                    String eventDate = events.getEventDateSched();
                    String eventStart = events.getTimeStart();
                    String eventEnd = events.getTimeEnd();
                    String eventDescription = events.getEventDescription();

                    tv_eventName.setText(eventName);
                    tv_eventAddress.setText(eventAddress);
                    tv_eventDate.setText(eventDate);
                    tv_eventTimeStart.setText(eventStart);
                    tv_eventTimeEnd.setText(eventEnd);
                    tv_eventDescription.setText(eventDescription);
                    tv_eventPrice.setText("₱ "+eventPrice+" / Event");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        progressBar = findViewById(R.id.progressBar);

        rb_userRating = findViewById(R.id.rb_userRating);

        tv_ongoing = findViewById(R.id.tv_ongoing);
        tv_terminated = findViewById(R.id.tv_terminated);
        tv_done = findViewById(R.id.tv_done);

        tv_userName = findViewById(R.id.tv_userName);
        tv_category = findViewById(R.id.tv_category);

        tv_userRatingCount = findViewById(R.id.tv_userRatingCount);
        tv_fname = findViewById(R.id.tv_fname);
        tv_lname = findViewById(R.id.tv_lname);
        tv_contactNum = findViewById(R.id.tv_contactNum);
        tv_gender = findViewById(R.id.tv_gender);

        tv_eventName = findViewById(R.id.tv_eventName);
        tv_eventAddress = findViewById(R.id.tv_eventAddress);
        tv_eventDate = findViewById(R.id.tv_eventDate);
        tv_eventTimeStart = findViewById(R.id.tv_eventTimeStart);
        tv_eventTimeEnd = findViewById(R.id.tv_eventTimeEnd);
        tv_eventDescription = findViewById(R.id.tv_eventDescription);
        tv_eventPrice = findViewById(R.id.tv_eventPrice);

        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        iv_backBtn = findViewById(R.id.iv_backBtn);

        btn_terminate = findViewById(R.id.btn_terminate);
        btn_complete = findViewById(R.id.btn_complete);
        btn_rate = findViewById(R.id.btn_rate);

        linearLayout7 = findViewById(R.id.linearLayout7);
    }
}