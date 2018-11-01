package com.example.harjacober.obouncechat.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.harjacober.obouncechat.ui.MainActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

import androidx.browser.browseractions.BrowserActionsIntent;

public class ProfileImageUtils {
    public static final int CAMERA = 54;
    public static final int GALLERY = 24;
    private static final String IMAGE_DIRECTORY = "/ObounceChat/profileImage";

    public static void showPictureDialog(final Activity context){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary(context);
                                break;
                            case 1:
                                takePhotoFromCamera(context);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private static void choosePhotoFromGallary(Activity context) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        context.startActivityForResult(galleryIntent, GALLERY);
    }

    private static void takePhotoFromCamera(Activity context) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        context.startActivityForResult(intent, CAMERA);
    }

    //The method takes an image bitmap and the context as argument
    //This method returns the path to which the image is saved if saved successfully
    //TODO This method should be in the background
    public static String saveImage(Bitmap myBitmap, Context context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        //Compress the image
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        //Creates the directory to save the image
        File profilePictureDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        // have the object build the directory structure, if needed.
        if (!profilePictureDirectory.exists()) {
            profilePictureDirectory.mkdirs();
        }

        try {
            File f = new File(profilePictureDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    //This method upload image to firebase cloud storage from the filepath
    public static void uploadImageToCloud(String path, final Context context, String userId)
            throws FileNotFoundException {
        //get instance of firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //This creates the path where the image will be saved on cloud storage
        StorageReference storageReference = storage.getReference();
        final StorageReference ref = storageReference.child("images/"+ userId+".jpg");

        if(path != null) {
            InputStream stream = new FileInputStream(new File(path));

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            ref.putStream(stream)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                            Log.d("sssssss", String.valueOf(ref.getDownloadUrl()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploading "+(int)progress+"%");
                        }
                    });
        }
    }

    //This method returns the download uri as oppose to the @uploadImageToCloud above
    public static void getDownloadUriAfterUpload(String path, String userId, final Context context){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //This creates the path where the image will be saved on cloud storage
        StorageReference storageReference = storage.getReference();
        final StorageReference ref = storageReference.child("images/"+ userId+".jpg");

        if(path != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Uri file = Uri.fromFile(new File(path));
            UploadTask uploadTask = ref.putFile(file);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
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
                        progressDialog.dismiss();
                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        SharedPreferenceUtils.saveUserProfilePicUrl(context, downloadUri );
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    public static Bitmap generateThumnail(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return ThumbnailUtils.extractThumbnail(bitmap,
                100, 100);
    }
}
