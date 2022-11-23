package Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revic_capstone.R;
import com.example.revic_capstone.change_password_page;
import com.example.revic_capstone.edit_profile_page;
import com.example.revic_capstone.homepage;
import com.example.revic_capstone.intro_logo;
import com.example.revic_capstone.my_wallet_page;
import com.example.revic_capstone.profile_user_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.fragmentAdapter;
import Adapters.fragmentAdapterProfile;
import Models.Posts;
import Models.Ratings;
import Models.Users;
import Models.Wallets;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMG = 1;
    private static final int PICK_VID = 2;

    private TextView tv_userName, tv_category, tv_connectionsCount, tv_postBtn;
    private TextView  tv_editProfile, tv_changePassword, tv_privacyPolicy, tv_aboutUs, tv_logout,
            tv_postOrEvents, tv_eventsCount, tv_userRating, tv_postsCount, tv_myWallet;
    private LinearLayout event_layout;
    private ImageView iv_userPhoto;
    private ProgressDialog progressDialog;
    private RatingBar rb_userRating;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private fragmentAdapter adapter;
    private fragmentAdapterProfile fragmentAdapterProfile;
    private ArrayAdapter<CharSequence> adapterSettings;

    private StorageReference photoStorage, videoStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, walletDatabase, postDatabase, eventDatabase,
            ratingDatabase;

    private String userID, category;
    private int uploads = 0;

    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private ArrayList<Uri> arrVideoList = new ArrayList<Uri>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        postDatabase = FirebaseDatabase.getInstance().getReference("Posts");
        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        photoStorage = FirebaseStorage.getInstance().getReference("Photos").child(userID);
        videoStorage = FirebaseStorage.getInstance().getReference("Videos").child(userID);

        setRef(view);
        generateUserData();
        generateRatingAverage();
        clickListeners();


        return view;
    }

    private void generateRatingAverage() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0, averageRating = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;
                    }

                    averageRating = totalRating / counter;
                    String ratingCounter = "(" + String.valueOf(averageRating) + ")";
                    tv_postsCount.setText(counter+"");
                    tv_userRating.setText(ratingCounter);
                    rb_userRating.setRating((float) averageRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    category = users.getCategory();


                    tv_userName.setText(fname + " " + lname);
                    tv_connectionsCount.setText(connections+"");
                    tv_category.setText(category);

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_userPhoto);

                }

                if(category.equals("Musician"))
                {
                    generateTabLayout();
                    tv_postOrEvents.setText("Posts");
                    generatePostsData();
                }
                else
                {
                    generateTabLayoutEvents();
                    tv_postOrEvents.setText("Events");
                    generateEventsData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void generateEventsData() {

        Query query = eventDatabase.orderByChild("userID").equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    long count = snapshot.getChildrenCount();
                    tv_eventsCount.setText(count+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generatePostsData() {

        Query query = postDatabase.orderByChild("userId").equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    long count = snapshot.getChildrenCount();
                    tv_eventsCount.setText(count+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clickListeners() {

        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), edit_profile_page.class);
                intent.putExtra("myCategory", category);
                startActivity(intent);
            }
        });

        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), change_password_page.class);
                intent.putExtra("User Email", user.getEmail());
                startActivity(intent);
            }
        });

        tv_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "About us coming soon.", Toast.LENGTH_SHORT).show();
            }
        });

        tv_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Privacy policy coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!")
                        .setCancelText("Cancel")
                        .setConfirmButton("Log Out", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), intro_logo.class));

                            }
                        })
                        .setContentText("Are you sure \nyou want to logout?")
                        .show();

            }
        });

        tv_postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), homepage.class);
                intent.putExtra("pageNumber", "3");
                intent.putExtra("myCategory", category);
                startActivity(intent);
            }
        });

        tv_myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkIfWalletExist();

            }
        });

    }

    private void checkIfWalletExist() {

        Query query = walletDatabase.orderByChild("userID").equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    Intent intent = new Intent(getContext(), my_wallet_page.class);
                    startActivity(intent);


                }
                else
                {
                    createWallet();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createWallet() {

        double i = 0;

        Wallets wallets = new Wallets(userID, i);

        walletDatabase.push().setValue(wallets).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(getContext(), my_wallet_page.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void generateTabLayout() {

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.posts));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.photos));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.videos));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.my_application));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ongoing));

        FragmentManager fragmentManager = getChildFragmentManager();
        adapter = new fragmentAdapter(fragmentManager, getLifecycle());
        vp_viewPager2.setSaveEnabled(false);
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

        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentAdapterProfile = new fragmentAdapterProfile(fragmentManager, getLifecycle());
        vp_viewPager2.setSaveEnabled(false);
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

    private void setRef(View view) {

        tv_editProfile = view.findViewById(R.id.tv_editProfile);
        tv_changePassword = view.findViewById(R.id.tv_changePassword);
        tv_privacyPolicy = view.findViewById(R.id.tv_privacyPolicy);
        tv_aboutUs = view.findViewById(R.id.tv_aboutUs);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_userName = view.findViewById(R.id.tv_userName);
        tv_connectionsCount = view.findViewById(R.id.tv_connectionsCount);
        tv_category = view.findViewById(R.id.tv_category);
        tv_postBtn = view.findViewById(R.id.tv_postBtn);
        tv_eventsCount = view.findViewById(R.id.tv_eventsCount);
        tv_postOrEvents = view.findViewById(R.id.tv_postOrEvents);
        tv_userRating = view.findViewById(R.id.tv_userRating);
        tv_postsCount = view.findViewById(R.id.tv_postsCount);
        tv_myWallet = view.findViewById(R.id.tv_myWallet);

        rb_userRating = view.findViewById(R.id.rb_userRating);

        tab_layout = view.findViewById(R.id.tab_layout);
        vp_viewPager2 = view.findViewById(R.id.vp_viewPager2);

        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);

    }
}