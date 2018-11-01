package com.example.harjacober.obouncechat.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.harjacober.obouncechat.data.Chats;
import com.example.harjacober.obouncechat.repository.FirebaseQueryLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatsListViewModel extends ViewModel {
    private FirebaseUser mUser =
            FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference rootReference =
            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(mUser.getUid()).child("chats");
    private FirebaseQueryLiveData allChatsLiveData =
            new FirebaseQueryLiveData(rootReference);
    private LiveData<List<Chats>> allChatsLivedataHere =
            Transformations.map(allChatsLiveData, new ALlGroupDeserializer());
    private class ALlGroupDeserializer implements Function<DataSnapshot, List<Chats>> {

        @Override
        public List<Chats> apply(DataSnapshot input) {
            List<Chats> chatsList = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()){
                Chats chats = snapshot.getValue(Chats.class);
                chatsList.add(chats);
            }
            return chatsList;
        }
    }
    @NonNull
    public LiveData<List<Chats>> getAllChatsLivedata(){
        return allChatsLivedataHere;
    }
}
