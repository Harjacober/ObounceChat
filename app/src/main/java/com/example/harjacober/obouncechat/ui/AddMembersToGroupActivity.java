package com.example.harjacober.obouncechat.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.adapters.AddGroupMembersAdapter;
import com.example.harjacober.obouncechat.adapters.FriendsListAdapter;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddMembersToGroupActivity extends AppCompatActivity
implements AddGroupMembersAdapter.onCreateCLicked {

    private RecyclerView mrecyclerView;
    private AddGroupMembersAdapter adapter;
    List<User> friendsList;
    ArrayList<String> membersIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_to_group);

        setTitle("Add Group Members");
        friendsList = new ArrayList<>();
        membersIdList = new ArrayList<>();
        mrecyclerView = findViewById(R.id.contacts_recycler_view);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddGroupMembersAdapter(this, friendsList,
                this);
        mrecyclerView.setAdapter(adapter);
        fetchDataFromFirebase();
    }

    @Override
    public void onMenuItemClicked(ArrayList<User> selectedList) {
        //TODO Delete the group if user fails to complete registration
        FirebaseUser mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        String groupUniqueId ="group_" + mUser.getUid() + UUID.randomUUID();
        for (int i = 0; i <selectedList.size(); i++) {
            DatabaseReference reference =
                    FirebaseDatabase.getInstance()
                            .getReference();
            reference.child("threads")
                            .child(groupUniqueId)
                            .child("members").child(selectedList.get(i)
                            .getUserId()).setValue(true);
            membersIdList.add(selectedList.get(i).getUserId());
        }
        //Don't forget to add the current user id too, he is a member of the group
        membersIdList.add(mUser.getUid());

        Intent intent = new Intent(this, CreateGroupActivity.class);
        /**Pass the group id and the group members id to the activity that adds the to firebase*/
        intent.putExtra("groupId", groupUniqueId);
        intent.putStringArrayListExtra("membersIds", membersIdList);
        startActivity(intent);

    }

    public void fetchDataFromFirebase(){
        UserViewModel userViewModel =
                ViewModelProviders.of(this)
                        .get(UserViewModel.class);
        LiveData<List<User>> userLiveData = userViewModel.getUserLiveData();
        userLiveData.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> userList) {
                friendsList.clear();
                if (userList != null){
                    friendsList = userList;
                    adapter.update(friendsList);
                }
            }
        });
    }

}
