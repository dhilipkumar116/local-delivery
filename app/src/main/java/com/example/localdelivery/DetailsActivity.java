package com.example.localdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.localdelivery.modelClass.Orders;
import com.example.localdelivery.prevalent.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {

    private TextView shopName,shopAddress,shopPhno,shopPrice,shopTime,orderDistance,
                     orderName,orderPhno,orderAddress,orderPrice,deliveryPrice,totalPrice;
    private Button shopLocation,orderLocation,shopCall,orderCall,viewProduct;
    private String shopId,userId,orderId , distanceToShop;
    private String shopPhoneNumber,orderPhoneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        shopId = getIntent().getStringExtra("shopId");
        userId = getIntent().getStringExtra("userId");
        orderId = getIntent().getStringExtra("orderId");


        shopName = findViewById(R.id.shopName);
        shopAddress = findViewById(R.id.shopAddress);
        shopPhno = findViewById(R.id.shopPhno);
        shopPrice = findViewById(R.id.shopProductPrice);
        shopTime = findViewById(R.id.shopTime);
        orderName = findViewById(R.id.orderName);
        orderPhno = findViewById(R.id.orderPhno);
        orderAddress = findViewById(R.id.orderAddress);
        orderPrice = findViewById(R.id.orderPrice);
        deliveryPrice = findViewById(R.id.deliveryPrice);
//        totalPrice = findViewById(R.id.totalPrice);
        orderDistance = findViewById(R.id.orderDistance);
        shopLocation = findViewById(R.id.shopLocation);
        orderLocation = findViewById(R.id.orderLocation);
        shopCall = findViewById(R.id.shopCall);
        orderCall = findViewById(R.id.orderCall);
        viewProduct = findViewById(R.id.view_products_btn);

        displayFromdetails();
        displayTodetails();

        shopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,MapsActivity.class);
                intent.putExtra("useroradmin","shop");
                intent.putExtra("orderID",orderId);
                intent.putExtra("ID",shopId);
                startActivity(intent);
            }
        });

        orderLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,MapsActivity.class);
                intent.putExtra("useroradmin","user");
                intent.putExtra("orderID",orderId);
                intent.putExtra("ID",userId);
                startActivity(intent);
            }
        });

        orderCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makecall = new Intent(Intent.ACTION_DIAL);
                makecall.setData(Uri.parse("tel:" +orderPhoneNumber));
                startActivity(makecall);
            }
        });
        shopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makecall = new Intent(Intent.ACTION_DIAL);
                makecall.setData(Uri.parse("tel:" +shopPhoneNumber));
                startActivity(makecall);
            }
        });

        viewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,ViewProductsActivity.class);
                intent.putExtra("orderId",orderId);
                startActivity(intent);
            }
        });
    }

    private void displayFromdetails() {
        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopId);
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopName.setText("shop name : "+shopId);
                shopPhno.setText("shop ph no : "+dataSnapshot.child("shop_phone").getValue().toString());
                shopAddress.setText("shop address : "+dataSnapshot.child("street").getValue().toString()+" , "+
                        dataSnapshot.child("district").getValue().toString()+" , "+
                        dataSnapshot.child("postcode").getValue().toString());
                shopPhoneNumber = dataSnapshot.child("shop_phone").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayTodetails() {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("orders_ID");
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(orderId).exists()){
                    Orders orders = dataSnapshot.child(orderId).getValue(Orders.class);
                    orderName.setText("name : "+orders.getOrdername());
                    orderPhno.setText("ph no : "+orders.getPhone_number());
                    orderAddress.setText("address : "+orders.getAddress());
                    orderPrice.setText("product price : "+ constants.notation+orders.getTotprice());
                    deliveryPrice.setText("delivery fee : "+constants.notation+orders.getDelivery_fee());
//                totalPrice.setText("total price : RM"+Double.parseDouble(orders.getTotprice())
//                +Double.parseDouble(orders.getDelivery_fee()));
                    orderDistance.setText("distance : "+orders.getKm()+"km");
                    shopTime.setText("ordered at : "+orders.getTime());
                    shopPrice.setText("product price : "+orders.getTotprice());
                    orderPhoneNumber = orders.getPhone_number();

                }else {
                    shopLocation.setEnabled(false);
                    orderLocation.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
