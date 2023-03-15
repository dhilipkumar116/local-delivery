package com.example.localdelivery.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localdelivery.DetailsActivity;
import com.example.localdelivery.R;
import com.example.localdelivery.ViewProductsActivity;
import com.example.localdelivery.modelClass.Orders;
import com.example.localdelivery.prevalent.constants;
import com.example.localdelivery.prevalent.userPrevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class waitingAdapter extends RecyclerView.Adapter<waitingAdapter.myViewHolder> {

    Context context;
    ArrayList<Orders> orderlist;
    DatabaseReference cartenableref,changeorderstate_user,orderIdref,
            changeorderstate_admin,notificationRef,deliveryGuyRef,deliveryShareRef;
    ProgressDialog progressDialog;
    Double alreadyEarned,wallet;
    private Double sharepercent;
    private String riderVnum,riderName,riderPh,pickUpTime;

    public waitingAdapter(Context context,ArrayList<Orders> orderlist,Double alreadyEarned,Double wallet){
        this.context = context;
        this.orderlist = orderlist;
        this.alreadyEarned = alreadyEarned;
        this.wallet = wallet;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waiting_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {

        holder.orderId.setText("order id : "+orderlist.get(position).getID());
        holder.address.setText("address : "+orderlist.get(position).getAddress());
        holder.productAmt.setText("product price : "+ constants.notation+orderlist.get(position).getTotprice());
        holder.deliveryAmt.setText("delivery price : "+constants.notation+orderlist.get(position).getDelivery_fee());
        holder.distance.setText("distance : "+orderlist.get(position).getKm()+"km");
        holder.orderTime.setText("ordered : "+orderlist.get(position).getTime());
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("shopId",orderlist.get(position).getShopname());
                intent.putExtra("userId",orderlist.get(position).getUserId());
                intent.putExtra("orderId",orderlist.get(position).getID());
                context.startActivity(intent);
            }
        });
        orderIdref= FirebaseDatabase.getInstance().getReference().child("orders_ID")
                .child(orderlist.get(position).getID());
        notificationRef=FirebaseDatabase.getInstance().getReference().child("notification");
        cartenableref = FirebaseDatabase.getInstance().getReference().child("cart_list")
                .child("checkcartenable").child(orderlist.get(position).getUserId())
                .child(orderlist.get(position).getShopname());
        changeorderstate_admin = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(orderlist.get(position).getShopname())
                .child("Orders").child(orderlist.get(position).getID());
        changeorderstate_user = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(orderlist.get(position).getUserId())
                .child("orders").child(orderlist.get(position).getID());
        deliveryShareRef = FirebaseDatabase.getInstance().getReference().child("UTILITIES");
        final DatabaseReference deliUser = FirebaseDatabase.getInstance().getReference()
                .child("delivery_account").child(userPrevalent.currentUser.getUsername());
        deliveryShareRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharepercent = Double.parseDouble(dataSnapshot.child("deliveryshare").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("do you want to pick up this order ?");
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("processing....");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        deliveryGuyRef = FirebaseDatabase.getInstance().getReference().child("delivery_account")
                                .child(userPrevalent.currentUser.getUsername());
                        if(!orderlist.get(position).getType().equals("specialOrder")){
                            cartenableref.child("status").setValue("picked up").isSuccessful();
                            changeorderstate_user.child("status").setValue("picked up").isSuccessful();
                        }
                        orderIdref.child("status").setValue("picked up").isSuccessful();
                        changeorderstate_admin.child("status").setValue("picked up").isSuccessful();
                        Double adminShare = Double.parseDouble(orderlist.get(position).getDelivery_fee())*sharepercent;
                        Double thisEarnings = Double.parseDouble(orderlist.get(position).getDelivery_fee())+alreadyEarned;
                        DecimalFormat decimalFormat = new  DecimalFormat("#0.00");
                        deliveryGuyRef.child("earnings").setValue(decimalFormat.format(thisEarnings));
                        //deliveryGuyRef.child("wallet").setValue(decimalFormat.format(wallet-adminShare));
                        deliveryGuyRef.child("wallet").setValue(decimalFormat.format(0.0));
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("shopId").setValue(orderlist.get(position).getShopname());
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("userId").setValue(orderlist.get(position).getUserId());
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("orderId").setValue(orderlist.get(position).getID());
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("oAddress").setValue(orderlist.get(position).getAddress());
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("oTime").setValue(orderlist.get(position).getTime());
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("oDistance").setValue(orderlist.get(position).getKm());
                        deliveryGuyRef.child("orders").child(orderlist.get(position).getID()).child("oType").setValue(orderlist.get(position).getType());
                        if(!orderlist.get(position).getType().equals("specialOrder")){
                            changeorderstate_user.child("deliveryID").setValue(userPrevalent.currentUser.getUsername()).isSuccessful();
                        }
                        changeorderstate_admin.child("deliveryID").setValue(userPrevalent.currentUser.getUsername()).isSuccessful();
                        copyNodeToFinalOrderNode(orderIdref,orderlist.get(position).getID(),
                                deliveryGuyRef,orderlist.get(position).getShopname(),
                                orderlist.get(position).getUserId());
                    }
                });
                builder.show();
            }
        });
        holder.viewproducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        context, ViewProductsActivity.class
                );
                intent.putExtra("orderId",orderlist.get(position).getID());
                context.startActivity(intent);
            }
        });

    }
    private void copyNodeToFinalOrderNode(DatabaseReference orderIdref,
                                          final String OrderId, DatabaseReference deliveryGuyRef,
                                          final String shopName,final String userID) {
        final DatabaseReference ToNode = FirebaseDatabase.getInstance().getReference().child("Final_OrderList");
        deliveryGuyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                riderName = dataSnapshot.child("username").getValue().toString();
                riderPh = dataSnapshot.child("phno").getValue().toString();
                riderVnum = dataSnapshot.child("vehicleNum").getValue().toString();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
                pickUpTime = currentTime.format(calendar.getTime());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        orderIdref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ToNode.child(OrderId).setValue(dataSnapshot.getValue());
                ToNode.child(OrderId).setValue(dataSnapshot.getValue());
                ToNode.child(OrderId).child("riderName").setValue(riderName);
                ToNode.child(OrderId).child("riderVnum").setValue(riderVnum);
                ToNode.child(OrderId).child("riderPhno").setValue(riderPh);
                ToNode.child(OrderId).child("paid").setValue("not");
                ToNode.child(OrderId).child("pickUpTime").setValue(pickUpTime)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    sendNotification(shopName,userID);
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(String shopName,String userID) {

        final DatabaseReference notificaionref = FirebaseDatabase.getInstance().getReference().child("notification");
        HashMap<String, String> notification = new HashMap<>();
        notification.put("from", shopName+" shop");
        notification.put("info", "delivery assigned to your order!! , soon it will be delivered");
        notificaionref.child(userID).push().setValue(notification);

    }

    @Override
    public int getItemCount() {
        return orderlist.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        private TextView orderId , address ,productAmt , deliveryAmt,distance, orderTime;
        private Button take , details , viewproducts;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_Id);
            address = itemView.findViewById(R.id.order_address);
            productAmt = itemView.findViewById(R.id.order_price);
            deliveryAmt = itemView.findViewById(R.id.delivery_price);
            distance = itemView.findViewById(R.id.order_distance);
            orderTime = itemView.findViewById(R.id.order_time);
            take = itemView.findViewById(R.id.order_take);
            details = itemView.findViewById(R.id.order_details);
            viewproducts = itemView.findViewById(R.id.order_view_product);

        }
    }
}
