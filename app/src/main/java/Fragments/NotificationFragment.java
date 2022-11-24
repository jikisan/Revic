package Fragments;

import android.app.Notification;
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
import com.example.revic_capstone.my_wallet_page;
import com.example.revic_capstone.view_contract_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterChatItem;
import Adapters.AdapterNotificationItem;
import Adapters.AdapterTransactionsItem;
import Models.Chat;
import Models.Notifications;
import Models.Transactions;

public class NotificationFragment extends Fragment {

    private List<Notifications> arrNotif = new ArrayList<>();;

    private ProgressBar progressBar;
    private TextView tv_noMessagesText;
    private RecyclerView recyclerView;
    private AdapterNotificationItem adapterNotificationItem;

    private FirebaseUser user;
    private DatabaseReference notificationDatabase;

    private String myUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        notificationDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

        setRef(view);
        generateRecyclerLayout();
        clickListeners();

        return view;
    }

    private void clickListeners() {

        adapterNotificationItem.setOnItemClickListener(new AdapterNotificationItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Notifications notifications = arrNotif.get(position);

                String notifType = notifications.getNotificationType().toLowerCase(Locale.ROOT);

                if(notifType.equals("message"))
                {   String chatId = notifications.getChatId();

                    String[] split = chatId.split("_");
                    String split1 = split[0];
                    String split2 = split[1];
                    String chatUid1 = split1 + "_" + split2;
                    String chatUid2 = split2 + "_" + split1;
                    String userTwo;

                    if(chatUid1.equals(myUserId))
                    {
                        userTwo = chatUid2;
                    }
                    else
                    {
                        userTwo = chatUid1;
                    }

                    Intent intent = new Intent(getActivity(), chat_activity.class);
                    intent.putExtra("chatUid1", chatUid1);
                    intent.putExtra("chatUid2", chatUid2);
                    intent.putExtra("userIdFromSearch", userTwo);
                    intent.putExtra("needNotification", "1");
                    startActivity(intent);
                }
                else if(notifType.equals("hired"))
                {   String userID = notifications.getUserId();
                    String contractId = notifications.getContractId();
                    String eventId = notifications.getEventId();

                    Intent intent = new Intent(getActivity(), view_contract_page.class);
                    intent.putExtra("contractId", contractId);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("employeeId", userID);
                    getContext().startActivity(intent);
                }
                else if(notifType.equals("payment"))
                {
                    Intent intent = new Intent(getContext(), my_wallet_page.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void generateRecyclerLayout() {

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapterNotificationItem = new AdapterNotificationItem(arrNotif);
        recyclerView.setAdapter(adapterNotificationItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = notificationDatabase.orderByChild("userId").equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrNotif.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Notifications notifications = dataSnapshot.getValue(Notifications.class);

                        arrNotif.add(notifications);
                    }
                }

                if(arrNotif.isEmpty())
                {
                    tv_noMessagesText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                Collections.reverse(arrNotif);
                progressBar.setVisibility(View.GONE);
                adapterNotificationItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        tv_noMessagesText = view.findViewById(R.id.tv_noMessagesText);
        recyclerView = view.findViewById(R.id.recyclerView);

    }
}