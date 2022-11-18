package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.AdapterPhotoView;
import Models.Photos;
import Models.Posts;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class photo_view_page extends AppCompatActivity {

    private AdapterPhotoView adapterPhotoView;
    private List<Posts> arrPosts = new ArrayList<Posts>();

    private String myUserID, fileType, imageName, postUserId;
    private int currentPosition;

    private DatabaseReference postsDatabase;
    private FirebaseUser user;

    private ImageView iv_deletPhoto;
    private TextView tv_back;
    private ViewPager vp_photoFullscreen;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();

        currentPosition = getIntent().getIntExtra("current position", 0);
        fileType = getIntent().getStringExtra("fileType");
        postUserId = getIntent().getStringExtra("postUserId");

        postsDatabase = FirebaseDatabase.getInstance().getReference("Posts");


        setRef();
        generateImageData();
        getViewHolderValues();
        clickListeners();

        if(!postUserId.equals(myUserID))
        {
            iv_deletPhoto.setVisibility(View.GONE);
        }
    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_deletPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(photo_view_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!.")
                        .setContentText("Delete \n" + imageName + "?")
                        .setCancelText("Cancel")
                        .setConfirmButton("Delete", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                deletePhotoInDb();

                            }
                        })
                .show();
            }
        });
    }

    private void deletePhotoInDb() {

        StorageReference postStorage = FirebaseStorage.getInstance().getReference("Posts");

        progressDialog = new ProgressDialog(photo_view_page.this);
        progressDialog.setTitle("Deleting photo: " + imageName );
        progressDialog.show();

        Query query = postsDatabase
                .orderByChild("fileName")
                .equalTo(imageName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StorageReference imageRef = postStorage.child(postUserId).child(imageName);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    imageRef.delete();
                    dataSnapshot.getRef().removeValue();

                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();


                        Toast.makeText(photo_view_page.this, "Video: " + imageName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(photo_view_page.this, profile_user_page.class);
                        photo_view_page.this.startActivity(intent);

                    }
                }, 4000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateImageData() {

        // Initializing the ViewPagerAdapter
        adapterPhotoView = new AdapterPhotoView(photo_view_page.this, arrPosts);

        // Adding the Adapter to the ViewPager
        vp_photoFullscreen.setAdapter(adapterPhotoView);

        vp_photoFullscreen.postDelayed(new Runnable() {

            @Override
            public void run() {

                vp_photoFullscreen.setCurrentItem(currentPosition);
            }
        }, 500);
//        vp_photoFullscreen.setCurrentItem(currentPosition);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = postsDatabase
                .orderByChild("fileType")
                .equalTo("photo");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrPosts.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Posts posts = dataSnapshot.getValue(Posts.class);
                        String userId = posts.getUserId();

                        if(userId.equals(postUserId))
                        {
                            imageName = posts.getFileName();
                            arrPosts.add(posts);
                        }

                    }
                }


                adapterPhotoView.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setRef() {

        vp_photoFullscreen = findViewById(R.id.vp_photoFullscreen);
        tv_back = findViewById(R.id.tv_back);
        iv_deletPhoto = findViewById(R.id.iv_deletPhoto);
    }
}