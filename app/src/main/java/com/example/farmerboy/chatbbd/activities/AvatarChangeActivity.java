package com.example.farmerboy.chatbbd.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 *  Created by farmerboy on 4/19/2017.
 */
public class AvatarChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CircleImageView mIvAvaIcon;
    private Button mBtnConfirm, mBtnChooseFile;
    private ImageButton mBtnHome;
    private Uri photoUri;
    private static final int ACTIVITY_SELECT_IMAGE = 2000;
    // string để lưu đường dẫn avatar
    private String mPath = "";
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        firebaseInitialization();

        mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);
        mBtnChooseFile = (Button) findViewById(R.id.btnChooseFile);
        mIvAvaIcon = (CircleImageView) findViewById(R.id.ivAvaIcon);

        mBtnChooseFile.setOnClickListener(this);
        mBtnHome.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);

        FirebaseUser user = mAuth.getCurrentUser();
        photoUri = user.getPhotoUrl();

        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            Picasso.with(this).load(photoUri).into(mIvAvaIcon);
        }
        else Picasso.with(this).load(R.drawable.ic_avatar_pattern).into(mIvAvaIcon);
    }

    private void firebaseInitialization() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mConnectionRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG11", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG11", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnConfirm) {
            checkAvatar();
        }
        else if (i == R.id.btnHome) {
            finish();
        }
        else if (i == R.id.btnChooseFile) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
        }
    }

    private void checkAvatar() {
        if (mPath.isEmpty()) {
            Toast.makeText(AvatarChangeActivity.this, "Vui lòng chọn ảnh đại diện mới", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(AvatarChangeActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();

        // khởi trị upload ảnh
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://chatbbd.appspot.com");
        StorageReference storageRef = storage.getReference();

        // bóp size của ảnh để tránh ảnh quá lớn + lấy ảnh từ CircleImageView luôn
        mIvAvaIcon.setDrawingCacheEnabled(true);
        mIvAvaIcon.buildDrawingCache();
        Bitmap bitmap = mIvAvaIcon.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference avatarRef = storageRef.child("images/" + user.getUid() + ".jpg");
        UploadTask uploadTask = avatarRef.putBytes(data);

        // upload ảnh
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(AvatarChangeActivity.this, "Đổi hình đại diện thất bại", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // thành công thì lấy uri bỏ vào user
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AvatarChangeActivity.this, "Đổi hình đại diện thành công", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG11", "User profile updated.");
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(AvatarChangeActivity.this, "Đổi hình đại diện thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Toast.makeText(AvatarChangeActivity.this, "Đổi hình đại diện thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // sau khi chọn ảnh xong sẽ đưa đường dẫn ảnh vào biến mPath và dùng Picasso để hiển thị avatar
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            mPath = getRealPathFromURI(selectedImageUri);
            Picasso.with(AvatarChangeActivity.this).load(new File(mPath)).into(mIvAvaIcon);
        }
    }

    // chuyển Uri ảnh về String
    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }
}
