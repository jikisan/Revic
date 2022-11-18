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

public class ProfileEventsFragment extends Fragment {

    private List<Events> arrEvents = new ArrayList<>();
    private List<String> arrEventsId = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView recyclerView_events;
    private AdapterEventsItem adapterEventsItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, userDatabase;

    private String myUserId, eventId, myPosts, userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_events, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        myPosts = getActivity().getIntent().getStringExtra("myPosts");
        userID = getActivity().getIntent().getStringExtra("userID");

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
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
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Events events = dataSnapshot.getValue(Events.class);
                        eventId = dataSnapshot.getKey().toString();
                        String eventUsersId = events.getUserID();

                        if(myPosts != null && myPosts.equals("1"))
                        {
                            if(myUserId.equals(eventUsersId))
                            {
                                arrEvents.add(events);
                                arrEventsId.add(eventId);
                            }

                        }else if(myPosts != null && myPosts.equals("2"))
                        {
                            if(userID.equals(eventUsersId))
                            {
                                arrEvents.add(events);
                                arrEventsId.add(eventId);
                            }

                        }else{
                            arrEvents.add(events);
                            arrEventsId.add(eventId);
                        }


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