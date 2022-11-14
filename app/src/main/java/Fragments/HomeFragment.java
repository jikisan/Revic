package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.revic_capstone.R;
import com.example.revic_capstone.profile_page;
import com.example.revic_capstone.profile_user_page;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.AdapterUsersItem;
import Adapters.fragmentAdapsterHomepage;
import Adapters.fragmentAdapter;
import Models.Users;
import Objects.TextModifier;

public class HomeFragment extends Fragment {

    private SearchView sv_search;
    private RecyclerView recyclerView_searches;
    private ImageView iv_bannerPhoto;
    private fragmentAdapsterHomepage adapter;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;

    private ArrayList<Users> arrUsers, arr;

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private String userID;

    private AdapterUsersItem adapterUsersItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");


        setRef(view);
        generateUserData();
        generateTabLayout(view);
        generateRecyclerLayout();
        clickListeners();

        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });

        return view;
    }


    private void generateUserData() {

        TextModifier textModifier = new TextModifier();

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(users != null){

                    String imageUrl = users.getImageUrl();

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_bannerPhoto);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Users users = dataSnapshot.getValue(Users.class);

                        if(users.getUsersId().equals(userID))
                        {
                            continue;
                        }

                        arrUsers.add(users);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error retrieving data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickListeners() {
        iv_bannerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), profile_user_page.class);
                startActivity(intent);
            }
        });

    }

    private void generateTabLayout(View view) {

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.events));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.connected));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.applied));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.rated));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.near_me));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.my_application));

        FragmentManager fragmentManager = getChildFragmentManager();
        adapter = new fragmentAdapsterHomepage(fragmentManager, getLifecycle());
        vp_viewPager2.setSaveEnabled(false);
        vp_viewPager2.setAdapter(adapter);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab_layout.selectTab(tab_layout.getTabAt(position));
            }
        });
    }

    private void setRef(View view) {

        iv_bannerPhoto = view.findViewById(R.id.iv_bannerPhoto);
        sv_search = view.findViewById(R.id.sv_search);
        recyclerView_searches = view.findViewById(R.id.recyclerView_searches);

        tab_layout = view.findViewById(R.id.tab_layout);
        vp_viewPager2 = view.findViewById(R.id.vp_viewPager2);
    }

    private void search(String s) {
        arr = new ArrayList<>();
        for(Users object : arrUsers)
        {
            if (object.getFname().toLowerCase().contains(s.toLowerCase()) || object.getLname().toLowerCase().contains(s.toLowerCase()))
            {
                arr.add(object);
            }

            if(s.isEmpty())
            {
                arr.clear();
            }

            adapterUsersItem = new AdapterUsersItem(arr);
            recyclerView_searches.setAdapter(adapterUsersItem);
        }

        if(adapterUsersItem != null)
        {
            adapterUsersItem.setOnItemClickListener(new AdapterUsersItem.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    String userID = arr.get(position).getUsersId();
                    Intent intent = new Intent(getContext(), profile_page.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent);

                }
            });
        }

    }

    private void generateRecyclerLayout() {
        recyclerView_searches.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_searches.setLayoutManager(linearLayoutManager);

        arrUsers = new ArrayList<>();
    }
}