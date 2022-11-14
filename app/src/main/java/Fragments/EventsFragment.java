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
import android.widget.Toast;

import com.example.revic_capstone.R;
import com.example.revic_capstone.view_event_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapters.AdapterChatItem;
import Adapters.AdapterEventsItem;
import Models.Chat;
import Models.Events;

public class EventsFragment extends Fragment {

    private List<Events> arrEvents = new ArrayList<>();
    private List<String> arrEventsId = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView recyclerView_events;
    private AdapterEventsItem adapterEventsItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase;

    private String userID, eventId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        setRef(view);
        generateRecyclerLayout();

//        adapterEventsItem.setOnItemClickListener(new AdapterEventsItem.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//
//                Intent intent = new Intent(getContext(), view_event_page.class);
//                intent.putExtra("eventId", arrEventsId.get(position));
//                getContext().startActivity(intent);
//
//            }
//        });

        return view;
    }

    private void generateRecyclerLayout() {

        recyclerView_events.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_events.setLayoutManager(linearLayoutManager);

        arrEvents = new ArrayList<>();
        adapterEventsItem = new AdapterEventsItem(arrEvents, arrEventsId);
        recyclerView_events.setAdapter(adapterEventsItem);

        getViewHolderValues();


    }

    private void getViewHolderValues() {

        eventDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Events events = dataSnapshot.getValue(Events.class);
                        eventId = dataSnapshot.getKey().toString();

                        arrEvents.add(events);
                        arrEventsId.add(eventId);
                    }
                }

                Collections.reverse(arrEvents);
                Collections.reverse(arrEventsId);

                progressBar.setVisibility(View.GONE);
                adapterEventsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_events = view.findViewById(R.id.recyclerView_events);

    }
}