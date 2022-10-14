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
import cn.pedant.SweetAlert.SweetAlertDialog;

public class photo_view_page extends AppCompatActivity {

    private AdapterPhotoView adapterPhotoView;
    private List<Photos> arrUrl = new ArrayList<Photos>();

    private String userID, category, imageName;
    private int currentPosition;

    private DatabaseReference photoDatabase;
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
        userID = user.getUid();

        currentPosition = getIntent().getIntExtra("current position", 0);
        category = getIntent().getStringExtra("category");

        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        setRef();
        generateImageData();
        getViewHolderValues();
        clickListeners();
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

        DatabaseReference photoDB = FirebaseDatabase.getInstance().getReference("Photos");
        StorageReference photoStorage = FirebaseStorage.getInstance().getReference("Photos");

        progressDialog = new ProgressDialog(photo_view_page.this);
        progressDialog.setTitle("Deleting photo: " + imageName );
        progressDialog.show();

        Query query = photoDB
                .orderByChild("photoName")
                .equalTo(imageName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StorageReference imageRef = photoStorage.child(userID).child(imageName);

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
        adapterPhotoView = new AdapterPhotoView(photo_view_page.this, arrUrl, currentPosition, category);

        // Adding the Adapter to the ViewPager
        vp_photoFullscreen.setAdapter(adapterPhotoView);

        vp_photoFullscreen.postDelayed(new Runnable() {

            @Override
            public void run() {
                adapterPhotoView.notifyDataSetChanged();
                vp_photoFullscreen.setCurrentItem(currentPosition);
            }
        }, 500);
//        vp_photoFullscreen.setCurrentItem(currentPosition);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = photoDatabase
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Photos photos = dataSnapshot.getValue(Photos.class);
                    imageName = photos.getPhotoName().toString();
                    arrUrl.add(photos);
                }

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