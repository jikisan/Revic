package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Models.Contracts;
import Models.Events;
import Models.Transactions;
import Models.Wallets;

public class view_contract_musician extends AppCompatActivity {

    private TextView tv_ongoing, tv_terminated, tv_done,
            tv_eventName, tv_eventAddress, tv_eventDate, tv_eventTimeStart,
            tv_eventTimeEnd, tv_eventDescription, tv_eventPrice;
    private ImageView iv_eventPhoto, iv_backBtn;
    private Button btn_terminate, btn_rate;

    private List<Events> arrEvents = new ArrayList<>();
    private List<String> arrEventsId = new ArrayList<>();

    private ProgressBar progressBar;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, contractDatabase, walletDatabase, transactionDatabase;

    private String myUserId, eventId, contractId, creatorId, walletId,
            timeCreated, dateCreated;
    private double eventPrice, fundAmount;
    private long dateTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contract_musician);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        contractId = getIntent().getStringExtra("contractId");
        creatorId = getIntent().getStringExtra("creatorId");
        eventId = getIntent().getStringExtra("eventId");

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");
        transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

        setRef();
        generateContractData();
        generateEventData();
        generateWalletData();
        clickListeners();
    }

    private void clickListeners() {

        iv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_contract_musician.this, homepage.class);
                intent.putExtra("pageNumber", "5");
                intent.putExtra("myCategory", "Event Organizer");
                startActivity(intent);
            }
        });

        btn_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refundFund();

            }
        });

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), rate_event_and_user_page.class);
                intent.putExtra("contractId", contractId);
                intent.putExtra("eventId", eventId);
                intent.putExtra("creatorId", creatorId);
                view.getContext().startActivity(intent);

            }
        });
    }


    private void refundFund() {

        double newFundValue = eventPrice + fundAmount;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", creatorId);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(walletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    updateTransactionData(eventPrice);

                }

            }
        });
    }

    private void updateTransactionData(double eventPrice) {

        setUpDate();

        String transactionType = "add";
        String transactionNote = "Refund (Terminated contract)";

        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, eventPrice, creatorId);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateContractData();

            }
        });

    }

    private void updateContractData() {

        String newValue = "terminated";
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("contractStatus", newValue);

        contractDatabase.child(contractId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(view_contract_musician.this, "Contract Terminated", Toast.LENGTH_SHORT).show();

                btn_terminate.setVisibility(View.INVISIBLE);
                tv_done.setVisibility(View.INVISIBLE);
                tv_ongoing.setVisibility(View.INVISIBLE);
                tv_terminated.setVisibility(View.VISIBLE);

            }
        });
    }



    private void generateContractData() {

        contractDatabase.child(contractId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Contracts contracts = snapshot.getValue(Contracts.class);

                    String contractStatus = contracts.getContractStatus();
                    Boolean isRatedByMusician = contracts.getRatedByMusician();
                    Boolean isRatedByCreator = contracts.getRatedByCreator();

                    if(contractStatus.toLowerCase(Locale.ROOT).equals("ongoing"))
                    {
                        tv_ongoing.setVisibility(View.VISIBLE);


                    } else if(contractStatus.toLowerCase(Locale.ROOT).equals("terminated"))
                    {
                        tv_terminated.setVisibility(View.VISIBLE);
                        btn_terminate.setVisibility(View.INVISIBLE);

                    }else if(contractStatus.toLowerCase(Locale.ROOT).equals("done"))
                    {
                        tv_done.setVisibility(View.VISIBLE);
                        btn_terminate.setVisibility(View.INVISIBLE);

                        if(!isRatedByMusician){
                            btn_rate.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateEventData() {

        eventDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Events events = snapshot.getValue(Events.class);

                    String imageUrl = events.getImageUrl();
                    String eventName = events.getEventName();
                    String eventAddress = events.getEventAddress();
                    String eventDate = events.getEventDateSched();
                    String eventStart = events.getTimeStart();
                    String eventEnd = events.getTimeEnd();
                    String eventDescription = events.getEventDescription();
                    eventPrice = events.getEventPrice();

                    Picasso.get().load(imageUrl).fit().centerCrop().into(iv_eventPhoto);
                    tv_eventName.setText(eventName);
                    tv_eventAddress.setText(eventAddress);
                    tv_eventDate.setText(eventDate);
                    tv_eventTimeStart.setText(eventStart);
                    tv_eventTimeEnd.setText(eventEnd);
                    tv_eventDescription.setText(eventDescription);
                    tv_eventPrice.setText("₱ "+eventPrice+" / Event");


                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateWalletData() {

        Query query = walletDatabase.orderByChild("userID").equalTo(creatorId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);

                        walletId = dataSnapshot.getKey().toString();
                        fundAmount = wallets.getFundAmount();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void setRef() {

        progressBar = findViewById(R.id.progressBar);

        tv_ongoing = findViewById(R.id.tv_ongoing);
        tv_terminated = findViewById(R.id.tv_terminated);
        tv_done = findViewById(R.id.tv_done);

        tv_eventName = findViewById(R.id.tv_eventName);
        tv_eventAddress = findViewById(R.id.tv_eventAddress);
        tv_eventDate = findViewById(R.id.tv_eventDate);
        tv_eventTimeStart = findViewById(R.id.tv_eventTimeStart);
        tv_eventTimeEnd = findViewById(R.id.tv_eventTimeEnd);
        tv_eventDescription = findViewById(R.id.tv_eventDescription);
        tv_eventPrice = findViewById(R.id.tv_eventPrice);

        iv_eventPhoto = findViewById(R.id.iv_eventPhoto);
        iv_backBtn = findViewById(R.id.iv_backBtn);

        btn_terminate = findViewById(R.id.btn_terminate);
        btn_rate = findViewById(R.id.btn_rate);

    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

    }
}