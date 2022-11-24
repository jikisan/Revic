package Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.revic_capstone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.Events;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PostEventsFragment extends Fragment {

    private Button btn_post;
    private ImageView iv_eventBannerPhoto;
    private TextView tv_uploadPhoto, tv_startTime, tv_endTime, tv_address, tv_back, tv_dateSched;
    private EditText et_projectName, et_specialInstruction, et_eventPrice;

    private Uri imageUri;
    private Geocoder geocoder;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int EVENT_PIC = 3;


    private FirebaseUser user;
    private DatabaseReference eventDatabase;
    private StorageReference eventStorage;
    private StorageTask addTask;
    private String userID, latLng, latString, longString, timeCreated, dateCreated;
    private long dateTimeInMillis;
    private int hour, minute;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_events, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        eventStorage = FirebaseStorage.getInstance().getReference("Events").child(userID);
        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        setRef(view);
        clickListeners();
        return view;
    }

    private void clickListeners() {

        tv_uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                PickImage();
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // placePicker();


                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getContext());

                //Start Activity result
                startActivityForResult(intent, 100);

            }
        });

        tv_dateSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        month = month+1;
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
                        Date dateOfWeek = new Date(year, month, day-1);
                        String dayOfWeek = simpledateformat.format(dateOfWeek);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
                        calendar.set(year, month, day);
                        dateCreated = sdf.format(calendar.getTime());

                        tv_dateSched.setText(dateCreated  + "-" + dayOfWeek);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 1*24*60*60*1000);
                datePickerDialog.show();

            }
        });

        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        boolean isPM = (hour >= 12);
                        tv_startTime.setText(String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM"));


                    }
                };

                int style = TimePickerDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        boolean isPM = (hour >= 12);
                        tv_endTime.setText(String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM"));

                    }
                };

                int style = TimePickerDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputValidation();
            }
        });
    }

    private void inputValidation() {

        String eventName = et_projectName.getText().toString();
        String eventAddress = tv_address.getText().toString();
        String time_start = tv_startTime.getText().toString();
        String time_end = tv_endTime.getText().toString();
        String eventDescription = et_specialInstruction.getText().toString();

        if(imageUri == null){
            Toast.makeText(getContext(), "Event photo is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(eventName)){
            Toast.makeText(getContext(), "Event Name is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(eventAddress)){
            Toast.makeText(getContext(), "Event address is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(time_start)){
            Toast.makeText(getContext(), "Starting time is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(time_end)){
            Toast.makeText(getContext(), "End time is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(eventDescription)){
            Toast.makeText(getContext(), "Event description is required", Toast.LENGTH_SHORT).show();
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


                            addEvent();

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

    private void addEvent() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading event...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference fileReference = eventStorage.child(imageUri.getLastPathSegment());

        setUpDate();

        String eventName = et_projectName.getText().toString();
        String eventAddress = tv_address.getText().toString();
        String eventDateSched = tv_dateSched.getText().toString();
        String time_start = tv_startTime.getText().toString();
        String time_end = tv_endTime.getText().toString();
        String eventDescription = et_specialInstruction.getText().toString();
        String imageName = imageUri.getLastPathSegment();
        String eventPriceInString = et_eventPrice.getText().toString();
        double eventPriceInDouble = Double.parseDouble(eventPriceInString);
        int rating = 0;
        int applicants = 0;

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imageUrl = uri.toString();

                        Events events = new Events(imageName, imageUrl, eventName, eventAddress, eventDateSched, time_start, time_end, eventDescription,
                                dateTimeInMillis, timeCreated, dateCreated, rating, latString, longString, applicants, userID, eventPriceInDouble);

                        eventDatabase.push().setValue(events).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    progressDialog.dismiss();

                                    SweetAlertDialog pdialog;
                                    pdialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                    pdialog.setTitleText("Event Successfully Added!");
                                    pdialog.setContentText("Event has been \nsuccessfully posted");
                                    pdialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            clearText();
                                            pdialog.dismiss();

                                        }
                                    });
                                    pdialog.show();


                                }
                                else{

                                }
                            }
                        });
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearText() {

        et_eventPrice.setText("");
        iv_eventBannerPhoto.setImageDrawable(null);
        imageUri = null;
        et_projectName.setText("");
        tv_address.setText("");
        tv_startTime.setText("");
        tv_endTime.setText("");
        et_specialInstruction.setText("");

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==EVENT_PIC && resultCode == RESULT_OK)
        {
            imageUri = data.getData();

            Picasso.get().load(imageUri)
                    .fit()
                    .centerCrop()
                    .into(iv_eventBannerPhoto);
        }
        else if(requestCode == 100 && resultCode == RESULT_OK){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            try {
                address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                latString = String.valueOf(address.get(0).getLatitude());
                longString = String.valueOf(address.get(0).getLongitude());

                latLng = latString + "," + longString;
                String addressText =  place.getAddress().toString();

                tv_address.setText(addressText);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void setRef(View view) {

        btn_post = view.findViewById(R.id.btn_post);

        iv_eventBannerPhoto = view.findViewById(R.id.iv_eventBannerPhoto);

        et_projectName = view.findViewById(R.id.et_projectName);
        et_specialInstruction = view.findViewById(R.id.et_specialInstruction);
        et_eventPrice = view.findViewById(R.id.et_eventPrice);

        tv_startTime = view.findViewById(R.id.tv_startTime);
        tv_address = view.findViewById(R.id.et_address);
        tv_uploadPhoto = view.findViewById(R.id.tv_uploadPhoto);
        tv_endTime = view.findViewById(R.id.tv_endTime);
        tv_back = view.findViewById(R.id.tv_back);
        tv_dateSched = view.findViewById(R.id.tv_dateSched);

        Places.initialize(getContext(), getString(R.string.API_KEY));
        //Set edittext no focusable
        tv_address.setFocusable(false);

    }

    private void PickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), EVENT_PIC);

    }
}