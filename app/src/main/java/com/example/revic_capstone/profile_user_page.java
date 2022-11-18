package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.fragmentAdapter;
import Adapters.fragmentAdapterProfile;
import Models.Connections;
import Models.Photos;
import Models.Users;
import Models.Videos;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class profile_user_page extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    private static final int PICK_VID = 2;

    private TextView tv_userName, tv_category, tv_connectionsCount;
    private TextView  tv_editProfile, tv_changePassword, tv_privacyPolicy, tv_aboutUs, tv_logout;
    private LinearLayout backBtn, event_layout;
    private ImageView iv_userPhoto;
    private ProgressDialog progressDialog;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private fragmentAdapter adapter;
    private fragmentAdapterProfile fragmentAdapterProfile;
    private ArrayAdapter<CharSequence> adapterSettings;

    private StorageReference photoStorage, videoStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, connectionsDatabase;

    private String userID, category;
    private int uploads = 0;

    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private ArrayList<Uri> arrVideoList = new ArrayList<Uri>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        connectionsDatabase = FirebaseDatabase.getInstance().getReference("Connections");

        photoStorage = FirebaseStorage.getInstance().getReference("Photos").child(userID);
        videoStorage = FirebaseStorage.getInstance().getReference("Videos").child(userID);

        setRef();
        generateUserData();
        clickListeners();
    }

    private void clickListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_user_page.this, homepage.class);
                startActivity(intent);
            }
        });

        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(profile_user_page.this, "Edit Profile coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_user_page.this, change_password_page.class);
                intent.putExtra("User Email", user.getEmail());
                startActivity(intent);
            }
        });

        tv_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(profile_user_page.this, "About us coming soon.", Toast.LENGTH_SHORT).show();
            }
        });

        tv_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(profile_user_page.this, "Privacy policy coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(profile_user_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!")
                        .setCancelText("Cancel")
                        .setConfirmButton("Log Out", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(profile_user_page.this, intro_logo.class));

                            }
                        })
                        .setContentText("Are you sure \nyou want to logout?")
                        .show();

            }
        });

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

                    int connections = users.getConnections();
                    String category = users.getCategory();


                    tv_userName.setText(fname + " " + lname);
                    tv_connectionsCount.setText(connections+"");
                    tv_category.setText(category);

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_userPhoto);

                }
                category = users.getCategory();

                if(category.equals("Musician"))
                {
                    generateTabLayout();
                }
                else
                {
                    generateTabLayoutEvents();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_user_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void generateTabLayout() {

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.posts));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.photos));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.videos));

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

    private void generateTabLayoutEvents() {


        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.events));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.my_application));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ongoing));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapterProfile = new fragmentAdapterProfile(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(fragmentAdapterProfile);

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

        tv_editProfile = findViewById(R.id.tv_editProfile);
        tv_changePassword = findViewById(R.id.tv_changePassword);
        tv_privacyPolicy = findViewById(R.id.tv_privacyPolicy);
        tv_aboutUs = findViewById(R.id.tv_aboutUs);
        tv_logout = findViewById(R.id.tv_logout);
        tv_userName = findViewById(R.id.tv_userName);
        tv_connectionsCount = findViewById(R.id.tv_connectionsCount);
        tv_category = findViewById(R.id.tv_category);

        tab_layout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

        iv_userPhoto = findViewById(R.id.iv_userPhoto);

    }

}