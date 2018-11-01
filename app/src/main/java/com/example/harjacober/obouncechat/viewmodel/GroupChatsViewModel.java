package com.example.harjacober.obouncechat.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.harjacober.obouncechat.data.Group;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.data.Message;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.repository.FirebaseQueryLiveData;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GroupChatsViewModel extends AndroidViewModel {
    FirebaseUser mUSer =
            FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference =
            FirebaseDatabase.getInstance().getReference()
            .child("threads")
            .child(SharedPreferenceUtils.retrieveCurrentFriendId(getApplication()));
    private FirebaseQueryLiveData messageLiveData =
            new FirebaseQueryLiveData(reference.child("messages"));

    private LiveData<List<Message>> messageListLiveData =
            Transformations.map(messageLiveData, new Deserializer());

    public GroupChatsViewModel(@NonNull Application application) {
        super(application);
    }

    private class Deserializer implements Function<DataSnapshot, List<Message>>{

        @Override
        public List<Message> apply(DataSnapshot input) {
            List<Message> messageList = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()){
                messageList.add(snapshot.getValue(Message.class));
            }
            return messageList;
        }
    }

    private FirebaseQueryLiveData detailsLiveData =
            new FirebaseQueryLiveData(reference.child("details"));
    private LiveData<GroupInfo> groupInfoLiveData =
            Transformations.map(detailsLiveData, new DetailsDeserializer());
    private class DetailsDeserializer implements Function<DataSnapshot, GroupInfo>{

        @Override
        public GroupInfo apply(DataSnapshot input) {
            return input.getValue(GroupInfo.class);
        }
    }

    private FirebaseQueryLiveData usersLiveData =
            new FirebaseQueryLiveData(reference.child("users"));
    private LiveData<List<User>> groupMembersLiveData =
            Transformations.map(usersLiveData, new USerDeserializer());
    private class USerDeserializer implements Function<DataSnapshot, List<User>>{
        @Override
        public List<User> apply(DataSnapshot input) {
            List<User> userList = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()){
                userList.add(snapshot.getValue(User.class));
            }
            return userList;
        }
    }

    //TODO check if the current user is a member of the group before quering it. if user is not, don't query it
/**    Or better still store all the group id a user belongs to in a node, now query the node to get the id of
    each group and now query each group info by their id. Remeber to update the mwthod that creates the group
    to create a node that stores the group id for every member of the group.*/
    private DatabaseReference rootReference =
            FirebaseDatabase.getInstance().getReference()
            .child("threads");
    private FirebaseQueryLiveData allGroupsLiveData =
            new FirebaseQueryLiveData(rootReference);
    private LiveData<List<Group>> allGroupLivedataHere =
            Transformations.map(allGroupsLiveData, new ALlGroupDeserializer());
    private class ALlGroupDeserializer implements Function<DataSnapshot, List<Group>>{

        @Override
        public List<Group> apply(DataSnapshot input) {
            List<Group> groupList = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()){
                Group group = snapshot.getValue(Group.class);
                if (group != null) {
                    groupList.add(group);
                }
            }
            return groupList;
        }
    }

    @NonNull
    public LiveData<List<Message>> getMessageListLiveData(){
        return messageListLiveData;
    }
    @NonNull
    public LiveData<GroupInfo> getGroupInfoLiveData(){
        return groupInfoLiveData;
    }
    @NonNull
    public LiveData<List<User>> getGroupMembersLiveData(){
        return groupMembersLiveData;
    }
    @NonNull
    public LiveData<List<Group>> getAllGroupLivedata(){
        return allGroupLivedataHere;
    }
}
