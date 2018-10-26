package com.example.harjacober.obouncechat.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.harjacober.obouncechat.data.Message;
import com.example.harjacober.obouncechat.repository.FirebaseQueryLiveData;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatsViewModel extends ViewModel {
    private FirebaseUser mUser =
            FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference =
            FirebaseDatabase.getInstance().getReference()
            .child("chats").child(
                    chatId(mUser.getUid(), SharedPreferenceUtils
                            .retrieveCurrentFriendId())
            );
    FirebaseQueryLiveData liveData =
            new FirebaseQueryLiveData(reference);
    private LiveData<List<Message>> listLiveData =
            Transformations.map(liveData, new Deserializer());
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

    @NonNull
    public LiveData<List<Message>> getListLiveData(){
        return listLiveData;
    }



    private String chatId(String userId, String friendsId){
        int result = userId.compareTo(friendsId);
        if (result < 0){
            return userId + friendsId;
        }else{
            return friendsId + userId;
        }
    }
}
