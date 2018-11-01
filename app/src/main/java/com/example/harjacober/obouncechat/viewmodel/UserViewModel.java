package com.example.harjacober.obouncechat.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

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

public class UserViewModel extends AndroidViewModel {
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference USER_REF =
            FirebaseDatabase.getInstance().getReference().child("users");

    private FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(USER_REF);
    private final LiveData<List<User>> listLiveData =
            Transformations.map(liveData, new Deserializer());

    public UserViewModel(@NonNull Application application) {
        super(application);
    }
    /*private final LiveData<User> userLiveData =
            Transformations.map(liveData, new Deserializer());*/

    private class Deserializer implements Function<DataSnapshot, List<User>>{

        List<String> friendsIdList = new ArrayList<>();
        @Override
        public List<User> apply(DataSnapshot input) {
            List<User> usersList = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()){
                User user = snapshot.getValue(User.class);
                String userId = user.getUserId();
                if (userId != null) {
                    if (!userId.equals(mUser.getUid())) {
                        usersList.add(user);
                        friendsIdList.add(userId);
                    }
                }
            }
            SharedPreferenceUtils.saveAllFriendsId(getApplication(), friendsIdList);
            return usersList;
        }
    }

    @NonNull
    public LiveData<List<User>> getUserLiveData(){
        return listLiveData;
    }
}
