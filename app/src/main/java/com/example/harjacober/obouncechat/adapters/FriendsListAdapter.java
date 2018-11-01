package com.example.harjacober.obouncechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harjacober.obouncechat.App;
import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.ui.ChatActivity;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendDataObjectHolder> {

    List<User> friendsList;
    Context context;
    ListItmeClickedListener listener;
    public interface ListItmeClickedListener{
        void onProfilePicClickedListener(String thumbnail,
                String profileUrl, String fullName);
    }

    public FriendsListAdapter(List<User> friendsList,
                              Context context,
                              ListItmeClickedListener listener) {
        this.friendsList = friendsList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendDataObjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_contacts_list, viewGroup, false);
        return new FriendDataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FriendDataObjectHolder friendDataObjectHolder,
            int positon) {
        User user = friendsList.get(positon);
        friendDataObjectHolder.bind(user);

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendDataObjectHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView fullName;
        TextView statusText;
        public FriendDataObjectHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.tv_full_name);
            statusText = itemView.findViewById(R.id.status);
            profilePic = itemView.findViewById(R.id.circleImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    SharedPreferenceUtils.saveCurrentFriendId(
                            context,
                            friendsList.get(getAdapterPosition())
                                    .getUserId());
                    SharedPreferenceUtils.saveCurrentFriendUserObject(
                            context,
                            friendsList.get(getAdapterPosition())
                    );
                    context.startActivity(intent);
                }
            });
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProfilePicClickedListener(friendsList.get(getAdapterPosition()).getThumbnail(),
                            friendsList.get(
                                    getAdapterPosition())
                                    .getProfileUrl(),
                            friendsList.get(getAdapterPosition()).getFullName());
                }
            });
        }

        public void bind(final User friends){
            fullName.setText(friends.getFullName());
            statusText.setText(friends.getStatusText());
            profilePic.setBorderColor(
                    context.getResources().getColor(
                            R.color.colorPrimary));
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    profilePic.setBorderColor(
                            Color.parseColor("#ffffff"));

                    profilePic.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            if (!friends.getThumbnail().isEmpty()) {
                App.picassoWithCache.get().load(friends
                        .getThumbnail())
                        .into(target);
            }else {
                profilePic.setImageDrawable(
                        context.getResources()
                                .getDrawable(R.drawable.ic_person_white_24dp));
            }
        }
    }
    public void update(List<User> list){
        friendsList = list;
        notifyDataSetChanged();
    }
}
