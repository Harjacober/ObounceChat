package com.example.harjacober.obouncechat.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.ui.MainActivity;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.harjacober.obouncechat.authentication.WelcomeActivity.EXTRAINFOSAVED;

public class UserExtraInfoActivity extends AppCompatActivity {
    private EditText mFullName;
    private EditText mSelfDesc;
    private ProgressBar mLoadIndicator;
    private LinearLayout linearLayout;
    private TextView mSaving;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference rootRefernce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_extra_info);

        mFullName = findViewById(R.id.edt_fullname);
        mSelfDesc = findViewById(R.id.edt_self_desc);
        mLoadIndicator = findViewById(R.id.load_indicator);
        linearLayout = findViewById(R.id.linear_layout);
        mSaving = findViewById(R.id.tv_saving);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        rootRefernce = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.extra_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_next:
                saveInfoToDatabase(mFullName.getText().toString(),
                        mSelfDesc.getText().toString());
                break;
        }
        return true;
    }

    private void launchCorrespondingActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void saveInfoToDatabase(final String fullName, final String selfDesc) {
        linearLayout.setVisibility(View.INVISIBLE);
        mLoadIndicator.setVisibility(View.VISIBLE);
        mSaving.setVisibility(View.VISIBLE);
        String uId = mUser.getUid();

        Map update = new HashMap();
        update.put("fullName", fullName);
        update.put("statusText", selfDesc);
        update.put("registrationStatus", "completed");
        rootRefernce.child("users").child(uId).updateChildren(update)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        saveUserInfoToDevice(fullName, selfDesc);
                        SharedPreferenceUtils.saveRegistrationProgress(
                                UserExtraInfoActivity.this,
                                EXTRAINFOSAVED
                        );
                        launchCorrespondingActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserExtraInfoActivity.this,
                        "Error saving Info", Toast.LENGTH_SHORT).show();
                mLoadIndicator.setVisibility(View.INVISIBLE);
                mSaving.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveUserInfoToDevice(String fullName, String selfDesc) {
        SharedPreferenceUtils.saveUserFullName(this,
                fullName);
        SharedPreferenceUtils.saveUserSelfDesc(this,
                selfDesc);
    }
}
