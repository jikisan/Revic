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
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterMostAppliedItem;
import Adapters.AdapterMostRatedItem;
import Models.Events;
import Models.MostRated;

public class MostAppliedFragment extends Fragment {

    private List<Events> arrEvents = new ArrayList<>();
    private List<MostRated> arrMostRated = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView recyclerView_users;
    private AdapterMostAppliedItem adapterMostAppliedItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase;

    private String myUserId, userID, eventId, eventName, eventsUrl;
    private int applicants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_applied, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        setRef(view);
        generateRecyclerLayout();

        return view;
    }

    private void generateRecyclerLayout() {

        recyclerView_users.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_users.setLayoutManager(linearLayoutManager);

        arrEvents = new ArrayList<>();
        adapterMostAppliedItem = new AdapterMostAppliedItem(arrMostRated);
        recyclerView_users.setAdapter(adapterMostAppliedItem);

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
                        eventName = events.getEventName();
                        applicants = events.getApplicants();
                        eventsUrl = events.getImageUrl();
                        int ratingsCount = 0;

                        MostRated mostRated = new MostRated(eventId, eventName, eventsUrl, ratingsCount, applicants);
                        arrMostRated.add(mostRated);
                    }
                }


                Collections.sort(arrMostRated, new Comparator<MostRated>() {
                    @Override
                    public int compare(MostRated t1, MostRated t2) {
                        return Integer.compare(t1.getApplicants(), t2.getApplicants());
                    }
                });
//
                Collections.reverse(arrMostRated);

                progressBar.setVisibility(View.GONE);

                adapterMostAppliedItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView_users = view.findViewById(R.id.recyclerView_users);
    }
}