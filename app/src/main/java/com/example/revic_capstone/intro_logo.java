package com.example.revic_capstone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import Models.Users;
import Models.Wallets;

public class intro_logo extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private String myUserId, myCategory;
    private DatabaseReference userDatabase , walletDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_logo);

        user = FirebaseAuth.getInstance().getCurrentUser();


        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");

        if(!(user == null))
        {
            myUserId = user.getUid();
            generateMyUserData();
            checkIfWalletExist();
        }



        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(!(user == null))
                {
                    Intent intent = new Intent(intro_logo.this, homepage.class);
                    intent.putExtra("myPosts", "1");
                    intent.putExtra("myCategory", myCategory);
                    startActivity(intent);
                    finish();

                }else{
                    Intent intent;
                    intent = new Intent(intro_logo.this, login_page.class);
                    startActivity(intent);
                    finish();
                }



//                if(user == null)
//                {
//                    intent = new Intent(intro_logo.this, login_page.class);
//                }
//                else
//                {
//                    intent = new Intent(intro_logo.this, homepage.class);
//                }





            }
        }, 2000);
    }

    private void generateMyUserData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(users != null) {

                    myCategory = users.getCategory();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void checkIfWalletExist() {

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists())
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

        Wallets wallets = new Wallets(myUserId, i);

        walletDatabase.push().setValue(wallets).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {



            }
        });
    }

}