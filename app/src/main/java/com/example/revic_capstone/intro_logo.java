package com.example.revic_capstone;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class intro_logo extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_logo);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent;
                intent = new Intent(intro_logo.this, login_page.class);

//                if(user == null)
//                {
//                    intent = new Intent(intro_logo.this, login_page.class);
//                }
//                else
//                {
//                    intent = new Intent(intro_logo.this, homepage.class);
//                }


                startActivity(intent);


            }
        }, 2000);
    }

}