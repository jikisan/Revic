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

import Models.Chat;
import Models.Messages;
import Models.Users;
import Objects.TextModifier;

public class chat_activity extends AppCompatActivity {

    private LinearLayout layout1, backBtn;
    private ImageView iv_chatProfilePhoto, sendButton;
    private TextView tv_chatName, tv_category;
    private EditText messageArea;
    private ScrollView scrollView;

    private String userOne, userTwo, myUserID, userID, chatId, senderPhotoUrl, fullName;

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

        chatId = getIntent().getStringExtra("chatId");

        setRef();
        generateUsersData();
        eventListeners();
        clickListeners();
    }

    private void generateUsersData() {


        String[] split = chatId.split("_");
        userOne = split[0];
        userTwo = split[1];

        if(!userOne.equals(myUserID))
        {
            userID = userOne;
        }
        else
        {
            userID = userTwo;
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chat_activity.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void eventListeners() {

        Query query = messageDatabase
                .orderByChild("chatUid")
                .equalTo(chatId);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                String message = messages.getMessage();


                if(messages.getSenderUid().equals(userID))
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

                Chat chat = new Chat(userOne, userTwo, chatId);


                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){

                    chatDatabase.child(chatId).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                addDataToMessageDB(userID, userTwo);

                            }
                        }


                    });

                }
            }
        });
    }

    private void addDataToMessageDB(String senderUid, String receiverUid) {

        String messageText = messageArea.getText().toString();
        Messages messages = new Messages(senderUid, receiverUid, chatId, messageText);

        messageDatabase.push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                messageArea.setText("");
            }
        });

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

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(chat_activity.this);
        textView.setText(message);
        textView.setTextSize(16);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        textView.setLayoutParams(lp);

        TextView textView2 = new TextView(chat_activity.this);
        textView2.setText(message);
        textView2.setTextSize(16);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        lp2.gravity = Gravity.RIGHT;
        lp2.setMargins(16, 16, 16, 16);
        textView2.setLayoutParams(lp2);

        if(type == 1) {
            textView2.setBackgroundResource(R.drawable.rounded_corner1);
            textView2.setTextColor(Color.WHITE);
            layout1.addView(textView2);

        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            textView.setTextColor(Color.BLACK);
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