package com.example.localdelivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localdelivery.Adapters.waitingAdapter;
import com.example.localdelivery.modelClass.Orders;
import com.example.localdelivery.prevalent.constants;
import com.example.localdelivery.prevalent.userPrevalent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class WaitingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Orders> orderlist;
    private DatabaseReference orderRef;
    private waitingAdapter waitingAdapter;
    private ProgressDialog progressDialog;
    private int PERMISSION_ID = 55;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;
    private Double latitude, longitude;
    private LinearLayout notfound;
    private Double alreadyEarned, wallet;
    private int minwallet;
    private TextView notfoundText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("loading...");
        progressDialog.show();
        getLastLocation();
        notfound = findViewById(R.id.noorder_layout);
        recyclerView = findViewById(R.id.waitingRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        notfoundText = findViewById(R.id.notfoundText);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.waiting);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.waiting:
                        return true;
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("delivery_account").child(userPrevalent.currentUser.getUsername());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alreadyEarned = Double.parseDouble(dataSnapshot.child("earnings").getValue().toString());
                wallet = Double.parseDouble(dataSnapshot.child("wallet").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference utilites = FirebaseDatabase.getInstance().getReference().child("UTILITIES");
        utilites.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                minwallet = Integer.parseInt(dataSnapshot.child("minwallet").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    goToAdapter(location);
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            if (mLastLocation == null) {
                Toast.makeText(WaitingActivity.this, "mlocation", Toast.LENGTH_SHORT).show();
                requestNewLocationData();
            } else {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                goToAdapter(location);
            }
        }
    };

    private void goToAdapter(final Location mLastLocation) {

        orderRef = FirebaseDatabase.getInstance().getReference().child("orders_ID");
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (wallet < minwallet) {
                    notfound.setVisibility(View.VISIBLE);
                    notfoundText.setText("your wallet is less than " + minwallet + "RM , please recharge amount!!");
                    recyclerView.setVisibility(View.GONE);
                    progressDialog.dismiss();
                } else {
                    orderlist = new ArrayList<Orders>();
                    for (DataSnapshot list : dataSnapshot.getChildren()) {
                        Orders orders = list.getValue(Orders.class);
                        if (orders.getStatus().equals("ready for delivery")
                                && orders.getPayment().equals("COD")) {
                            LatLng userLat = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            LatLng shopLat = new LatLng(orders.getShoplat(), orders.getShoplang());
                            Double distance = SphericalUtil.computeDistanceBetween(userLat, shopLat) / 1000;
                           // if (distance <= 10) {
                                orderlist.add(orders);
                                progressDialog.dismiss();
                          //  }
                        }
                    }

                    waitingAdapter = new waitingAdapter(WaitingActivity.this, orderlist, alreadyEarned, wallet);
                    recyclerView.setAdapter(waitingAdapter);
                    if (orderlist.isEmpty()) {
                        progressDialog.dismiss();
                        notfound.setVisibility(View.VISIBLE);
                        notfoundText.setText("no order available nearby 10kms");
                    } else {
                        progressDialog.dismiss();
                        notfound.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private Boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }
}
