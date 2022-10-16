package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.chat_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterChatItem;
import Models.Chat;

public class MessagesFragment extends Fragment {

    private List<Chat> arrChat = new ArrayList<>();;

    private ProgressBar progressBar;
    private TextView tv_noMessagesText;
    private RecyclerView recyclerView_chatProfiles;
    private AdapterChatItem adapterChatItem;

    private FirebaseUser user;
    private DatabaseReference chatDatabase, messageDatabase;

    private String userID, userOne, userTwo, chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        setRef(view);
        generateRecyclerLayout();
        clickListeners();

        return view;
    }

    private void clickListeners() {

        adapterChatItem.setOnItemClickListener(new AdapterChatItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                arrChat.get(position);

                Intent intent = new Intent(getActivity(), chat_activity.class);
                intent.putExtra("chatId", chatId);
                intent.putExtra("userIdFromSearch", userTwo);
                startActivity(intent);
            }
        });
    }

    private void generateRecyclerLayout() {

        recyclerView_chatProfiles.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_chatProfiles.setLayoutManager(linearLayoutManager);

        arrChat = new ArrayList<>();
        adapterChatItem = new AdapterChatItem(arrChat);
        recyclerView_chatProfiles.setAdapter(adapterChatItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        chatId = dataSnapshot.getKey().toString();
                        userOne = chat.getUserIdOne();
                        userTwo = chat.getUserIdTwo();

                        if(userOne.equals(userID) || userTwo.equals(userID))
                        {
                            arrChat.add(chat);
                        }
                    }
                }


                if (arrChat.isEmpty()) {
                    recyclerView_chatProfiles.setVisibility(View.GONE);
                    tv_noMessagesText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    recyclerView_chatProfiles.setVisibility(View.VISIBLE);
                    tv_noMessagesText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }

                adapterChatItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_chatProfiles = view.findViewById(R.id.recyclerView_chatProfiles);

        tv_noMessagesText = view.findViewById(R.id.tv_noMessagesText);
    }
}