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

import Adapters.AdapterApplicationsItem;
import Models.Applications;
import Models.Events;
import Models.Users;
import Objects.TextModifier;

public class ApplicantsFragment extends Fragment {

    private List<Applications> arrApplications = new ArrayList<>();
    private List<String> arrApplicationId = new ArrayList<>();

    private TextView tv_noPhotos;
    private ProgressBar progressBar;
    private RecyclerView recyclerView_events;
    private AdapterApplicationsItem adapterApplicationsItem;
    private TextModifier textModifier = new TextModifier();

    private FirebaseUser user;
    private DatabaseReference applicationDatabase, userDatabase, eventDatabase;

    private String myUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_applicants, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        applicationDatabase = FirebaseDatabase.getInstance().getReference("Applications");
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

        adapterApplicationsItem = new AdapterApplicationsItem(arrApplications, arrApplicationId);
        recyclerView_events.setAdapter(adapterApplicationsItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = applicationDatabase
                .orderByChild("status")
                .equalTo("pending");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        arrApplicationId.clear();
                        arrApplications.clear();

                        Applications applications = dataSnapshot.getValue(Applications.class);

                        String creatorUserId = applications.getCreatorUserId();
                        String applicationId = dataSnapshot.getKey().toString();

                        if(creatorUserId.equals(myUserId))
                        {
                            arrApplicationId.add(applicationId);
                            arrApplications.add(applications);

                        }
                    }
                }


                if(arrApplications.isEmpty())
                {
                    recyclerView_events.setVisibility(View.GONE);
                    tv_noPhotos.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                 }else
                {
                    recyclerView_events.setVisibility(View.VISIBLE);
                    tv_noPhotos.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }

                adapterApplicationsItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        tv_noPhotos = view.findViewById(R.id.tv_noPhotos);
        recyclerView_events = view.findViewById(R.id.recyclerView_events);
    }
}