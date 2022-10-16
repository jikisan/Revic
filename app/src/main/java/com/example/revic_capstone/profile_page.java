package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.fragmentAdapter;
import Models.Chat;
import Models.Users;
import Objects.TextModifier;

public class profile_page extends AppCompatActivity {

    private LinearLayout backBtn;
    private fragmentAdapter adapter;
    private TextView tv_userName, tv_messageBtn, tv_connectBtn;
    private ImageView iv_userPhoto;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;

    private StorageReference userStorage, photoStorage, videoStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, photoDatabase, videoDatabase, chatDatabase;

    private String userID, userIdFromSearch, chatUid;

    private ArrayList<Users> arrUsers = new ArrayList<>();
    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private ArrayList<Uri> arrVideoList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userIdFromSearch = getIntent().getStringExtra("userID");

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");
        videoDatabase = FirebaseDatabase.getInstance().getReference("Videos");
        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");

        photoStorage = FirebaseStorage.getInstance().getReference("Photos").child(userID);
        videoStorage = FirebaseStorage.getInstance().getReference("Videos").child(userID);

        setRef();
        generateUserData();
        clickListeners();
        generateTabLayout();
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

        tv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                validatePrevChatExistence();



            }
        });

    }

    private void validatePrevChatExistence() {

        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        String uid1 = userID + "_" + userIdFromSearch;
                        String uid2 = userIdFromSearch + "_" + userID;

                        String checkChatUid = dataSnapshot.getKey().toString();

                        if(checkChatUid.equals(uid1))
                        {
                            chatUid = uid1;
                        }
                        else if (checkChatUid.equals(uid2))
                        {
                            chatUid = uid2;
                        }
                        else
                        {
                            chatUid = userID + "_" + userIdFromSearch;
                        }

                    }
                }
                else
                {
                    chatUid = userID + "_" + userIdFromSearch;
                }


                Intent intent = new Intent(profile_page.this, chat_activity.class);
                intent.putExtra("userIdFromSearch", userIdFromSearch);
                intent.putExtra("chatId", chatUid);
                startActivity(intent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateTabLayout() {

        tab_layout.addTab(tab_layout.newTab().setText("Photos"));
        tab_layout.addTab(tab_layout.newTab().setText("Videos"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new fragmentAdapter(fragmentManager, getLifecycle());



        vp_viewPager2.setAdapter(adapter);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab_layout.selectTab(tab_layout.getTabAt(position));
            }
        });
    }

    private void setRef() {

        backBtn = findViewById(R.id.backBtn);
        tv_userName = findViewById(R.id.tv_userName);
        tv_messageBtn = findViewById(R.id.tv_messageBtn);
        tv_connectBtn = findViewById(R.id.tv_connectBtn);

        iv_userPhoto = findViewById(R.id.iv_userPhoto);

        tab_layout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);
    }
}