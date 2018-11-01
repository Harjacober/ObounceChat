package com.example.harjacober.obouncechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harjacober.obouncechat.App;
import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.Chats;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.ui.ChatActivity;
import com.example.harjacober.obouncechat.utils.DateAndTimeUtils;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.GroupObjectHolder> {

    List<Chats> chatsList;
    Context context;
    ListItmeClickedListener listener;
    public interface ListItmeClickedListener{
        void onProfilePicClickedListener(String thumbnail,
                                         String profileUrl, String fullName);
    }

    public ChatsListAdapter(List<Chats> chatsList,
                            Context context,
                            ListItmeClickedListener listener) {
        this.chatsList = chatsList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupObjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_chats_list, viewGroup, false);
        return new GroupObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupObjectHolder groupObjectHolder, int position) {
        Chats chats = chatsList.get(position);
        if (chats != null) {
            groupObjectHolder.bind(chats);
        }

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class GroupObjectHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView fullName;
        TextView lastMessage;
        TextView timeStamp;
        TextView unreadMessages;
        ImageView unreadMessHolder;
        public GroupObjectHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.tv_full_name);
            lastMessage = itemView.findViewById(R.id.last_conversation);
            profilePic = itemView.findViewById(R.id.circleImageView);
            timeStamp = itemView.findViewById(R.id.last_mess_timestamp);
            unreadMessages = itemView.findViewById(R.id.unread_messages);
            unreadMessHolder = itemView.findViewById(R.id.unread_message_holder);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    if (chatsList.get(getAdapterPosition()).getUser() != null) {
                        SharedPreferenceUtils.saveCurrentFriendId(
                                context,
                                chatsList.get(getAdapterPosition())
                                        .getUser().getUserId());
                    }else {
                        SharedPreferenceUtils.saveCurrentFriendId(
                                context,
                                chatsList.get(getAdapterPosition())
                                        .getGroupInfo().getGroupId());
                        SharedPreferenceUtils.saveCurrentGroupInfo(
                                context,
                                chatsList.get(getAdapterPosition())
                                        .getGroupInfo()
                        );
                    }
                    context.startActivity(intent);
                }
            });
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatsList.get(getAdapterPosition()).getUser() != null) {
                        listener.onProfilePicClickedListener(chatsList.get(getAdapterPosition())
                                        .getUser().getThumbnail(),
                                chatsList.get(
                                        getAdapterPosition())
                                        .getUser().getProfileUrl(),
                                chatsList.get(getAdapterPosition()).getUser().getFullName());
                    }else{
                        listener.onProfilePicClickedListener(chatsList.get(getAdapterPosition())
                                        .getGroupInfo().getGroupThumnail(),
                                chatsList.get(
                                        getAdapterPosition())
                                        .getGroupInfo().getGroupProfileUrl(),
                                chatsList.get(getAdapterPosition()).getUser().getFullName());
                    }
                }
            });
        }
        public void bind(Chats chats){
            lastMessage.setText(chats.getLastMessage());
//            unreadMessages.setText(chats.getUnreads());
            timeStamp.setText(DateAndTimeUtils.formatDateTime(chats.getCreatedAt()));
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

            if (chats.getGroupInfo() != null) {
                fullName.setText(chats.getGroupInfo().getGroupName());
                if (!chats.getGroupInfo().getGroupThumnail().isEmpty()) {
                    App.picassoWithCache.get().load(chats
                            .getGroupInfo().getGroupThumnail())
                            .into(target);
                } else {
                    profilePic.setImageDrawable(
                            context.getResources()
                                    .getDrawable(R.drawable.ic_person_white_24dp));
                }

            }else if (chats.getUser() != null){
                fullName.setText(chats.getUser().getFullName());
                if (!chats.getUser().getThumbnail().isEmpty()) {
                    Picasso.get().load(chats
                            .getUser().getThumbnail())
                            .into(target);
                    setMessageHolderBackground();
                } else {
                    profilePic.setImageDrawable(
                            context.getResources()
                                    .getDrawable(R.drawable.ic_person_white_24dp));
                }
            }
        }

        private void setMessageHolderBackground() {
            boolean isOnline = chatsList.get(getAdapterPosition())
                    .getUser().isOnline();
            int unreads = chatsList.get(getAdapterPosition()).getUnreads();
            if (unreads > 0 && isOnline){
                unreadMessHolder.setBackgroundColor(Color.GREEN);
            }else if (unreads > 0 && !isOnline){
                unreadMessHolder.setBackgroundColor(Color.GRAY);
            }else if (unreads == 0 && isOnline){
                unreadMessHolder.setBackgroundColor(Color.BLUE);
            }else if (unreads == 0 && !isOnline){
                unreadMessHolder.setBackgroundColor(Color.GRAY);
            }
        }
    }

    public void update(List<Chats> list){
        chatsList = list;
        notifyDataSetChanged();
    }
}
