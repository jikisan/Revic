package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class edit_profile_page extends AppCompatActivity {

    private ImageView iv_addPhoto, iv_userPhoto;
    private EditText et_firstName, et_lastName, et_contactNumber;
    private TextView tv_date, textView;
    private RadioGroup radioGroup;
    private RadioButton rb_m, rb_f;
    private Button btn_update;

    private FirebaseAuth fAuth;
    private DatabaseReference userDatabase;
    private StorageReference userStorage;
    private FirebaseUser user;

    public static final int USER_PIC = 3;

    private String myUserId, imageUrl, imageName, fname, lname, contactNum, DOB, gender, newGender, category;
    private Uri userPicUri;
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userStorage = FirebaseStorage.getInstance().getReference("Users");

        category = getIntent().getStringExtra("myCategory");

        setRef();
        generateUsersData();
        clickListener();
    }

    private void generateUsersData() {
        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Users users = snapshot.getValue(Users.class);

                    imageUrl = users.getImageUrl();
                    fname = users.getFname();
                    lname = users.getLname();
                    contactNum = users.getContactNum();
                    DOB = users.getDOB();
                    gender = users.getGender();

                    Picasso.get().load(imageUrl).into(iv_userPhoto);
                    et_firstName.setText(fname);
                    et_lastName.setText(lname);
                    et_contactNumber.setText(contactNum);
                    tv_date.setText(DOB);

                    if(gender != null)
                    {
                        if(gender.equals("Male"))
                        {
                            radioGroup.check(R.id.rb_m);

                        }else if(gender.equals("Female"))
                        {

                            radioGroup.check(R.id.rb_f);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListener() {

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImage();
            }
        });

        rb_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.rb_m);
                newGender = "Male";
            }
        });

        rb_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.rb_f);
                newGender = "Female";
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
                        edit_profile_page.this, new DatePickerDialog.OnDateSetListener() {
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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(userPicUri == null)
                {
                    saveWithOutImage();

                }else

                {
                    saveWithImage();
                }

            }
        });


    }

    private void saveWithImage() {

        String newImageName = userPicUri.getLastPathSegment();
        StorageReference userPicRef = userStorage.child(myUserId).child(newImageName);

        userPicRef.putFile(userPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                userPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String newImageUrl = uri.toString();
                        String newFname = et_firstName.getText().toString();
                        String newLname = et_lastName.getText().toString();
                        String newContactNum = et_contactNumber.getText().toString();
                        String newDOB = tv_date.getText().toString();

                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("imageUrl", newImageUrl);
                        hashMap.put("imageName", newImageName);
                        hashMap.put("fname", newFname);
                        hashMap.put("lname", newLname);
                        hashMap.put("contactNum", newContactNum);
                        hashMap.put("DOB", newDOB);
                        hashMap.put("gender", newGender);

                        userDatabase.child(myUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    SweetAlertDialog sDialog;
                                    sDialog =  new SweetAlertDialog(edit_profile_page.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sDialog.setTitleText("SUCCESS!");
                                    sDialog.setCancelable(false);
                                    sDialog.setContentText("Profile Updated!.");
                                    sDialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    Intent intent = new Intent(edit_profile_page.this, homepage.class);
                                                    intent.putExtra("pageNumber", "5");
                                                    intent.putExtra("myCategory", category);
                                                    intent.putExtra("myPosts", "1");
                                                    startActivity(intent);
                                                }
                                            });
                                    sDialog.show();
                                }

                            }
                        });

                    }
                });
            }
        });
    }

    private void saveWithOutImage() {

        String newFname = et_firstName.getText().toString();
        String newLname = et_lastName.getText().toString();
        String newContactNum = et_contactNumber.getText().toString();
        String newDOB = tv_date.getText().toString();


        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("fname", newFname);
        hashMap.put("lname", newLname);
        hashMap.put("contactNum", newContactNum);
        hashMap.put("DOB", newDOB);
        hashMap.put("gender", newGender);

        userDatabase.child(myUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    new SweetAlertDialog(edit_profile_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("SUCCESS!")
                            .setContentText("Profile Updated!.")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    Intent intent = new Intent(edit_profile_page.this, homepage.class);
                                    intent.putExtra("pageNumber", "5");
                                    intent.putExtra("myCategory", category);
                                    intent.putExtra("myPosts", "1");
                                    startActivity(intent);
                                }
                            })
                            .show();
                }

            }
        });
    }

    private void setRef() {

        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        iv_addPhoto = findViewById(R.id.iv_addPhoto);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_contactNumber = findViewById(R.id.et_contactNumber);

        rb_m = findViewById(R.id.rb_m);
        rb_f = findViewById(R.id.rb_f);
        radioGroup = findViewById(R.id.radioGroup);

        tv_date = findViewById(R.id.tv_date);
        textView = findViewById(R.id.textView);

        btn_update = findViewById(R.id.btn_update);

        fAuth = FirebaseAuth.getInstance();
    }

    private void PickImage() {

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
    }
}