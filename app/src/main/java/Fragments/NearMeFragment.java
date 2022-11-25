package Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.revic_capstone.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterMostAppliedItem;
import Adapters.AdapterNearMeItem;
import Models.Events;
import Models.MostRated;

public class NearMeFragment extends Fragment {

    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private List<Events> arrEvents = new ArrayList<>();
    private List<MostRated> arrMostRated = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView recyclerView_users;
    private AdapterNearMeItem adapterNearMeItem;

    private FirebaseUser user;
    private DatabaseReference eventDatabase;

    private String myUserId, userID, eventId, eventName, eventsUrl;
    private int applicants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near_me, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        setRef(view);
        validatePermission();

        return view;
    }

    private void validatePermission() {

        // check condition
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            getCurrentLocation();
        } else {
            // When permission is not granted
            // Call method

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(
                        @NonNull Task<Location> task) {

                    // Initialize location
                    Location location = task.getResult();                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        myLatDouble = location.getLatitude();
                        myLongDouble = location.getLongitude();
                        generateRecyclerLayout();


                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void
                            onLocationResult(LocationResult locationResult) {
                                // Initialize
                                // location
                                Location location1 = locationResult.getLastLocation();
                                myLatDouble = location1.getLatitude();
                                myLongDouble = location1.getLongitude();
                                generateRecyclerLayout();


                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void generateRecyclerLayout() {

        recyclerView_users.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_users.setLayoutManager(linearLayoutManager);

        arrEvents = new ArrayList<>();
        adapterNearMeItem = new AdapterNearMeItem(arrMostRated, arrEvents);
        recyclerView_users.setAdapter(adapterNearMeItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {


        eventDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrMostRated.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Events events = dataSnapshot.getValue(Events.class);

                        eventId = dataSnapshot.getKey().toString();
                        eventName = events.getEventName();
                        applicants = events.getApplicants();
                        eventsUrl = events.getImageUrl();
                        int ratingsCount = 0;
                        double latDouble = Double.parseDouble(events.getLatitude());
                        double longDouble = Double.parseDouble(events.getLongitude());

                        LatLng location = new LatLng(latDouble, longDouble);
                        distance = generateDistance(location);
                        String eventAddress = events.getEventAddress();

                        MostRated mostRated = new MostRated(eventId, eventName, eventsUrl, ratingsCount, applicants, distance, eventAddress);
                        arrMostRated.add(mostRated);
                    }
                }


                Collections.sort(arrMostRated, new Comparator<MostRated>() {
                    @Override
                    public int compare(MostRated t1, MostRated t2) {
                        return Double.compare(t1.getDistance(), t2.getDistance());
                    }
                });

                progressBar.setVisibility(View.GONE);

                adapterNearMeItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double generateDistance(LatLng location) {

        LatLng myLatLng = new LatLng(myLatDouble, myLongDouble);

//        LatLng myLatLng = new LatLng(10.320066961476325, 123.89681572928217);


        double distanceResult = SphericalUtil.computeDistanceBetween(myLatLng, location);

        return distanceResult;
    }

    private void setRef(View view) {
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_users = view.findViewById(R.id.recyclerView_users);
    }


}