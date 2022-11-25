package Fragments;

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

import Adapters.AdapterEventsItem;
import Models.Events;


public class EventsFragmentInMyProfile extends Fragment {

    private List<Events> arrEvents = new ArrayList<>();
    private List<String> arrEventsId = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView tv_errorMessage;
    private RecyclerView recyclerView_events;
    private AdapterEventsItem adapterEventsItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase;

    private String myUserId, eventId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_in_my_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        setRef(view);
        generateRecyclerLayout();


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
                    arrEvents.clear();
                    arrEventsId.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Events events = dataSnapshot.getValue(Events.class);
                        String creatorId = events.getUserID();
                        eventId = dataSnapshot.getKey().toString();

                        if(creatorId.equals(myUserId))
                        {
                            arrEvents.add(events);
                            arrEventsId.add(eventId);
                        }

                    }
                }

                if(arrEvents.isEmpty())
                {
                    recyclerView_events.setVisibility(View.GONE);
                    tv_errorMessage.setVisibility(View.VISIBLE);
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
        tv_errorMessage = view.findViewById(R.id.tv_errorMessage);

        recyclerView_events = view.findViewById(R.id.recyclerView_events);

    }
}