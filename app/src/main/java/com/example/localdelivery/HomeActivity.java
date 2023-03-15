package com.example.localdelivery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localdelivery.modelClass.deliver;
import com.example.localdelivery.prevalent.userPrevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LinearLayout noordertaken_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        noordertaken_layout = findViewById(R.id.noordertaken_layout);
        recyclerView = findViewById(R.id.activeRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.waiting:
                        startActivity(new Intent(getApplicationContext(), WaitingActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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

    }


    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference deliveryOrderRef = FirebaseDatabase.getInstance().getReference()
                .child("delivery_account").child(userPrevalent.currentUser.getUsername());
        deliveryOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("orders").exists()) {
                    noordertaken_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("orders_ID");
        final DatabaseReference finalOrderRef = FirebaseDatabase.getInstance().getReference().child("Final_OrderList");

        FirebaseRecyclerOptions<deliver> options =
                new FirebaseRecyclerOptions.Builder<deliver>()
                        .setQuery(deliveryOrderRef.child("orders"), deliver.class).build();

        FirebaseRecyclerAdapter<deliver, myViewHolder> adapter =
                new FirebaseRecyclerAdapter<deliver, myViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final myViewHolder holder, int i, @NonNull final deliver deliver) {


                        shopRef.child(deliver.getShopId()).child("Orders").child(deliver.getOrderId()).
                                addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("status").exists()){
                                    if(dataSnapshot.child("status").getValue().equals("picked up")){
                                        holder.status.setText("status : picked up");
                                    }
                                    if(dataSnapshot.child("status").getValue().equals("on the way")){
                                        holder.status.setText("status : on the way");
                                    }
                                    if(dataSnapshot.child("status").getValue().equals("delivered")){
                                        holder.status.setText("status : delivered");
                                        holder.delete.setVisibility(View.VISIBLE);
                                    }
                                    holder.call.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent makecall = new Intent(Intent.ACTION_DIAL);
                                            makecall.setData(Uri.parse("tel:" +
                                                    dataSnapshot.child("phone_number").getValue().toString()));
                                            startActivity(makecall);
                                        }
                                    });
                                }else{
                                    holder.status.setText("status : delivered");
                                    holder.delete.setVisibility(View.VISIBLE);
                                    holder.dummybar.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });


                        holder.orderid.setText("order id : " + deliver.getOrderId());
                        holder.address.setText("address : " + deliver.getoAddress());
                        holder.distance.setText("distance : " + deliver.getoDistance() + "km");
                        holder.time.setText("ordered At : " + deliver.getoTime());
                        holder.details.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                                intent.putExtra("shopId", deliver.getShopId());
                                intent.putExtra("userId", deliver.getUserId());
                                intent.putExtra("orderId", deliver.getOrderId());
                                startActivity(intent);
                                holder.delete.setVisibility(View.INVISIBLE);
                            }
                        });
                        holder.otw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!deliver.getoType().equals("specialOrder")){
                                    userRef.child(deliver.getUserId()).child("orders").child(deliver.getOrderId())
                                            .child("status").setValue("on the way").isSuccessful();
                                }
                                shopRef.child(deliver.getShopId()).child("Orders").child(deliver.getOrderId())
                                        .child("status").setValue("on the way").isSuccessful();
                                orderRef.child(deliver.getOrderId()).child("status").setValue("on the way").isSuccessful();
                                finalOrderRef.child(deliver.getOrderId()).child("status").setValue("on the way").isSuccessful();
                                holder.delete.setVisibility(View.INVISIBLE);
                                holder.status.setText("status : on the way");

                            }
                        });
                        holder.delivered.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!deliver.getoType().equals("specialOrder")){
                                    userRef.child(deliver.getUserId()).child("orders").child(deliver.getOrderId())
                                            .child("status").setValue("delivered").isSuccessful();
                                }
                                shopRef.child(deliver.getShopId()).child("Orders").child(deliver.getOrderId())
                                        .child("status").setValue("delivered").isSuccessful();
                                orderRef.child(deliver.getOrderId()).child("status").setValue("delivered").isSuccessful();
                                finalOrderRef.child(deliver.getOrderId()).child("status").setValue("delivered").isSuccessful();
                                holder.delete.setVisibility(View.VISIBLE);
                                holder.status.setText("status : delivered");

                            }
                        });

                        holder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Task<Void> currRef = FirebaseDatabase.getInstance().getReference()
                                        .child("delivery_account").child(userPrevalent.currentUser.getUsername())
                                        .child("orders").child(deliver.getOrderId()).removeValue();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.activeorder_layout, parent, false);
                        return new myViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private TextView orderid, address, distance, time, details,
                otw, delivered, call,status,delete;
        private LinearLayout dummybar;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            orderid = itemView.findViewById(R.id.currOrderId);
            address = itemView.findViewById(R.id.currOrderAddress);
            distance = itemView.findViewById(R.id.currOrderDis);
            time = itemView.findViewById(R.id.currOrderTime);
            details = itemView.findViewById(R.id.currOrderDetails);
            otw = itemView.findViewById(R.id.currOrderOTW);
            delivered = itemView.findViewById(R.id.currOrderDelivered);
            call = itemView.findViewById(R.id.currOrderCall);
            status = itemView.findViewById(R.id.currOrder_status);
            delete = itemView.findViewById(R.id.currOrder_delete);
            dummybar = itemView.findViewById(R.id.dummy_bar);
            dummybar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                    alertDialog.setMessage("you can't able to access orders after delivery gets over! THANK YOU! for your service.");
                    alertDialog.show();
                }
            });
        }

    }
}
