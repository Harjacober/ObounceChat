package com.example.harjacober.obouncechat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.adapters.GroupListAdapter;
import com.example.harjacober.obouncechat.data.Group;
import com.example.harjacober.obouncechat.data.GroupInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.harjacober.obouncechat.fragments.CallsFragments.displayImageInDialog;


public class GroupsFragments extends Fragment
implements GroupListAdapter.ListItmeClickedListener{
    private List<GroupInfo> groupList;
    List<Group> groups;
    private RecyclerView mrecyclerView;
    private GroupListAdapter adapter;
    private LinearLayout emptyView;
    List<String> groupIds;

    public GroupsFragments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =
             inflater.inflate(R.layout.fragment_groups_fragments, container, false);
        groupList = new ArrayList<>();
        groups = new ArrayList<>();
        groupIds = new ArrayList<>();
        emptyView = view.findViewById(R.id.empty_view);
        mrecyclerView = view.findViewById(R.id.contacts_recycler_view);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupListAdapter(groupList, groups,
                getContext(),
                this);
        mrecyclerView.setAdapter(adapter);

        fetchDataFromFirebase();
        return view;
    }

    public void fetchDataFromFirebase(){
       /* GroupChatsViewModel userViewModel =
                ViewModelProviders.of(this)
                        .get(GroupChatsViewModel.class);
        LiveData<List<Group>> allGroupLivedata = userViewModel.getAllGroupLivedata();
        allGroupLivedata.observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groupsHere) {
                groupList.clear();
                if (groupsHere != null) {
                    for (Group group : groupsHere) {
                        groupList.add(group.getDetails());
                    }
                    groups.addAll(groupsHere);
                    adapter.update(groupList, groups);
                }
            }
        });*/
       /**First get the ids of all the group the user belong
       to and use the id to query the groups data*/
        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid()).child("groups");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String groupId = snapshot.getKey();
                            groupIds.add(groupId);
                        }
                        queryAllGroupInformation(groupIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void queryAllGroupInformation(final List<String> groupIds) {
        for (String groupId : groupIds) {
            DatabaseReference rootReference =
                    FirebaseDatabase.getInstance().getReference()
                            .child("threads").child(groupId);
            rootReference.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Group eachGroup = dataSnapshot.getValue(Group.class);
                            groups.add(eachGroup);
                            if (eachGroup.getDetails() != null) {
                                emptyView.setVisibility(View.INVISIBLE);
                                mrecyclerView.setVisibility(View.VISIBLE);
                                groupList.add(eachGroup.getDetails());
                            } else {
                                emptyView.setVisibility(View.VISIBLE);
                                mrecyclerView.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
        adapter.update(groupList, groups);
    }

    @Override
    public void onProfilePicClickedListener(String thumbnail, String profileUrl, String fullName) {
        displayImageInDialog(thumbnail,
                profileUrl,
                fullName,
                getContext(),
                getActivity());
    }
}
