package com.example.revic_capstone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Models.Restaurant;
import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class sign_up2_restaurant_page extends AppCompatActivity {

    private String category, password, username;
    private int connections = 0;

    private TextView textView, tv_date, tv_addValidID, tv_signIn;
    private ImageView iv_addPhoto, iv_userPhoto, iv_validID;
    private EditText et_nameOfEst, et_firstName, et_lastName, et_contactNumber;
    private Button btn_signUp;

    private FirebaseAuth fAuth;
    private DatabaseReference userDatabase;
    private StorageReference userStorage;
    private FirebaseUser user;

    public static final int USER_PIC = 3;
    public static final int VALID_ID = 2;

    private Uri userPicUri, validIdUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up2_restaurant_page);

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userStorage = FirebaseStorage.getInstance().getReference("Users");

        category = getIntent().getStringExtra("category");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        setRef();
        clickListener();

        textView.setText("Sign up (" + category + ")");
    }

    private void clickListener() {

        iv_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageForUserPic();
            }
        });

        tv_addValidID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageForValidID();
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInfo();
            }
        });

        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sign_up2_restaurant_page.this, login_page.class);
                startActivity(intent);
            }
        });

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        sign_up2_restaurant_page.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        month = month+1;
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
                        Date dateOfWeek = new Date(year, month, day-1);
                        String dayOfWeek = simpledateformat.format(dateOfWeek);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                        calendar.set(year, month, day);
                        String dateString = sdf.format(calendar.getTime());

                        tv_date.setText(dateString);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 1*24*60*60*1000);
                datePickerDialog.show();
            }
        });

    }

    private void validateInfo() {

        String nameOfEst = et_nameOfEst.getText().toString();
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String contactNum = et_contactNumber.getText().toString();
        String dob = tv_date.getText().toString();
        String company = "N/A";


            if (userPicUri == null)
            {
                Toast.makeText(this, "Please upload a establishment picture", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(nameOfEst))
            {
                et_nameOfEst.setError("This field is required");
            }
            else if (TextUtils.isEmpty(firstName))
            {
                et_firstName.setError("This field is required");
            }
            else if (TextUtils.isEmpty(lastName))
            {
                et_lastName.setError("This field is required");
            }
            else if (TextUtils.isEmpty(contactNum))
            {
                et_contactNumber.setError("This field is required");
            }
            else if (TextUtils.isEmpty(dob))
            {
                tv_date.setError("This field is required");
            }
            else if (validIdUri == null)
            {
                Toast.makeText(this, "Please upload a valid document", Toast.LENGTH_SHORT).show();
            }
            else
            {
                new AlertDialog.Builder(sign_up2_restaurant_page.this)
                        .setTitle("SIGN UP")
                        .setMessage("Please make sure all information entered are correct")
                        .setCancelable(true)
                        .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String userPicName = userPicUri.getLastPathSegment();
                                String validIdName = validIdUri.getLastPathSegment();


                                signUp(firstName, lastName, contactNum, dob, nameOfEst, userPicName, validIdName);

                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                            }
                        })
                        .show();
            }
    }

    private void signUp(String firstName, String lastName, String contactNum,
                        String dob, String nameOfEst, String userPicName, String validIdName) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating account");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            addingUserPicToStorage(firstName, lastName, contactNum, dob, nameOfEst, userPicName, validIdName);
                        }

                    }
                });

            }
        });
    }

    private void addingUserPicToStorage(String firstName, String lastName,
                                        String contactNum, String dob, String nameOfEst, String
                                                userPicName, String validIdName) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid().toString();

        StorageReference userPicRef = userStorage.child(userID).child(userPicName);
        userPicRef.putFile(userPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                userPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String userPicUrl = uri.toString();

                            StorageReference validIdRef = userStorage.child(userID).child(validIdName);
                            validIdRef.putFile(validIdUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    validIdRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            final String validIdUrl = uri.toString();

                                            saveToRealtimedb(userID, firstName, lastName, contactNum, dob, nameOfEst, userPicName, validIdName, userPicUrl, validIdUrl);

                                        }
                                    });

                                }
                            });
                    }
                });

            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(sign_up2_restaurant_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void saveToRealtimedb(String userID, String firstName, String lastName,
                                  String contactNum, String dob, String nameOfEst, String
                                          userPicName, String validIdName, String userPicUrl, String validIdUrl) {

        String gender = "N/A";
        Restaurant restaurant = new Restaurant(userID, username, password, userPicUrl, userPicName, nameOfEst, firstName, lastName, contactNum, dob, validIdUrl, validIdName, category, connections);

        userDatabase.child(userID).setValue(restaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    fAuth.signOut();
//                    user.sendEmailVerification();

                    new SweetAlertDialog(sign_up2_restaurant_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Account Created.")
                            .setContentText("Please check your email " +
                                    "\nto verify your account.")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(getApplicationContext(), login_page.class));
                                }
                            })
                            .show();

                }
                else
                {
                    Toast.makeText(sign_up2_restaurant_page.this, "Creation Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void pickImageForValidID() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),VALID_ID);
    }

    private void pickImageForUserPic() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),USER_PIC);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==USER_PIC && resultCode == Activity.RESULT_OK)
        {
            userPicUri = data.getData();

            Picasso.get().load(userPicUri)
                    .into(iv_userPhoto);
        }
        else if (requestCode==VALID_ID && resultCode == Activity.RESULT_OK)
        {
            validIdUri = data.getData();

            Picasso.get().load(validIdUri)
                    .resize(300, 200)
                    .into(iv_validID);
        }
    }

    private void setRef() {

        textView = findViewById(R.id.textView);
        tv_date = findViewById(R.id.tv_date);
        tv_addValidID = findViewById(R.id.tv_addValidID);
        tv_signIn = findViewById(R.id.tv_signIn);

        iv_addPhoto = findViewById(R.id.iv_addPhoto);
        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        iv_validID = findViewById(R.id.iv_validID);

        et_nameOfEst = findViewById(R.id.et_nameOfEst);
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_contactNumber = findViewById(R.id.et_contactNumber);

        btn_signUp = findViewById(R.id.btn_signUp);

        fAuth = FirebaseAuth.getInstance();
    }

    // validate permissions
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }
}