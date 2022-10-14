package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.fragmentAdapter;
import Models.Users;
import Objects.RealtimeDB;
import Objects.TextModifier;

public class profile_user_page extends AppCompatActivity {

    private ExtendedFloatingActionButton btn_post;
    private FloatingActionButton btn_addPhotos, btn_addVideos;
    private TextView tv_addVidText, tv_addPhotoText, tv_userName;
    private LinearLayout backBtn;
    private fragmentAdapter adapter;
    private ImageView iv_userPhoto;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;

    private StorageReference userStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase;

    private String userID;

    private ArrayList<Users> arrUsers = new ArrayList<>();

    Boolean isAllFabsVisible;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        setRef();
        generateUserData();
        buttonActivity();
        generateTabLayout();
        clickListeners();
    }

    private void generateUserData() {

        TextModifier textModifier = new TextModifier();

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(profile_user_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void clickListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_user_page.this, homepage.class);
                startActivity(intent);
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFabsVisible)
                {

                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    btn_addPhotos.show();
                    btn_addVideos.show();
                    tv_addVidText.setVisibility(View.VISIBLE);
                    tv_addPhotoText.setVisibility(View.VISIBLE);


                    // Now extend the parent FAB, as
                    // user clicks on the shrinked
                    // parent FAB
                    btn_post.extend();

                    // make the boolean variable true as
                    // we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = true;
                }
                else
                {

                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs GONE.
                    btn_addPhotos.hide();
                    btn_addVideos.hide();
                    tv_addVidText.setVisibility(View.GONE);
                    tv_addPhotoText.setVisibility(View.GONE);

                    // Set the FAB to shrink after user
                    // closes all the sub FABs
                    btn_post.shrink();

                    // make the boolean variable false
                    // as we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = false;
                }
            }
        });

        btn_addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(profile_user_page.this, "Add Photo Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btn_addVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(profile_user_page.this, "Add Video Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buttonActivity() {
        btn_addPhotos.hide();
        btn_addVideos.hide();
        tv_addVidText.setVisibility(View.GONE);
        tv_addPhotoText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        btn_post.shrink();
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

        btn_post = findViewById(R.id.btn_post);

        btn_addPhotos = findViewById(R.id.btn_addPhotos);
        btn_addVideos = findViewById(R.id.btn_addVideos);

        tv_addVidText = findViewById(R.id.tv_addVidText);
        tv_addPhotoText = findViewById(R.id.tv_addPhotoText);
        tv_userName = findViewById(R.id.tv_userName);

        tab_layout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

        iv_userPhoto = findViewById(R.id.iv_userPhoto);
    }
}