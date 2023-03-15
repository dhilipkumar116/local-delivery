package com.example.localdelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localdelivery.prevalent.constants;
import com.example.localdelivery.prevalent.userPrevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {

    private TextView username,vehiclenum,vehicletype,password,totolearnings,wallet,change_profile;
    private EditText phonenumber;
    private ImageView imageview,hidepassword;
    private Button save , savepass;
    DatabaseReference userRef;
    private Boolean hide = true;
    private boolean isProfileImagePicked=false;
    private ProgressDialog progressDialog;
    private Uri profileUri;
    private String profileUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        username = findViewById(R.id.username);
        vehiclenum = findViewById(R.id.vehiclenumber);
        vehicletype = findViewById(R.id.vehicletype);
        password = findViewById(R.id.password);
        totolearnings = findViewById(R.id.earnings);
        phonenumber = findViewById(R.id.phonenumber);
        imageview = findViewById(R.id.image);
        save  = findViewById(R.id.savebtn);
        hidepassword = findViewById(R.id.hidePassword);
        savepass = findViewById(R.id.savepassbtn);
        wallet = findViewById(R.id.wallet);
        change_profile = findViewById(R.id.change_profile);
        progressDialog = new ProgressDialog(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.account);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return  true;
                    case R.id.waiting:
                        startActivity(new Intent(getApplicationContext(), WaitingActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.account:
                        return true;
                }
                return false;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phonenumber.getText().toString().trim().isEmpty()){
                    Toast.makeText(AccountActivity.this,"enter phone number",Toast.LENGTH_SHORT).show();
                }
                if(!phonenumber.getText().toString().trim().equals(userPrevalent.phonePattern)){
                    Toast.makeText(AccountActivity.this,"enter valid phone number",Toast.LENGTH_SHORT).show();
                }else {
                    userRef.child("phno").setValue(phonenumber.getText().toString().trim());
                    Toast.makeText(AccountActivity.this,"saved",Toast.LENGTH_SHORT).show();
                }

            }
        });

        hidepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide==true){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hide=false;
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hide=true;
                }
            }
        });

        savepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().trim().isEmpty()){
                    Toast.makeText(AccountActivity.this,"enter password",Toast.LENGTH_SHORT).show();
                }else {
                    userRef.child("password").setValue(password.getText().toString().trim());
                    Toast.makeText(AccountActivity.this,"saved",Toast.LENGTH_SHORT).show();
                }
            }
        });

        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(profileUri).setAspectRatio(1, 1)
                        .start(AccountActivity.this);
            }
        });


        userRef = FirebaseDatabase.getInstance().getReference()
                .child("delivery_account").child(userPrevalent.currentUser.getUsername());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue().toString());
                phonenumber.setText(dataSnapshot.child("phno").getValue().toString());
                vehiclenum.setText(dataSnapshot.child("vehicleNum").getValue().toString());
                vehicletype.setText(dataSnapshot.child("vehicleType").getValue().toString());
                password.setText(dataSnapshot.child("password").getValue().toString());
                wallet.setText("wallet balance : "+ constants.notation+dataSnapshot.child("wallet").getValue().toString());
                totolearnings.setText("total earnings : "+constants.notation+dataSnapshot.child("earnings").getValue().toString());
                if(dataSnapshot.child("selfie").exists()){
                    String image = dataSnapshot.child("selfie").getValue().toString();
                    Picasso.get().load(image).into(imageview);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isProfileImagePicked){
            uploadToPicDatabase("selfie");
        }
    }
    private void uploadToPicDatabase(final String docName) {
        progressDialog.setMessage("saving profile... ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final DatabaseReference accref = FirebaseDatabase.getInstance().getReference().child("delivery_account");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("delivery profile").child(userPrevalent.currentUser.getUsername());
        final StorageReference filepath = storageReference.child(docName + ".jpg");
        final UploadTask uploadTask = filepath.putFile(profileUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = (Uri) task.getResult();
                    profileUrl = downloadUrl.toString();
                    accref.child(userPrevalent.currentUser.getUsername()).child(docName).setValue(profileUrl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    isProfileImagePicked = false;
                                    Toast.makeText(AccountActivity.this, "saved", Toast.LENGTH_SHORT).show();

                                }

                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AccountActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode== Activity.RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            profileUri = result.getUri();
            isProfileImagePicked=true;
            Picasso.get().load(profileUri).into(imageview);
        }
    }
}
