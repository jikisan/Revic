package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import Models.Users;
import Objects.TextModifier;

public class profile_page extends AppCompatActivity {

    private LinearLayout backBtn;

    private TextView tv_userName;
    private ImageView iv_userPhoto;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, photoDatabase, videoDatabase;

    private String userID, userIdFromSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");
        videoDatabase = FirebaseDatabase.getInstance().getReference("Videos");

        userIdFromSearch = getIntent().getStringExtra("userID");

        setRef();
        generateUserData();
        clickListeners();
    }

    private void generateUserData() {

        TextModifier textModifier = new TextModifier();

        userDatabase.child(userIdFromSearch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(users != null){

                    String imageUrl = users.getImageUrl();

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();

                    tv_userName.setText(fname + " " + lname);

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_userPhoto);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void clickListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setRef() {

        backBtn = findViewById(R.id.backBtn);
        tv_userName = findViewById(R.id.tv_userName);
        iv_userPhoto = findViewById(R.id.iv_userPhoto);
    }
}