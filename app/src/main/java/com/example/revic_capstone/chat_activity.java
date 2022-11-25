package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Models.Chat;
import Models.Messages;
import Models.Notifications;
import Models.Users;
import Objects.TextModifier;

public class chat_activity extends AppCompatActivity {

    private LinearLayout layout1, backBtn;
    private ImageView iv_chatProfilePhoto, sendButton;
    private TextView tv_chatName, tv_category;
    private EditText messageArea;
    private ScrollView scrollView;

    private String userOne, userTwo, myUserID, userID, chatUid1,
            chatUid2, senderPhotoUrl, fullName, sender, receiver,
            needNotification, myFullName;
    private String timeCreated, dateCreated;
    private long dateTimeInMillis;

    private FirebaseUser user;
    private DatabaseReference messageDatabase, chatDatabase, userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();

        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");


        chatUid1 = getIntent().getStringExtra("chatUid1");
        chatUid2 = getIntent().getStringExtra("chatUid2");
        needNotification = getIntent().getStringExtra("needNotification");

        setRef();
        generateMyData();
        generateUsersData();

        clickListeners();
    }

    private void generateMyData() {

        TextModifier textModifier = new TextModifier();

        userDatabase.child(myUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(snapshot.exists()){

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();

                    myFullName = fname + " " + lname;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateUsersData() {


        String[] split = chatUid1.split("_");
        userOne = split[0];
        userTwo = split[1];

        if(userOne.equals(myUserID))
        {
            sender = userOne;
            receiver = userTwo;
            userID = userTwo;
        }
        else
        {
            sender = userTwo;
            receiver = userOne;
            userID = userOne;
        }

        TextModifier textModifier = new TextModifier();

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(users != null){

                    senderPhotoUrl = users.getImageUrl();

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();

                    fullName = fname + " " + lname;
                    String category = users.getCategory();

                    tv_chatName.setText(fullName);
                    tv_category.setText(category);

                    Picasso.get()
                            .load(senderPhotoUrl)
                            .into(iv_chatProfilePhoto);

                    eventListeners();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chat_activity.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void clickListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addChatUid1();
            }
        });
    }

    private void addChatUid1() {

        Chat chat = new Chat(userOne, userTwo, chatUid1);

        String messageText = messageArea.getText().toString();

        if(!messageText.equals("")){

            chatDatabase.child(chatUid1).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {

                        addChatUid2();
                    }
                }


            });

        }
    }

    private void addChatUid2() {

        Chat chat = new Chat(userTwo, userOne, chatUid2);

        chatDatabase.child(chatUid2).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                addDataToMessageDB(userOne, userTwo);

            }
        });


    }

    private void addDataToMessageDB(String userOne, String userTwo) {

        String messageText = messageArea.getText().toString();
        Messages messages = new Messages(sender, receiver, chatUid1, messageText);

        messageDatabase.push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    addSecondMessage(userOne, userTwo, messageText);

                }

            }
        });

    }

    private void addSecondMessage(String userOne, String userTwo, String messageText) {

        Messages messages = new Messages(sender, receiver, chatUid2, messageText);

        messageDatabase.push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(needNotification.equals("1"))
                {
                    generateNotification();
                }
                else
                {
                    messageArea.setText("");
                    needNotification = "2";

                }

            }
        });

    }

    private void generateNotification() {

        setUpDate();

        DatabaseReference notificationDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

        String notificationType = "message";
        String notificationMessage = myFullName + " sent you a message";
        String contractId = "";
        String userId = userID;
        String chatId = chatUid1;
        String eventId = "";

        Notifications notifications = new Notifications(dateTimeInMillis, dateCreated, timeCreated, notificationType,
                notificationMessage, contractId, userId, chatId, eventId);

        notificationDatabase.push().setValue(notifications).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                messageArea.setText("");
                needNotification = "2";
            }
        });

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


    private void setRef() {

        iv_chatProfilePhoto = findViewById(R.id.iv_chatProfilePhoto);
        sendButton = findViewById(R.id.sendButton);

        tv_chatName = findViewById(R.id.tv_chatName);
        tv_category = findViewById(R.id.tv_category);

        messageArea = findViewById(R.id.messageArea);

        scrollView = findViewById(R.id.scrollView);

        layout1 = findViewById(R.id.layout1);
        backBtn = findViewById(R.id.backBtn);
    }

    private void eventListeners() {

        Query query = messageDatabase
                .orderByChild("chatUid")
                .equalTo(chatUid2);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                String message = messages.getMessage();


                if(messages.getSenderUid().equals(myUserID))
                {
                    addMessageBox( message, 1);
                }
                else
                {
                    addMessageBox( message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public void addMessageBox(String message, int type){

        TextView tv = new TextView(chat_activity.this);
        tv.setTextSize(12);
        tv.setText(fullName+":");
        LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lptv.weight = 1.0f;
        lptv.setMargins(16, 16, 16, 0);
        tv.setLayoutParams(lptv);

        TextView textView = new TextView(chat_activity.this);
        textView.setText(message);
        textView.setTextSize(16);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 0, 16, 16);
        textView.setLayoutParams(lp);



        TextView tv2 = new TextView(chat_activity.this);
        tv2.setTextSize(12);
        tv2.setText("Me: ");
        LinearLayout.LayoutParams lptv2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lptv2.weight = 1.0f;
        lptv2.gravity = Gravity.RIGHT;
        lptv2.setMargins(16, 16, 16, 0);
        tv2.setLayoutParams(lptv2);

        TextView textView2 = new TextView(chat_activity.this);
        textView2.setText(message);
        textView2.setTextSize(16);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        lp2.gravity = Gravity.RIGHT;
        lp2.setMargins(16, 0, 16, 16);
        textView2.setLayoutParams(lp2);



        if(type == 1) {
            textView2.setBackgroundResource(R.drawable.rounded_corner1);
            textView2.setTextColor(Color.WHITE);
            layout1.addView(tv2);
            layout1.addView(textView2);

        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            textView.setTextColor(Color.BLACK);
            layout1.addView(tv);
            layout1.addView(textView);

        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });


    }

}