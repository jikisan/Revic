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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterMostRatedItem;
import Adapters.AdapterMyApplicationsItem;
import Models.Applications;
import Models.Events;
import Models.MostRated;

public class MyApplicationFragment extends Fragment {

    private List<Events> arrEvents = new ArrayList<>();
    private List<String> arrEventsId = new ArrayList<>();
    private List<String> arrStatus = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView tv_emptyText;
    private RecyclerView recyclerView_users;
    private AdapterMyApplicationsItem adapterMyApplicationsItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, applicationsDatabase;

    private String myUserId ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_application, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        applicationsDatabase = FirebaseDatabase.getInstance().getReference("Applications");

        setRef(view);
        generateRecyclerLayout();

        return view;
    }

    private void generateRecyclerLayout() {
        recyclerView_users.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_users.setLayoutManager(linearLayoutManager);

        adapterMyApplicationsItem = new AdapterMyApplicationsItem(arrEvents, arrEventsId, arrStatus);
        recyclerView_users.setAdapter(adapterMyApplicationsItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = applicationsDatabase.orderByChild("applicantUsersId")
                .equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Applications applications = dataSnapshot.getValue(Applications.class);

                        String eventId = applications.getEventId();
                        String status = applications.getStatus();

                        arrStatus.add(status);
                        arrEventsId.add(eventId);
                        generateEvent(eventId);
                    }
                }

                if(arrEventsId.isEmpty())
                {
                    recyclerView_users.setVisibility(View.GONE);
                    tv_emptyText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else {
                    recyclerView_users.setVisibility(View.VISIBLE);
                    tv_emptyText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateEvent(String eventId) {

        eventDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                        Events events = snapshot.getValue(Events.class);
                        arrEvents.add(events);
                }

                progressBar.setVisibility(View.GONE);
                adapterMyApplicationsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        tv_emptyText = view.findViewById(R.id.tv_emptyText);

        recyclerView_users = view.findViewById(R.id.recyclerView_users);
    }
}