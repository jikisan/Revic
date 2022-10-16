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
import Models.Photos;
import Models.Users;
import Models.Videos;
import Objects.TextModifier;

public class profile_user_page extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    private static final int PICK_VID = 2;

    private ExtendedFloatingActionButton btn_post;
    private FloatingActionButton btn_addPhotos, btn_addVideos;
    private TextView tv_addVidText, tv_addPhotoText, tv_userName;
    private LinearLayout backBtn;
    private fragmentAdapter adapter;
    private ImageView iv_userPhoto;
    private ProgressDialog progressDialog;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;

    private StorageReference photoStorage, videoStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, photoDatabase, videoDatabase;

    private String userID;
    private int uploads = 0;

    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private ArrayList<Uri> arrVideoList = new ArrayList<Uri>();

    private Boolean isAllFabsVisible;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");
        videoDatabase = FirebaseDatabase.getInstance().getReference("Videos");

        photoStorage = FirebaseStorage.getInstance().getReference("Photos").child(userID);
        videoStorage = FirebaseStorage.getInstance().getReference("Videos").child(userID);



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

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMG);            }
        });

        btn_addVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), PICK_VID);            }
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        arrImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }

                    int imageCount = arrImageList.size();

                    uploadPhotos(imageCount);


                }
                else
                {
                    Uri imageuri = data.getData();
                    if (imageuri != null)
                    {

                        arrImageList.add(imageuri);

                        int imageCount = arrImageList.size();
                        uploadPhotos(imageCount);
                    }
                }

            }

        }
        else  if (requestCode == PICK_VID) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentVideoSelect = 0;

                    while (CurrentVideoSelect < count) {
                        Uri videouri = data.getClipData().getItemAt(CurrentVideoSelect).getUri();
                        arrVideoList.add(videouri);
                        CurrentVideoSelect = CurrentVideoSelect + 1;
                    }

                    int vidCount = arrVideoList.size();
                    uploadVideos(vidCount);

                }
                else {
                    Uri uri = data.getData();
                    if (uri != null) {

                        arrVideoList.add(uri);

                        int vidCount = arrVideoList.size();
                        uploadVideos(vidCount);


                    }
                }

            }

        }

    }

    private void uploadVideos(int vidCount) {

        progressDialog = new ProgressDialog(profile_user_page.this);
        progressDialog.setTitle("Uploading " + vidCount + " videos");
        progressDialog.show();

        for (uploads=0; uploads < arrVideoList.size(); uploads++)
        {
            Uri Video  = arrVideoList.get(uploads);

            long videoTime = System.currentTimeMillis();
            String fileType = getfiletype(Video);
            String videoName = videoTime + Video.getLastPathSegment().toString() + uploads + "." + fileType;

            StorageReference videoRef = videoStorage.child(videoName);

            videoRef.putFile(arrVideoList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendVidLink(url, videoName);
                        }
                    });

                }
            });
        }
    }

    private void SendVidLink(String url, String videoName) {

        Videos videos = new Videos(userID, url, videoName);

        videoDatabase.push().setValue(videos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                arrVideoList.clear();
                Toast.makeText(profile_user_page.this, "Upload complete", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Intent intent = new Intent(profile_user_page.this, profile_user_page.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void uploadPhotos(int imageCount) {

        progressDialog = new ProgressDialog(profile_user_page.this);
        progressDialog.setTitle("Uploading " + imageCount + " photos");
        progressDialog.show();


        for (uploads=0; uploads < arrImageList.size(); uploads++)
        {
            Uri Image  = arrImageList.get(uploads);

            long imageTime = System.currentTimeMillis();
            String imageName = imageTime + Image.getLastPathSegment().toString();

            StorageReference imagename = photoStorage.child(imageName);

            imagename.putFile(arrImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendLink(url, imageName);
                        }
                    });

                }
            });

        }

    }

    private void SendLink(String url, String imageName) {

        Photos photos = new Photos(userID, url, imageName);

        photoDatabase.push().setValue(photos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                arrImageList.clear();
                Toast.makeText(profile_user_page.this, "Upload complete", Toast.LENGTH_SHORT);
                progressDialog.dismiss();
                Intent intent = new Intent(profile_user_page.this, profile_user_page.class);
                startActivity(intent);

            }
        });
    }

    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }
}