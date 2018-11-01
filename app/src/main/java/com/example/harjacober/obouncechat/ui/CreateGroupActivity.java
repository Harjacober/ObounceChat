package com.example.harjacober.obouncechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.authentication.UserInfoActivity;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.utils.ProfileImageUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.harjacober.obouncechat.utils.ProfileImageUtils.CAMERA;
import static com.example.harjacober.obouncechat.utils.ProfileImageUtils.GALLERY;

public class CreateGroupActivity extends AppCompatActivity {

    private String groupId;
    private TextView nameField;
    private TextView purposeField;
    private CircleImageView groupIcon;
    private String path;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private ArrayList<String> membersIdList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Intent intent = getIntent();
        if (intent.hasExtra("groupId")){
            groupId = intent.getStringExtra("groupId");
            membersIdList = intent.getStringArrayListExtra("membersIds");
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        nameField = findViewById(R.id.group_name);
        purposeField = findViewById(R.id.group_purpose);
        groupIcon = findViewById(R.id.group_icon);

        groupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileImageUtils.showPictureDialog(CreateGroupActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create){
            if (path != null) {
                UploadUserInformation(path,
                        groupId);
            }
        }
        return super.onOptionsItemSelected(item);
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
                    Toast.makeText(CreateGroupActivity.this,
                            "Image Saved!", Toast.LENGTH_SHORT).show();
                    groupIcon.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateGroupActivity.this,
                            "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            groupIcon.setImageBitmap(thumbnail);
            new saveImageTask().execute(thumbnail);
            Toast.makeText(CreateGroupActivity.this,
                    "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    class saveImageTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            String filePath = ProfileImageUtils.saveImage(bitmap
                    , CreateGroupActivity.this);
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            path = s;
            super.onPostExecute(s);
        }
    }

    private void saveGroupInfo(Uri profileUrl, Uri thumbnail) {
        GroupInfo info = new GroupInfo();
        info.setGroupName(nameField.getText().toString());
        info.setGroupId(groupId);
        info.setGrouPurpose(purposeField.getText().toString());
        info.setCreatedAt(System.currentTimeMillis());
        info.setGroupThumnail(String.valueOf(thumbnail));
        info.setGroupProfileUrl(String.valueOf(profileUrl));
        progressDialog.setMessage("Almoset done...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("threads").child(groupId).child("details")
                .setValue(info).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateAllMembersOfGroup();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void updateAllMembersOfGroup() {
        for (String mIds : membersIdList){
            progressDialog.setMessage("Done...");
            DatabaseReference databaseReference =
                    FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("users").child(mIds).child("groups")
                    .child(groupId).setValue(true).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CreateGroupActivity.this,
                                            "Group created ",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateGroupActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                    );
        }

    }

    public void UploadUserInformation(final String path, final String groupId) {
        final StorageReference ref = storageReference.child(
                "images/" + groupId + ".jpg");
        if (path != null) {
            progressDialog.setTitle("Creating group...");
            progressDialog.show();
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
                        Toast.makeText(CreateGroupActivity.this,
                                "Uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        uploadThumbnail(ProfileImageUtils.generateThumnail(path),
                                groupId,
                                downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
    public void uploadThumbnail(Bitmap bitmap,
                                String groupId,
                                final Uri downloadUri){
        progressDialog.setTitle("just a moment...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref = storageReference.child(
                "images/" + groupId + "_thumbnail.jpg");
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
                    saveGroupInfo(downloadUri, thumbnailUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}
