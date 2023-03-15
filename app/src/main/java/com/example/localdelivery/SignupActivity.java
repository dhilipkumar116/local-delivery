package com.example.localdelivery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localdelivery.prevalent.constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import static com.example.localdelivery.prevalent.userPrevalent.phonePattern;

public class SignupActivity extends AppCompatActivity {

    private EditText userName, password, phoneNumber, fullName, address, vehicleNumber, vehicleType;
    private ImageView pickIcFront, pickIcback, pickLiFront, pickLiBack, pickSelie;
    private Button uploadIcFront, uploadIcBack, uploadLiFront, uploadLiBack, uploadSelfie;
    private TextView registerBtn;
    ProgressDialog progressDialog;
    private Uri imageUri;
    private String myUrl;
    private boolean isImagePicked = false, uploadedOneDocument = false;
    private DatabaseReference accref;
    private String docSelected="";
    private ImageView icFpicked, icBpicked, liFpicked, liBpicked, selfiepicked;
    private boolean sucess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phoneNumber);
        fullName = findViewById(R.id.fullName);
        address = findViewById(R.id.address);
        pickIcFront = findViewById(R.id.pick_icFront);
        pickIcback = findViewById(R.id.pick_icBack);
        pickLiFront = findViewById(R.id.pick_LiFront);
        pickLiBack = findViewById(R.id.pick_LiBack);
        pickSelie = findViewById(R.id.pick_SelfiePic);
        uploadIcFront = findViewById(R.id.upload_icFront);
        uploadIcBack = findViewById(R.id.upload_icBack);
        uploadLiFront = findViewById(R.id.upload_LiFront);
        uploadLiBack = findViewById(R.id.upload_LiBack);
        uploadSelfie = findViewById(R.id.upload_SelfiePic);
        registerBtn = findViewById(R.id.registerBtn);
        vehicleNumber = findViewById(R.id.vehicleNumber);
        vehicleType = findViewById(R.id.typeofVehicle);
        progressDialog = new ProgressDialog(this);

        icFpicked = findViewById(R.id.icFrontPicked);
        icBpicked = findViewById(R.id.icBackPicked);
        liFpicked = findViewById(R.id.liFrontPicked);
        liBpicked = findViewById(R.id.liBackPicked);
        selfiepicked = findViewById(R.id.selfiePicked);

        accref = FirebaseDatabase.getInstance().getReference().child("delivery_account");


