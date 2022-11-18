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

public class profile_user_page extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    private static final int PICK_VID = 2;

    private TextView tv_userName, tv_category, tv_connectionsCount;
    private LinearLayout backBtn, event_layout;
    private ImageView iv_userPhoto;
    private ProgressDialog progressDialog;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private fragmentAdapter adapter;
    private fragmentAdapterProfile fragmentAdapterProfile;

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


    private void clickListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_user_page.this, homepage.class);
                startActivity(intent);
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

        tv_userName = findViewById(R.id.tv_userName);
        tv_connectionsCount = findViewById(R.id.tv_connectionsCount);
        tv_category = findViewById(R.id.tv_category);

        tab_layout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

        iv_userPhoto = findViewById(R.id.iv_userPhoto);
    }

//    @SuppressLint("SetTextI18n")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMG) {
//            if (resultCode == RESULT_OK) {
//                if (data.getClipData() != null) {
//                    int count = data.getClipData().getItemCount();
//
//                    int CurrentImageSelect = 0;
//
//                    while (CurrentImageSelect < count) {
//                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
//                        arrImageList.add(imageuri);
//                        CurrentImageSelect = CurrentImageSelect + 1;
//                    }
//
//                    int imageCount = arrImageList.size();
//
//                    uploadPhotos(imageCount);
//
//
//                }
//                else
//                {
//                    Uri imageuri = data.getData();
//                    if (imageuri != null)
//                    {
//
//                        arrImageList.add(imageuri);
//
//                        int imageCount = arrImageList.size();
//                        uploadPhotos(imageCount);
//                    }
//                }
//
//            }
//
//        }
//        else  if (requestCode == PICK_VID) {
//            if (resultCode == RESULT_OK) {
//                if (data.getClipData() != null) {
//                    int count = data.getClipData().getItemCount();
//
//                    int CurrentVideoSelect = 0;
//
//                    while (CurrentVideoSelect < count) {
//                        Uri videouri = data.getClipData().getItemAt(CurrentVideoSelect).getUri();
//                        arrVideoList.add(videouri);
//                        CurrentVideoSelect = CurrentVideoSelect + 1;
//                    }
//
//                    int vidCount = arrVideoList.size();
//                    uploadVideos(vidCount);
//
//                }
//                else {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//
//                        arrVideoList.add(uri);
//
//                        int vidCount = arrVideoList.size();
//                        uploadVideos(vidCount);
//
//
//                    }
//                }
//
//            }
//
//        }
//
//    }
//
//    private void uploadVideos(int vidCount) {
//
//        progressDialog = new ProgressDialog(profile_user_page.this);
//        progressDialog.setTitle("Uploading " + vidCount + " videos");
//        progressDialog.show();
//
//        for (uploads=0; uploads < arrVideoList.size(); uploads++)
//        {
//            Uri Video  = arrVideoList.get(uploads);
//
//            long videoTime = System.currentTimeMillis();
//            String fileType = getfiletype(Video);
//            String videoName = videoTime + Video.getLastPathSegment().toString() + uploads + "." + fileType;
//
//            StorageReference videoRef = videoStorage.child(videoName);
//
//            videoRef.putFile(arrVideoList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
//                            String url = String.valueOf(uri);
//                            SendVidLink(url, videoName);
//                        }
//                    });
//
//                }
//            });
//        }
//    }
//
//    private void SendVidLink(String url, String videoName) {
//
//        Videos videos = new Videos(userID, url, videoName);
//
//        videoDatabase.push().setValue(videos).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                arrVideoList.clear();
//                Toast.makeText(profile_user_page.this, "Upload complete", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                Intent intent = new Intent(profile_user_page.this, profile_user_page.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void uploadPhotos(int imageCount) {
//
//        progressDialog = new ProgressDialog(profile_user_page.this);
//        progressDialog.setTitle("Uploading " + imageCount + " photos");
//        progressDialog.show();
//
//
//        for (uploads=0; uploads < arrImageList.size(); uploads++)
//        {
//            Uri Image  = arrImageList.get(uploads);
//
//            long imageTime = System.currentTimeMillis();
//            String imageName = imageTime + Image.getLastPathSegment().toString();
//
//            StorageReference imagename = photoStorage.child(imageName);
//
//            imagename.putFile(arrImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
//                            String url = String.valueOf(uri);
//                            SendLink(url, imageName);
//                        }
//                    });
//
//                }
//            });
//
//        }
//
//    }
//
//    private void SendLink(String url, String imageName) {
//
//        Photos photos = new Photos(userID, url, imageName);
//
//        photoDatabase.push().setValue(photos).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                arrImageList.clear();
//                Toast.makeText(profile_user_page.this, "Upload complete", Toast.LENGTH_SHORT);
//                progressDialog.dismiss();
//                Intent intent = new Intent(profile_user_page.this, profile_user_page.class);
//                startActivity(intent);
//
//            }
//        });
//    }
//
//    private String getfiletype(Uri videouri) {
//        ContentResolver r = getContentResolver();
//        // get the file type ,in this case its mp4
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
//    }
}