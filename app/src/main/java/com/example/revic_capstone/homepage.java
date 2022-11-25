package com.example.revic_capstone;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Fragments.HomeFragmentForRestAndEO;
import Fragments.PostEventsFragment;
import Fragments.PostPhotosAndVidFragment;
import Fragments.HomeFragment;
import Fragments.NotificationFragment;
import Fragments.ProfileFragment;
import Fragments.MessagesFragment;
import Models.Users;

public class homepage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private String userID, pageNumber, myCategory;

    HomeFragment homeFragment = new HomeFragment();
    HomeFragmentForRestAndEO homeFragmentForRestAndEO = new HomeFragmentForRestAndEO();
    PostPhotosAndVidFragment postPhotosAndVidFragment = new PostPhotosAndVidFragment();
    PostEventsFragment postEventsFragment = new PostEventsFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    MessagesFragment messagesFragment = new MessagesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        myCategory = getIntent().getStringExtra("myCategory");
        pageNumber = getIntent().getStringExtra("pageNumber");

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        bottomNavigationView = findViewById(R.id.bottom_nav);


        if(pageNumber != null) {

            if(pageNumber.equals("3"))
            {

                if( myCategory.equals("Musician"))
                {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, postPhotosAndVidFragment);
                    fragmentTransaction.commitNow();

                    bottomNavigationView.setSelectedItemId(R.id.event);

                }
                else
                {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, postEventsFragment);
                    fragmentTransaction.commitNow();

                    bottomNavigationView.setSelectedItemId(R.id.event);
                }

            }
            else if(pageNumber.equals("5"))
            {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, profileFragment);
                fragmentTransaction.commitNow();

                bottomNavigationView.setSelectedItemId(R.id.profile);
            }

        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){
                    case R.id.home:

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;

                    case R.id.event:

                        if(myCategory != null)
                        {
                            if( myCategory.equals("Musician"))
                            {
                                getSupportFragmentManager().beginTransaction().replace(R.id.container,postPhotosAndVidFragment).commit();
                                return true;
                            }
                        }

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, postEventsFragment).commit();
                        return true;

                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,notificationFragment).commit();
                        return true;

                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();
                        return true;

                    case R.id.messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,messagesFragment).commit();
                        return true;
                    default:


                }
                return false;
            }
        });
    }

}