//        pickIcFront.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CropImage.activity(imageUri).setAspectRatio(1, 1)
//                        .start(SignupActivity.this);
//                docSelected = "icFront";
//
//            }
//        });
//        pickIcback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CropImage.activity(imageUri).setAspectRatio(1, 1)
//                        .start(SignupActivity.this);
//                docSelected = "icBack";
//            }
//        });
//        pickLiFront.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CropImage.activity(imageUri).setAspectRatio(1, 1)
//                        .start(SignupActivity.this);
//                docSelected = "liFront";
//            }
//        });
//        pickLiBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CropImage.activity(imageUri).setAspectRatio(1, 1)
//                        .start(SignupActivity.this);
//                docSelected = "liBack";
//
//            }
//        });
//        pickSelie.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CropImage.activity(imageUri).setAspectRatio(1, 1)
//                        .start(SignupActivity.this);
//                docSelected = "selfie";
//            }
//        });
//        uploadLiFront.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateData(docSelected);
//                liFpicked.setVisibility(View.VISIBLE);
//            }
//        });
//        uploadLiBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateData(docSelected);
//                liBpicked.setVisibility(View.VISIBLE);
//            }
//        });
//        uploadIcFront.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateData(docSelected);
//                icFpicked.setVisibility(View.VISIBLE);
//            }
//        });
//        uploadIcBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateData(docSelected);
//                icBpicked.setVisibility(View.VISIBLE);
//            }
//        });
//        uploadSelfie.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateData(docSelected);
//                selfiepicked.setVisibility(View.VISIBLE);
//            }
//        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData("");
                //checkAllFilesUploaded();
            }
        });

    }


    private void checkAllFilesUploaded() {

        accref.child(userName.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("liFront").exists()
                            && dataSnapshot.child("liBack").exists()
                            && dataSnapshot.child("icFront").exists()
                            && dataSnapshot.child("icBack").exists()
                            && dataSnapshot.child("selfie").exists()) {
                        Toast.makeText(SignupActivity.this, "successfuly registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this,MainActivity.class));
                    } else {
                        Toast.makeText(SignupActivity.this, "upload all documents", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "upload all documents", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            isImagePicked = true;
        }
    }

    private boolean validateData(final String docName) {
        if (userName.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter username", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter password", Toast.LENGTH_SHORT).show();
        } else if (phoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter phone number", Toast.LENGTH_SHORT).show();
        } else if (fullName.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter fullname", Toast.LENGTH_SHORT).show();
        } else if (address.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter address", Toast.LENGTH_SHORT).show();
        } else if (vehicleNumber.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter vehicle number", Toast.LENGTH_SHORT).show();
        } else if (vehicleType.getText().toString().isEmpty()) {
            Toast.makeText(SignupActivity.this, "enter vehicle type", Toast.LENGTH_SHORT).show();
        } else if (!phoneNumber.getText().toString().trim().equals(phonePattern)) {
            Toast.makeText(SignupActivity.this, "enter vehicle type", Toast.LENGTH_SHORT).show();
        }
//        else if (!isImagePicked) {
//            Toast.makeText(SignupActivity.this, "pick image", Toast.LENGTH_SHORT).show();
//        }
//        else if(docSelected.equals("")){
//            Toast.makeText(SignupActivity.this, "pick license front image", Toast.LENGTH_SHORT).show();
//        }
         else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", userName.getText().toString().trim());
            map.put("password", password.getText().toString().trim());
            map.put("phno", phoneNumber.getText().toString().trim());
            map.put("fullname", fullName.getText().toString().trim());
            map.put("address", address.getText().toString().trim());
            map.put("earnings", "0.0");
            map.put("wallet", "0.0");
            map.put("vehicleNum", vehicleNumber.getText().toString().trim());
            map.put("vehicleType", vehicleType.getText().toString().trim());
            map.put("approval", constants.approvalStatus);
            accref.child(userName.getText().toString().trim()).updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignupActivity.this,"signed up",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this,MainActivity.class));
                        }
                    });

//            accref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child(userName.getText().toString().trim()).exists() && uploadedOneDocument == false) {
//                        Toast.makeText(SignupActivity.this, "username taken, try another", Toast.LENGTH_SHORT).show();
//                    } else if (uploadedOneDocument) {
//                        if(uploadToPicDatabase(docName)){
//                            sucess = true;
//                        }else {
//                            sucess = false;
//                        }
//                    } else {
//                        if(uploadToPicDatabase(docName)){
//                            sucess = true;
//                        }else {
//                            sucess = false;
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

        }
        return sucess;
    }

    private boolean uploadToPicDatabase(final String docName) {
        progressDialog.setMessage("Uploading your file... ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("delivery profile").child(userName.getText().toString());
        final StorageReference filepath = storageReference.child(docName + ".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);
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
                    myUrl = downloadUrl.toString();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", userName.getText().toString().trim());
                    map.put("password", password.getText().toString().trim());
                    map.put("phno", phoneNumber.getText().toString().trim());
                    map.put("fullname", fullName.getText().toString().trim());
                    map.put("address", address.getText().toString().trim());
                    map.put("earnings", "0.0");
                    map.put("wallet", "0.0");
                    map.put("vehicleNum", vehicleNumber.getText().toString().trim());
                    map.put("vehicleType", vehicleType.getText().toString().trim());
                    map.put("approval", "not approved");
                    map.put(docName, myUrl);
                    accref.child(userName.getText().toString().trim()).updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    isImagePicked = false;
                                    imageUri = Uri.EMPTY;
                                    uploadedOneDocument = true;
                                    Toast.makeText(SignupActivity.this, "added", Toast.LENGTH_SHORT).show();
                                    sucess = true;
                                }

                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    sucess =  false;
                }
            }
        });
        return sucess;

    }
}
