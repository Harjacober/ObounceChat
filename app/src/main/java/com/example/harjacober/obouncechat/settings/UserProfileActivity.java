package com.example.harjacober.obouncechat.settings;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harjacober.obouncechat.App;
import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.authentication.SignInActivity;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView mProfilePic;
    private TextView mUsername;
    private TextView mSelfDesc;
    private TextView mEmail;
    private TextView mPhoneNo;
    private ImageView imagePicker;
    private ImageView usernameEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setTitle(SharedPreferenceUtils.retrieveFullName(this));

        mProfilePic = findViewById(R.id.profile_pcture);
        mUsername = findViewById(R.id.tv_username);
        mSelfDesc = findViewById(R.id.tv_self);
        mEmail = findViewById(R.id.tv_email_address);
        mPhoneNo = findViewById(R.id.tv_phone_number);
        imagePicker = findViewById(R.id.image_chooser);
        usernameEditor = findViewById(R.id.img_edit_phone);

        fetchUserInfo();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchUserInfo() {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference();
        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            reference.child("users")
                    .child(user.getUid()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User userInfo = dataSnapshot.getValue(User.class);
                            if (userInfo != null) {
                                saveUserInfoToDevice(userInfo);
                                updateUI();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
    }

    private void saveUserInfoToDevice(User userInfo) {
        SharedPreferenceUtils.saveUserProfilePicUrl(this, Uri.parse(userInfo.getProfileUrl()));
        SharedPreferenceUtils.savethumbnail(this, Uri.parse(userInfo.getThumbnail()));
        SharedPreferenceUtils.saveUserFullName(this, userInfo.getFullName());
        SharedPreferenceUtils.saveUsername(this, userInfo.getUsername());
        SharedPreferenceUtils.saveUserSelfDesc(this, userInfo.getStatusText());
        SharedPreferenceUtils.saveEmail(this, userInfo.getEmail());
        SharedPreferenceUtils.savePhoneNo(this, userInfo.getPhoneNo());
    }

    private void updateUI() {
        mUsername.setText(SharedPreferenceUtils.retrieveFullName(this));
        mSelfDesc.setText(SharedPreferenceUtils.retrieveSelfDesc(this));
        mPhoneNo.setText(SharedPreferenceUtils.retrievePhoneNo(this));
        mEmail.setText(SharedPreferenceUtils.retrieveEmail(this));
        loadProfileImage(SharedPreferenceUtils.retrieveThumbnail(this),
                mProfilePic);
    }

    private void loadProfileImage(final String profileUri, final CircleImageView imageView) {
        if (!profileUri.isEmpty()) {
            App.picassoWithCache.get().load(profileUri)
                    .placeholder(imageView.getDrawable())
                    .into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.sign_out:
                signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(
                new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SharedPreferenceUtils.saveRegistrationProgress(
                        UserProfileActivity.this,
                        ""
                );
                Intent intent = new Intent(UserProfileActivity.this,
                        SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
