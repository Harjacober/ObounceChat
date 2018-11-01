package com.example.harjacober.obouncechat.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.adapters.MessageListAdapter;
import com.example.harjacober.obouncechat.data.Chats;
import com.example.harjacober.obouncechat.data.Group;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.data.Message;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.example.harjacober.obouncechat.viewmodel.ChatsViewModel;
import com.example.harjacober.obouncechat.viewmodel.GroupChatsViewModel;
import com.example.harjacober.obouncechat.viewmodel.UserOnlineModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mMessageRecyclerView;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList;
    private String userId;
    private EditText chatBox;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference rooteference;
    String chatId;
    String friendsOrGroupId;
    private Toolbar toolbar;
    private TextView toolbarFullName;
    private TextView toolbarIsOnline;
    private ImageView toolbarProfilePic;
    private ImageView backButton;
    private User user;
    private String textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setUpToolbar();
        chatBox = findViewById(R.id.edittext_chatbox);
        Button sendMessage = findViewById(R.id.button_chatbox_send);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        rooteference = FirebaseDatabase.getInstance().getReference();
        rooteference.child("chats").keepSynced(true);
        //tells whether the friend is online or not
        updateToolbar(SharedPreferenceUtils.retrieveCurrentFriendId(this));
        messageList = new ArrayList<>();

        mMessageRecyclerView = findViewById(R.id.reyclerview_chat_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        //Allow chats to be displayed from bottom
        manager.setStackFromEnd(true);
        mMessageRecyclerView.setHasFixedSize(true);
        mMessageRecyclerView.setLayoutManager(manager);
        mMessageAdapter = new MessageListAdapter(messageList, this);
//        mMessageRecyclerView.canScrollVertically(0);
        mMessageRecyclerView.setAdapter(mMessageAdapter);

        fetchChatsFromDatabase(SharedPreferenceUtils.retrieveCurrentFriendId(this));

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chatBox.getText().toString().isEmpty()) {
                    saveChatToDatabase();
                }
            }
        });

    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.chat_toolbar);
        toolbarFullName = findViewById(R.id.tv_full_name);
        toolbarIsOnline = findViewById(R.id.isonline);
        toolbarProfilePic = findViewById(R.id.profile_pcture);
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.view_profile:
                //TODO show contact profile
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChatToDatabase() {
        /**Check whether it is a group chaf or individual chat
        by checking the current id stored in sharedPreferences
        group chat id starts with group*/
        textMessage = chatBox.getText().toString();
        chatBox.setText("");
        friendsOrGroupId = SharedPreferenceUtils.retrieveCurrentFriendId(this);
        if (!friendsOrGroupId.contains("group")) {
            int result = userId.compareTo(friendsOrGroupId);
            if (result < 0) {
                chatId = userId + friendsOrGroupId;
            } else {
                chatId = friendsOrGroupId + userId;
            }
        }else {
            chatId = friendsOrGroupId;
        }
        user = new User();
        user.setUsername(SharedPreferenceUtils.retrieveUsername(this));
        user.setProfileUrl(SharedPreferenceUtils.retrieveProfileUri(this));
        user.setThumbnail(SharedPreferenceUtils.retrieveThumbnail(this));
        user.setUserId(userId);
        user.setFullName(SharedPreferenceUtils.retrieveFullName(this));
        Message message = new Message(textMessage,
                user, System.currentTimeMillis());
        if (chatId.contains("group")){
            //Save the message under the @threads node
            rooteference.child("threads")
                    .child(SharedPreferenceUtils.retrieveCurrentFriendId(this))
                    .child("messages")
                    .push().setValue(message)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //TODO Show message as sent with a mark
                            updateBothPartiesChatNodes();
                            Toast.makeText(ChatActivity.this,
                                    "message sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO Show message as not sent with something sha
                    Toast.makeText(ChatActivity.this,
                            "message not sent", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            rooteference.child("chats").child(chatId).push().setValue(message)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //TODO Show message as sent with a mark
                            updateBothPartiesChatNodes();
                            Toast.makeText(ChatActivity.this,
                                    "message sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO Show message as not sent with something sha
                    Toast.makeText(ChatActivity.this,
                            "message not sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateBothPartiesChatNodes() {
       /** This method add this chat to the user chat list and the other user or group chat list,
        the chat contains useful information like the lastTime sent and the current sent. and the user
        that sent the message*/
       final Chats chats = new Chats();
       chats.setLastMessage(textMessage);
       chats.setCreatedAt(System.currentTimeMillis());
       if (chatId.contains("group")){
        GroupInfo groupInfo =
                SharedPreferenceUtils.retrieveCurrGroupInfo(this);
        chats.setGroupInfo(groupInfo);
           rooteference.child("users")
                   .child(userId)
                   .child("chats").child(chatId).setValue(chats).addOnSuccessListener(
                   new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           updateUnreads();
                       }
                   }
           );
//           Group group = SharedPreferenceUtils.retrieveCurrGroup(this);
           DatabaseReference reference =
                   FirebaseDatabase.getInstance().getReference()
                   .child("threads")
                   .child(SharedPreferenceUtils.retrieveCurrentFriendId(this))
                   .child("members");
           reference.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                       String id= snapshot.getKey();
                       rooteference.child("users")
                               .child(id)
                               .child("chats").child(chatId).setValue(chats).addOnSuccessListener(
                               new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       updateUnreads();
                                   }
                               }
                       );

                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });


       }else{
           /** This notifies the friend that a message has been sent by this current user
            * The details of the sender(current user) is sent to the friend*/
           user.setStatusText(SharedPreferenceUtils.retrieveSelfDesc(this));
           user.setRegistrationStatus(SharedPreferenceUtils.retrieveRegProgress(this));
           /**This sets the current user details so that it will be sent to the friend
           */
           chats.setUser(user);
           //This tells the friend this message was received from this current user
           chats.setMessageType("received");
           rooteference.child("users")
                   .child(SharedPreferenceUtils.retrieveCurrentFriendId(this))
                   .child("chats").child(chatId).setValue(chats).addOnSuccessListener(
                   new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           updateUnreads();
                       }
                   }
           );
           //TODO set the chat user to the friends data
           chats.setUser(SharedPreferenceUtils
           .retrieveFrndUserObject(this));
           chats.setMessageType("sent");
           rooteference.child("users")
                   .child(userId).child("chats")
                   .child(chatId).setValue(chats).addOnSuccessListener(
                   new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           updateUnreads();
                       }
                   }
           );
       }

    }

    private void updateUnreads() {
        rooteference.child("users")
                .child(userId).child("chats")
                .child(chatId).child("unreads")
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        int unreads = mutableData.getValue(Integer.class);
                        mutableData.setValue(unreads + 1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });
    }

    public void fetchChatsFromDatabase(String id){
        LiveData<List<Message>> listLiveData;
        //TODO Replace the .equals argument with a sample groupId
        if (id.contains("group")){
            GroupChatsViewModel model =
                    ViewModelProviders.of(this)
                            .get(GroupChatsViewModel.class);
            listLiveData =
                    model.getMessageListLiveData();

        }else {
            ChatsViewModel model =
                    ViewModelProviders.of(this)
                            .get(ChatsViewModel.class);
            listLiveData =
                    model.getListLiveData();
        }
        listLiveData.observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable List<Message> messages) {
                messageList.clear();
                if (messages != null) {
                    messageList = messages;
                }
                mMessageAdapter.update(messageList);
            }
        });
    }

    public void updateToolbar(String id){
        if (id.contains("group")){
            GroupChatsViewModel model =
                    ViewModelProviders.of(this)
                    .get(GroupChatsViewModel.class);
            LiveData<GroupInfo> infoLiveData =
                    model.getGroupInfoLiveData();
            infoLiveData.observe(this, new Observer<GroupInfo>() {
                @Override
                public void onChanged(@Nullable GroupInfo groupInfo) {
                    if (groupInfo != null){
                        Picasso.get()
                                .load(groupInfo.getGroupThumnail())
                                .into(toolbarProfilePic);
                        toolbarFullName.setText(groupInfo.getGroupName());
                        toolbarIsOnline.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }else {
            UserOnlineModel model = ViewModelProviders.of(this)
                    .get(UserOnlineModel.class);
            LiveData<User> userLiveData = model.getUserLiveData();
            userLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    Picasso.get()
                            .load(user.getThumbnail())
                            .into(toolbarProfilePic);
                    if (user.isOnline()) {
                        toolbarFullName.setText(user.getFullName());
                        toolbarIsOnline.setText("Online");
                    } else {
                        toolbarFullName.setText(user.getFullName());
                        toolbarIsOnline.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
}
