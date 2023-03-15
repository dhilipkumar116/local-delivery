package com.example.localdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.localdelivery.modelClass.Carts;
import com.squareup.picasso.Picasso;

public class ViewProductsActivity extends AppCompatActivity {

    private String orderId;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DatabaseReference orderproductref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        orderId = getIntent().getStringExtra("orderId");

        recyclerView = findViewById(R.id.wishListRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orderproductref = FirebaseDatabase.getInstance().getReference().child("orders_ID").child(orderId);

        loadProducts();
    }

    private void loadProducts() {


        FirebaseRecyclerOptions<Carts> options =
                new FirebaseRecyclerOptions.Builder<Carts>()
                        .setQuery(orderproductref.child("products"), Carts.class)
                        .build();

        FirebaseRecyclerAdapter<Carts, myViewHolder> adapter =
                new FirebaseRecyclerAdapter<Carts, myViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myViewHolder holder, int i, @NonNull Carts carts) {
                        holder.title.setText(carts.getPname());
                        holder.price.setText(carts.getSelling_price());
                        Picasso.get().load(carts.getImage()).into(holder.image);
                        holder.noofpdt.setText("no of items : " + carts.getNo_of_product());
                    }

                    @NonNull
                    @Override
                    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.product_list_layout, parent, false);
                        myViewHolder holder = new myViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }



    public class myViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title, noofpdt, price;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Cart_product_NameQuantity);
            price = itemView.findViewById(R.id.Cart_product_Price);
            noofpdt = itemView.findViewById(R.id.Cart_product_Noofproduct);
            image = itemView.findViewById(R.id.wishlist_image_lay);
        }
    }
}
