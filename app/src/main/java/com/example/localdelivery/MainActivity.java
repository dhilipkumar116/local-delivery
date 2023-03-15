package com.example.localdelivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localdelivery.modelClass.Users;
import com.example.localdelivery.prevalent.userPrevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private EditText userName , passWord;
    private TextView signup;
    private CheckBox rememberme;
    private Button loginBtn;
    private DatabaseReference accref;
    private ProgressDialog Loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.login_username);
        passWord = findViewById(R.id.login_password);
        signup = findViewById(R.id.signup_btn);
        rememberme = findViewById(R.id.rememberPassword);
        loginBtn = findViewById(R.id.login_btn);
        Loadingbar = new ProgressDialog(this);
        Paper.init(this);

        checkifalreadyLOGEDIN();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignupActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = userName.getText().toString().trim();
                String password = passWord.getText().toString().trim();

                if(userName.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"enter username",Toast.LENGTH_SHORT).show();
                }else if(passWord.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"enter password",Toast.LENGTH_SHORT).show();
                }else{
                    Loadingbar.setMessage("verifying your account...");
                    Loadingbar.setCanceledOnTouchOutside(false);
                    Loadingbar.show();
                    AllowAccessToAccount(username,password);
                }

            }
        });

    }

    private void checkifalreadyLOGEDIN() {
        String username = Paper.book().read(userPrevalent.Usernamekey);
        String userpass = Paper.book().read(userPrevalent.Userpasskey);


        if (!TextUtils.isEmpty(userpass) && !TextUtils.isEmpty(username)) {
            Loadingbar.setMessage("already logged in...  please wait");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();
            AllowAccessToAccount(username, userpass);
        }
    }

    private void AllowAccessToAccount(final String username, final String password) {

        accref = FirebaseDatabase.getInstance().getReference().child("delivery_account");
        accref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(username).exists())
                {
                    final Users users = dataSnapshot.child(username).getValue(Users.class);
                    if(password.equals(users.getPassword()))
                    {
//                        if (dataSnapshot.child(username).child("liFront").exists()
//                                && dataSnapshot.child(username).child("liBack").exists()
//                                && dataSnapshot.child(username).child("icFront").exists()
//                                && dataSnapshot.child(username).child("icBack").exists()
//                                && dataSnapshot.child(username).child("selfie").exists()) {
                            if(users.getApproval().equals("approved")){
                               // String device_token = FirebaseInstanceId.getInstance().getToken();
//                        String currentShopID = mAuth.getCurrentUser().getUid();
                                //accref.child(username).child("device_token").setValue(device_token);
                                accref.child(username).child("uid").setValue("currentShopID")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Loadingbar.dismiss();
                                                    userPrevalent.currentUser = users;
                                                    if(rememberme.isChecked())
                                                    {
                                                        userPrevalent.currentUser = users;
                                                        Paper.book().write(userPrevalent.Usernamekey, username);
                                                        Paper.book().write(userPrevalent.Userpasskey, password);
                                                    }
                                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));

                                                }else {
                                                    Loadingbar.dismiss();
                                                    Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else {
                                Loadingbar.dismiss();
                                Toast.makeText(MainActivity.this,"your account is not approved yet",Toast.LENGTH_SHORT).show();
                            }


//                        } else {
//                            Loadingbar.dismiss();
//                            Toast.makeText(MainActivity.this, "u have not uploaded al" +
//                                    "l documents , create new account !", Toast.LENGTH_LONG).show();
//                        }

                    }else {
                        Loadingbar.dismiss();
                        Toast.makeText(MainActivity.this,"incorrect password",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Loadingbar.dismiss();
                    Toast.makeText(MainActivity.this,"sign up",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
