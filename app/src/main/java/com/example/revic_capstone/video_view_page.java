package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import Models.Videos;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class video_view_page extends AppCompatActivity {

    private List<Videos> arrUrl = new ArrayList<Videos>();
    private String userID, category, vidName, vidLink;
    private int currentPosition;

    private ImageView iv_deletPhoto;
    private TextView tv_back;
    private VideoView videoView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_page);

        setRef();

        vidLink = getIntent().getStringExtra("vidLink");
        vidName = getIntent().getStringExtra("vidName");
        userID = getIntent().getStringExtra("userID");
        loadVid(vidLink);

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


                new SweetAlertDialog(video_view_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete")
                        .setContentText(vidName + "?")
                        .setCancelText("Cancel")
                        .setConfirmButton("Delete", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                deleteVideoInDb();

                            }
                        })
                .show();
            }
        });

    }

    private void deleteVideoInDb() {

        DatabaseReference postsDatabase = FirebaseDatabase.getInstance().getReference("Posts");
        StorageReference postStorage = FirebaseStorage.getInstance().getReference("Posts");

        progressDialog = new ProgressDialog(video_view_page.this);
        progressDialog.setTitle("Deleting video: " + vidName );
        progressDialog.show();

        Query query = postsDatabase
                .orderByChild("fileName")
                .equalTo(vidName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StorageReference imageRef = postStorage.child(userID).child(vidName);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    imageRef.delete();
                    dataSnapshot.getRef().removeValue();

                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();


                        Toast.makeText(video_view_page.this, "Video: " + vidName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(video_view_page.this, profile_user_page.class);
                        video_view_page.this.startActivity(intent);


                    }
                }, 4000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadVid(String vidUri) {

        Uri uri = Uri.parse(vidUri);

        // sets the resource from the
        // videoUrl to the videoView
        videoView.setVideoURI(uri);

        // creating object of
        // media controller class
        MediaController mediaController = new MediaController(this);

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(videoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(videoView);

        // sets the media controller to the videoView
        videoView.setMediaController(mediaController);

        // starts the video
        // videoView.start();
    }

    private void setRef() {

        videoView = findViewById(R.id.videoView);
        tv_back = findViewById(R.id.tv_back);
        iv_deletPhoto = findViewById(R.id.iv_deletPhoto);
    }
}