package Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.revic_capstone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Models.Posts;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class PostPhotosAndVidFragment extends Fragment {

    private ExtendedFloatingActionButton btn_postFloating;
    private FloatingActionButton btn_addPhotos, btn_addVideos;
    private Button btn_postBtn;
    private EditText et_postDescription;
    private ImageView iv_post;
    private TextView tv_addVidText, tv_addPhotoText;

    private Uri fileUri;

    public static final int PIC_ID = 3;
    public static final int VID_ID = 2;

    private Boolean isAllFabsVisible, isVidAttached, isPhotoAttached;
    private String fileType, userID, timeCreated, dateCreated, fileName;
    private long dateTimeInMillis;

    private FirebaseUser user;
    private DatabaseReference postDatabase;
    private StorageReference postStorage;
    private StorageReference fileReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        postStorage = FirebaseStorage.getInstance().getReference("Posts").child(userID);
        postDatabase = FirebaseDatabase.getInstance().getReference("Posts");

        setRef(view);
        buttonActivity();
        clickListeners();

        return view;
    }



    private void clickListeners() {

        btn_postFloating.setOnClickListener(new View.OnClickListener() {
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
                    btn_postFloating.extend();

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
                    btn_postFloating.shrink();

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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),PIC_ID);
            }
        });

        btn_addVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), VID_ID);
            }
        });

        btn_postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation();
            }
        });

    }

    private void inputValidation() {

        if(fileUri == null){
            Toast.makeText(getContext(), "Please upload photo or video.", Toast.LENGTH_SHORT).show();
        }
        else{
            new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.revic_logo2)
                    .setTitle("Almost there!")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(true)
                    .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            addPost();

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

    private void addPost() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading post...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String postMessage = et_postDescription.getText().toString();
        int ratings = 0;
        fileName = fileUri.getLastPathSegment();
        fileReference = postStorage.child(fileUri.getLastPathSegment());

        if(isPhotoAttached = false)
        {
            fileType = "video";
        }
        else if(isVidAttached = false)
        {
            fileType = "photos";
        }

        setUpDate();

        fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String fileUrl = uri.toString();

                        Posts posts = new Posts(postMessage, fileUrl, fileName, fileType, dateTimeInMillis,
                                timeCreated, dateCreated, ratings, userID);

                        postDatabase.push().setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();

                                    SweetAlertDialog pdialog;
                                    pdialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                    pdialog.setTitleText("Post Successfully Added!");
                                    pdialog.setContentText("Post has been \nsuccessfully added for Admin review");
                                    pdialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            clearText();
                                            pdialog.dismiss();

                                        }
                                    });
                                    pdialog.show();

                                }
                            }
                        });

                    }
                });

            }
        });

    }

    private void clearText() {

        et_postDescription.setText("");
        iv_post.setImageDrawable(null);
        fileUri = null;
        btn_postBtn.setVisibility(View.GONE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PIC_ID && resultCode == RESULT_OK){
            fileUri = data.getData();

            Picasso.get().load(fileUri)
                    .fit()
                    .centerCrop()
                    .into(iv_post);

            btn_postBtn.setVisibility(View.VISIBLE);
            isVidAttached = false;
            buttonActivity();

        }
        else  if (requestCode==VID_ID && resultCode == RESULT_OK){

            fileUri = data.getData();

            Glide.with(getContext())
                    .load(fileUri)
                    .fitCenter()
                    .centerCrop()
                    .into(iv_post);

            btn_postBtn.setVisibility(View.VISIBLE);
            isPhotoAttached = false;
            buttonActivity();
        }

    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
//        Date dateInMillis = new Date(dateTimeInMillis);
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

    }

    private void buttonActivity() {
        btn_addPhotos.hide();
        btn_addVideos.hide();
        tv_addVidText.setVisibility(View.GONE);
        tv_addPhotoText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        btn_postFloating.shrink();
    }

    private void setRef(View view) {

        btn_postFloating = view.findViewById(R.id.btn_postFloating);
        btn_postBtn = view.findViewById(R.id.btn_postBtn);
        btn_addPhotos = view.findViewById(R.id.btn_addPhotos);
        btn_addVideos = view.findViewById(R.id.btn_addVideos);

        et_postDescription = view.findViewById(R.id.et_postDescription);

        iv_post = view.findViewById(R.id.iv_post);

        tv_addVidText = view.findViewById(R.id.tv_addVidText);
        tv_addPhotoText = view.findViewById(R.id.tv_addPhotoText);

    }
}