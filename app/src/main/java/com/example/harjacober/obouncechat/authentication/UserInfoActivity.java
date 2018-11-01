package com.example.harjacober.obouncechat.authentication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.utils.ProfileImageUtils;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.harjacober.obouncechat.authentication.WelcomeActivity.INFOSAVED;
import static com.example.harjacober.obouncechat.utils.ProfileImageUtils.CAMERA;
import static com.example.harjacober.obouncechat.utils.ProfileImageUtils.GALLERY;

public class UserInfoActivity extends AppCompatActivity {
    private CircleImageView mProfilePic;
    private EditText mUsernameField;
    private Button mBtnNext;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String path = null;
    private DatabaseReference rootRefernce;
    private AnimatedCircleLoadingView progressDialog;
    private static int REQUEST_CODE = 53;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        requestAllPermission();
        storage = FirebaseStorage.getInstance();

        //This creates the path where the image will be saved on cloud storage
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        rootRefernce = FirebaseDatabase.getInstance().getReference();
        mProfilePic = findViewById(R.id.circleImageView_profile_pic);
        mUsernameField = findViewById(R.id.edt_username);
        mBtnNext = findViewById(R.id.btn_next);
        progressDialog = findViewById(R.id.circle_loading_view);

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch the camera app or open the gallery
                ProfileImageUtils.showPictureDialog(UserInfoActivity.this);
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null){ UploadUserInformation(
                        path,
                        mUser.getUid(),
                        mUsernameField.getText().toString());
                }
            }
        });
    }


    private void diaableAllViews(){
        mUsernameField.setEnabled(false);
        mBtnNext.setEnabled(false);
        mBtnNext.setVisibility(View.INVISIBLE);
        mProfilePic.setEnabled(false);
    }
    private void enableAllViews(){
        mUsernameField.setEnabled(true);
        mBtnNext.setEnabled(true);
        mProfilePic.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), contentURI);
//                    path = ProfileImageUtils.saveImage(bitmap, this);
                    new saveImageTask().execute(bitmap);
                    Toast.makeText(UserInfoActivity.this,
                            "Image Saved!", Toast.LENGTH_SHORT).show();
                    mProfilePic.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserInfoActivity.this,
                            "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            mProfilePic.setImageBitmap(thumbnail);
            new saveImageTask().execute(thumbnail);
            Toast.makeText(UserInfoActivity.this,
                    "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    class saveImageTask extends AsyncTask<Bitmap, Void, String>{

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            String filePath = ProfileImageUtils.saveImage(bitmap
                    , UserInfoActivity.this);
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            path = s;
            super.onPostExecute(s);
        }
    }
    //This method returns the download uri as oppose to the @uploadImageToCloud above
    public void UploadUserInformation(final String path, final String userId,
                                      final String username) {

        //This creates the path where the image will be saved on cloud storage
        final StorageReference ref = storageReference.child(
                "images/" + userId + ".jpg");

        if (path != null) {
            diaableAllViews();
            progressDialog.startDeterminate();
            progressDialog.setPercent(63);
            progressDialog.setVisibility(View.VISIBLE);
            Uri file = Uri.fromFile(new File(path));
            UploadTask uploadTask = ref.putFile(file);

            Task<Uri> urlTask = uploadTask.continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(
                        @NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserInfoActivity.this,
                                "Uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        uploadThumbnail(ProfileImageUtils.generateThumnail(path),
                                userId,
                                downloadUri,
                                username);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
    public void uploadThumbnail(Bitmap bitmap,
                                String userId,
                                final Uri downloadUri,
                                final String username){
        progressDialog.setPercent(82);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref = storageReference.child(
                "images/" + userId + "_thumbnail.jpg");
        UploadTask uploadTask = ref.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(
                            @NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri thumbnailUri = task.getResult();
                    saveInfoToDatabase(thumbnailUri, downloadUri, username);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void saveInfoToDatabase(final Uri thumbnailUri,
                                    final Uri downloadUri,
                                    final String username) {
        progressDialog.setPercent(99);
        String uId = mUser.getUid();
        User user = new User();
        user.setProfileUrl(String.valueOf(downloadUri));
        user.setThumbnail(String.valueOf(thumbnailUri));
        user.setUsername(username);
        user.setUserId(uId);
        user.setOnline(false);
        rootRefernce.child("users").child(uId).setValue(user).addOnSuccessListener(
                new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.setPercent(100);
                progressDialog.setVisibility(View.INVISIBLE);
                Toast.makeText(UserInfoActivity.this,
                        "Info saved successfully", Toast.LENGTH_SHORT).show();
                saveUserInfoToDevice(downloadUri,
                        username,
                        thumbnailUri);
               /** Track user progress.
                        Update shared preferences that user has gotten to this stage*/
                SharedPreferenceUtils.saveRegistrationProgress(
                        UserInfoActivity.this,
                        INFOSAVED
                );
                progressDialog.stopOk();
                launchCorrespondingActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setVisibility(View.INVISIBLE);
                progressDialog.stopFailure();
                enableAllViews();
                Toast.makeText(UserInfoActivity.this,
                        "Error saving Info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfoToDevice(Uri downloadUri,
                                      String username,
                                      Uri thumbnail) {
        SharedPreferenceUtils.saveUserProfilePicUrl(UserInfoActivity.this,
                downloadUri);
        SharedPreferenceUtils.saveUsername(UserInfoActivity.this,
                username);
        SharedPreferenceUtils.savethumbnail(UserInfoActivity.this,
                thumbnail);
    }

    private void launchCorrespondingActivity() {
        Intent intent = new Intent(this, UserExtraInfoActivity.class);
        startActivity(intent);
        finish();
    }

    public void requestAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            }else {
                requestPermissions(
                        new String[]{WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                        REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            }
        }else{
            Toast.makeText(this,
                    "All Permission needs to be granted",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
