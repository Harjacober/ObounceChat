package com.example.harjacober.obouncechat.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NetworkStateService extends Service {

private NetworkStateChangeReceiver networkChangeReceiver =
        new NetworkStateChangeReceiver();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class NetworkStateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DatabaseReference reference =
                    FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            reference.child("users").child(userId).child("online")
                    .setValue(isConnectedToInternet(context));
            List<String> frndsIdList =
                    SharedPreferenceUtils.retrieveAllFriendId(context);
            if (frndsIdList != null) {
                for (String friendsId : frndsIdList) {
                    String chatId;
                    int result = userId.compareTo(friendsId);
                    if (result < 0) {
                        chatId = userId + friendsId;
                    } else {
                        chatId = friendsId + userId;
                    }
                    reference.child("users")
                            .child(friendsId)
                            .child("chats").child(chatId).child("user")
                            .child("online").setValue(isConnectedToInternet(context));
                }
            }
        }

        private boolean isConnectedToInternet(Context context) {
            try {
                if (context != null) {
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    return networkInfo != null && networkInfo.isConnected();
                }
                return false;
            } catch (Exception e) {
                Log.e(com.example.harjacober.obouncechat.services.NetworkStateService.class.getName(),
                        e.getMessage());
                return false;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}
