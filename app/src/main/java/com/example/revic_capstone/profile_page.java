package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.HashMap;

import Adapters.fragmentAdapter;
import Models.Chat;
import Models.Connections;
import Models.Users;
import Objects.TextModifier;

public class profile_page extends AppCompatActivity {

    private LinearLayout backBtn, event_layout;
    private fragmentAdapter adapter;
    private TextView tv_userName, tv_messageBtn, tv_connectBtn, tv_disconnectBtn, tv_category, tv_noEvents, tv_connectionsCount;
    private ImageView iv_userPhoto;
    private RecyclerView rv_eventRv;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;

    private StorageReference userStorage, photoStorage, videoStorage;
    private FirebaseUser user;
    private DatabaseReference userDatabase, photoDatabase, videoDatabase, chatDatabase, connectionsDatabase;

    private String userID, userIdFromSearch, chatUid, category;
    private int connections = 0, connectionsCount = 0, myConnections = 0;

    private ArrayList<Users> arrUsers = new ArrayList<>();
    private ArrayList<Connections> connectionsArrayList = new ArrayList<Connections>();
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
        connectionsDatabase = FirebaseDatabase.getInstance().getReference("Connections");

        photoStorage = FirebaseStorage.getInstance().getReference("Photos").child(userID);
        videoStorage = FirebaseStorage.getInstance().getReference("Videos").child(userID);

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

                    connectionsCount = users.getConnections();
                    String imageUrl = users.getImageUrl();

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();
                    category = users.getCategory();

                    tv_userName.setText(fname + " " + lname);
                    tv_category.setText(category);
                    tv_connectionsCount.setText(connectionsCount + "");

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_userPhoto);

                }

                if(category.equals("Musician"))
                {
                    tab_layout.setVisibility(View.VISIBLE);
                    vp_viewPager2.setVisibility(View.VISIBLE);
                    event_layout.setVisibility(View.GONE);
                    generateTabLayout();
                }
                else
                {
                    tab_layout.setVisibility(View.GONE);
                    vp_viewPager2.setVisibility(View.GONE);
                    event_layout.setVisibility(View.VISIBLE);
                }

                generateConnections();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(users != null){

                    myConnections = users.getConnections();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_page.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void generateConnections() {

        connectionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Connections connections = dataSnapshot.getValue(Connections.class);

                        String user1 = connections.getUser1();
                        String user2 = connections.getUser2();

                        if(userID.equals(user1) && userIdFromSearch.equals(user2) || userID.equals(user2) && userIdFromSearch.equals(user1) )
                        {
                            tv_connectBtn.setVisibility(View.GONE);
                            tv_disconnectBtn.setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_page.this, homepage.class);
                startActivity(intent);
            }
        });

        tv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                validatePrevChatExistence();



            }
        });

        tv_connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Connections connections = new Connections(userID, userIdFromSearch);

                connectionsDatabase.push().setValue(connections).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        int i = 1;
                        updateConnectionsInDB(i);

                        Intent intent = new Intent(profile_page.this, profile_page.class);
                        intent.putExtra("userID", userIdFromSearch);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(profile_page.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        tv_disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(profile_page.this)
                        .setTitle("Warning!")
                        .setMessage("Remove connection for " + tv_userName.getText() + "?")
                        .setCancelable(true)
                        .setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                connectionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            Connections connections = dataSnapshot.getValue(Connections.class);

                                            String user1 = connections.getUser1();
                                            String user2 = connections.getUser2();

                                            if((user1.equals(userID) && user2.equals(userIdFromSearch) || user1.equals(userIdFromSearch) && user2.equals(userID)))
                                            {
                                                dataSnapshot.getRef().removeValue();

                                                int i = -1;
                                                updateConnectionsInDB(i);

                                                Intent intent = new Intent(profile_page.this, profile_page.class);
                                                intent.putExtra("userID", userIdFromSearch);
                                                startActivity(intent);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                            }
                        })
                        .show();

            }
        });

    }

    private void updateConnectionsInDB(int i) {

        int newValue = connectionsCount + i;
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("connections", newValue);
        userDatabase.child(userIdFromSearch).updateChildren(hashMap);

        int myConnectionsNewValue= myConnections + i;
        HashMap<String, Object> hashMapMyConnections = new HashMap<String, Object>();
        hashMapMyConnections.put("connections", myConnectionsNewValue);
        userDatabase.child(userID).updateChildren(hashMapMyConnections);


        Toast.makeText(profile_page.this, "Connected", Toast.LENGTH_LONG).show();
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
        event_layout = findViewById(R.id.event_layout);

        tv_userName = findViewById(R.id.tv_userName);
        tv_messageBtn = findViewById(R.id.tv_messageBtn);
        tv_connectBtn = findViewById(R.id.tv_connectBtn);
        tv_disconnectBtn = findViewById(R.id.tv_disconnectBtn);
        tv_category = findViewById(R.id.tv_category);
        tv_noEvents = findViewById(R.id.tv_noEvents);
        tv_connectionsCount = findViewById(R.id.tv_connectionsCount);

        rv_eventRv = findViewById(R.id.rv_eventRv);

        iv_userPhoto = findViewById(R.id.iv_userPhoto);

        tab_layout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);
    }

}

