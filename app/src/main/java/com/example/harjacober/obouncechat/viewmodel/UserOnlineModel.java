package com.example.harjacober.obouncechat.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.repository.FirebaseQueryLiveData;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserOnlineModel extends AndroidViewModel {
    private DatabaseReference reference =
            FirebaseDatabase.getInstance().getReference().child("users").child(
                    SharedPreferenceUtils.retrieveCurrentFriendId(getApplication()));

    private FirebaseQueryLiveData liveData =
            new FirebaseQueryLiveData(reference);
    private LiveData<User> userLiveData =
            Transformations.map(liveData, new Deserializer());

    public UserOnlineModel(@NonNull Application application) {
        super(application);
    }

    private class Deserializer implements Function<DataSnapshot, User>{

        @Override
        public User apply(DataSnapshot input) {
            return input.getValue(User.class);
        }
    }

    @NonNull
    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }
}
