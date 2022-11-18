package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.revic_capstone.R;
import com.example.revic_capstone.profile_page;
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

import Adapters.AdapterMostConnectedItem;
import Models.Users;


public class MostRatedMusicians extends Fragment {

    private List<Users> arrUsers = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView recyclerView_users;
    private AdapterMostConnectedItem adapterMostConnectedItem;

    private FirebaseUser user;
    private DatabaseReference userDatabase;

    private String myUserId, userID, category, myCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_rated_musicians, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef(view);
        generateRecyclerLayout();
        clickListeners();

        return view;
    }

    private void clickListeners() {

        adapterMostConnectedItem.setOnItemClickListener(new AdapterMostConnectedItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String userID = arrUsers.get(position).getUsersId();
                Intent intent = new Intent(getContext(), profile_page.class);
                intent.putExtra("userID", userID);
                intent.putExtra("myPosts", "2");
                startActivity(intent);
            }
        });
    }

    private void generateRecyclerLayout() {

        recyclerView_users.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView_users.setLayoutManager(gridLayoutManager);

        arrUsers = new ArrayList<>();
        adapterMostConnectedItem = new AdapterMostConnectedItem(arrUsers);
        recyclerView_users.setAdapter(adapterMostConnectedItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        generateMyCategory();

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrUsers.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Users users = dataSnapshot.getValue(Users.class);
                        category = users.getCategory();


                        if (myCategory.equals("Musician") ) {

                            if (category.equals("Musician") || users.getUsersId().equals(myUserId)) {
                                continue;
                            }

                            arrUsers.add(users);
                        }else if (myCategory.equals("Event Organizer") || myCategory.equals("Restaurant") )
                        {
                            if (category.equals("Event Organizer") || category.equals("Restaurant")  ||users.getUsersId().equals(myUserId)) {
                                continue;
                            }

                            arrUsers.add(users);
                        }


                    }
                }


                Collections.sort(arrUsers, new Comparator<Users>() {
                    @Override
                    public int compare(Users users, Users t1) {
                        return Long.compare(t1.getRating(), users.getRating());
                    }
                });

                progressBar.setVisibility(View.GONE);
                adapterMostConnectedItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateMyCategory() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    myCategory = users.getCategory();

                }
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