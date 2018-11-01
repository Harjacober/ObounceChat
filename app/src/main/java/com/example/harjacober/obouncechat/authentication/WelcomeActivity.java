package com.example.harjacober.obouncechat.authentication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.ui.MainActivity;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;

public class WelcomeActivity extends AppCompatActivity {
    public static final String SIGNEDIN = "signedIn";
    public static final String INFOSAVED = "infoSaved";
    public static final String EXTRAINFOSAVED = "extraInfoSaved";
    public static final String SIGNED_IN_BEFORE = "signedInBefore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (SharedPreferenceUtils
                .retrieveRegProgress(this)) {
            case "":
                setContentView(R.layout.activity_welcome);
                Button button = findViewById(R.id.btn_getting_started);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WelcomeActivity.this,
                                SignInActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case SIGNEDIN: {
                Intent intent = new Intent(this,
                        UserInfoActivity.class);
                startActivity(intent);
                break;
            }
            case INFOSAVED: {
                Intent intent = new Intent(this,
                        UserExtraInfoActivity.class);
                startActivity(intent);

                break;
            }
            case EXTRAINFOSAVED: {
                Intent intent = new Intent(this,
                        MainActivity.class);
                startActivity(intent);
                break;
            }
            case SIGNED_IN_BEFORE: {
                Intent intent = new Intent(this,
                        MainActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

}
