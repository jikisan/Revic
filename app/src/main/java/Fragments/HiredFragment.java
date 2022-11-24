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

import Adapters.AdapterHiredItem;
import Adapters.AdapterMyApplicationsItem;
import Models.Applications;
import Models.Contracts;
import Models.Events;


public class HiredFragment extends Fragment {

    private List<Contracts> arrContracts = new ArrayList<>();
    private List<String> arrContractId = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView tv_emptyText;
    private RecyclerView recyclerView_users;
    private AdapterHiredItem adapterHiredItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase, contractDatabase;

    private String myUserId ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hired, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");

        setRef(view);
        generateRecyclerLayout();

        return view;
    }

    private void generateRecyclerLayout() {


        recyclerView_users.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_users.setLayoutManager(linearLayoutManager);

        adapterHiredItem = new AdapterHiredItem(arrContracts, arrContractId);
        recyclerView_users.setAdapter(adapterHiredItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = contractDatabase.orderByChild("contractStatus")
                .equalTo("ongoing");

        contractDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrContracts.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Contracts contracts = dataSnapshot.getValue(Contracts.class);

                        String creatorUserId = contracts.getCreatorUserId();
                        String contractId = dataSnapshot.getKey().toString();

                        if(creatorUserId.equals(myUserId))
                        {
                            arrContracts.add(contracts);
                            arrContractId.add(contractId);
                        }

                    }
                }

                if(arrContracts.isEmpty())
                {
                    recyclerView_users.setVisibility(View.GONE);
                    tv_emptyText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    adapterHiredItem.notifyDataSetChanged();
                }else {
                    recyclerView_users.setVisibility(View.VISIBLE);
                    tv_emptyText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    adapterHiredItem.notifyDataSetChanged();

                }


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