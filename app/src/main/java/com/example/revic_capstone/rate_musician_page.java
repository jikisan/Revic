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

import Models.Ratings;
import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class rate_musician_page extends AppCompatActivity {

    private List<Ratings> arrRatings = new ArrayList<>();

    private ImageView iv_userPhoto;
    private TextView tv_backBtn, tv_clientName;
    private EditText et_ratingComment;
    private Button btn_ratingSubmit;
    private RatingBar ratingBar;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, contractDatabase, userDatabase, ratingDatabase;

    private String myUserId, eventId, contractId, employeeId, applicationId,
        myUserName, musicianName, myCategory;
    private int employeeRatingCount = 0;
    private double musicianRatings = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_musician_page);

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
        generateMusicianData();
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

                int noOfStars = ratingBar.getNumStars();
                float getRating = ratingBar.getRating();

                new SweetAlertDialog(rate_musician_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("FINISH REVIEW")
                        .setCancelText("Back")
                        .setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                final ProgressDialog progressDialog = new ProgressDialog(rate_musician_page.this);
                                progressDialog.setTitle("Processing review...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                submitRating(getRating);

                            }
                        })
                        .setContentText("Give " + musicianName + "\na " + getRating + "/" + noOfStars +  " review?")
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

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(employeeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);

                        arrRatings.add(ratings);
                    }

                    employeeRatingCount = arrRatings.size();
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
                    String fname = users.getFname();
                    String lname = users.getLname();
                    musicianName = fname + " " + lname;

                    Picasso.get().load(imageUrl).into(iv_userPhoto);
                    tv_clientName.setText(musicianName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        iv_userPhoto = findViewById(R.id.iv_userPhoto);

        tv_backBtn = findViewById(R.id.tv_backBtn);
        tv_clientName = findViewById(R.id.tv_clientName);

        et_ratingComment = findViewById(R.id.et_ratingComment);

        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);

        ratingBar = findViewById(R.id.ratingBar);
    }

    private void submitRating(float getRating) {

        String comment = et_ratingComment.getText().toString();
        String ratingType = "users";

        Ratings ratings = new Ratings(employeeId, musicianName, myUserId, myUserName, getRating, comment, ratingType, contractId);

        ratingDatabase.push().setValue(ratings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    updateContract();

                }

            }
        });
    }

    private void updateContract() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("ratedByCreator", true);
        contractDatabase.child(contractId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateMusicianData();
            }
        });

    }

    private void updateMusicianData() {

        int i = 1;
        int newRatingCount = employeeRatingCount + i;


        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("rating", newRatingCount);

        userDatabase.child(employeeId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(rate_musician_page.this, homepage.class);
                intent.putExtra("pageNumber", "5");
                intent.putExtra("myCategory", myCategory);
                startActivity(intent);
            }
        });
    }
}