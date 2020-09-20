package com.example.myinsta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myinsta.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close, image_profie;
    TextView save, tvchange;
    MaterialEditText fullname,username,bio;

    FirebaseUser firebaseUser;
    private Uri mImageuri;
    private StorageTask uploadTask;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close=findViewById(R.id.close);
        image_profie=findViewById(R.id.image_profile);
        save=findViewById(R.id.save);
        tvchange=findViewById(R.id.tv_change);
        fullname=findViewById(R.id.fullnamee);
        username=findViewById(R.id.username);
        bio=findViewById(R.id.bio);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageRef= FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                fullname.setText(users.getFullname());
                username.setText(users.getUsername());
                bio.setText(users.getBio());
                Glide.with(getApplicationContext()).load(users.getImageurl()).into(image_profie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        image_profie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(fullname.getText().toString()
                        ,username.getText().toString()
                        ,bio.getText().toString());
            }
        });


    }
    private void updateProfile(String fullname, String username, String bio){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String , Object>hashMap=new HashMap<>();
        hashMap.put("fullname",fullname);
        hashMap.put("username",username);
        hashMap.put("bio",bio);

        reference.updateChildren(hashMap);

    }

    private String getFileextension(Uri uri){

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void Uploadimage(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();

        if(mImageuri!=null){
            final StorageReference filerefrence=storageRef.child(System.currentTimeMillis() +"."+getFileextension(mImageuri));

            uploadTask=filerefrence.putFile(mImageuri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw   task.getException();
                    }
                    return filerefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        String myurl=downloaduri.toString();

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("imageurl",""+myurl);
                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageuri = result.getUri();

            Uploadimage();

        } else {
            Toast.makeText(this, "Something Gone Wrong!", Toast.LENGTH_SHORT).show();
        }

    }
}