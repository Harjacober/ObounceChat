package com.example.harjacober.obouncechat.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.adapters.ViewPagerAdapter;
import com.example.harjacober.obouncechat.fragments.CallsFragments;
import com.example.harjacober.obouncechat.fragments.ChatsFragment;
import com.example.harjacober.obouncechat.fragments.GroupsFragments;
import com.example.harjacober.obouncechat.settings.SettingsActivity;
import com.example.harjacober.obouncechat.settings.UserProfileActivity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    //Fragments

    private GroupsFragments contactsFragments;
    private ChatsFragment chatsFragment;
    private CallsFragments callsFragments;
    private MenuItem prevMenuItem;
    private static final int REQUEST_CODE = 89;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermission();
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_contact:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_chat:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_call:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        contactsFragments = new GroupsFragments();
        chatsFragment = new ChatsFragment();
        callsFragments = new CallsFragments();
        adapter.addFragment(contactsFragments);
        adapter.addFragment(chatsFragment);
        adapter.addFragment(callsFragments);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_account:
                launchUserProfileActivity();
                break;
            case R.id.menu_new_group:
                Intent intent = new Intent(this, AddMembersToGroupActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
        return true;
    }

    private void launchUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
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
