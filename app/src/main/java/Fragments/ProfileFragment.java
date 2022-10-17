package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revic_capstone.R;
import com.example.revic_capstone.change_password_page;
import com.example.revic_capstone.intro_logo;
import com.example.revic_capstone.profile_page;
import com.example.revic_capstone.profile_user_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Models.Users;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private String userID;

    private ProgressBar progressBar;
    private TextView tv_editProfile, tv_changePassword, tv_contactUs, tv_aboutUs, tv_logout,
            tv_bannerName, tv_userRating, tv_privacyPolicy, tv_myAddress, tv_back, tv_myRatings;
    private RatingBar rb_userRating;
    private ImageView iv_userPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        setRef(view);
        generateUserData();
        clickListeners();


        return view;
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

                    tv_bannerName.setText(fname + " " + lname);

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_userPhoto);

                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void clickListeners() {

        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Edit Profile coming soon", Toast.LENGTH_SHORT).show();
            }
        });


        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), change_password_page.class);
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
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!")
                        .setCancelText("Cancel")
                        .setConfirmButton("Log Out", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getActivity(), intro_logo.class));

                            }
                        })
                        .setContentText("Are you sure \nyou want to logout?")
                        .show();

            }
        }); //

    }

    private void setRef(View view) {

        tv_editProfile = view.findViewById(R.id.tv_editProfile);
        tv_changePassword = view.findViewById(R.id.tv_changePassword);
        tv_contactUs = view.findViewById(R.id.tv_contactUs);
        tv_aboutUs = view.findViewById(R.id.tv_aboutUs);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_bannerName = view.findViewById(R.id.tv_bannerName);
        tv_privacyPolicy = view.findViewById(R.id.tv_privacyPolicy);
        tv_userRating = view.findViewById(R.id.tv_userRating);

        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);

        progressBar = view.findViewById(R.id.progressBar);

        rb_userRating = view.findViewById(R.id.rb_userRating);
    }
}