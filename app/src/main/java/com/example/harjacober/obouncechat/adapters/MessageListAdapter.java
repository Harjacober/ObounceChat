package com.example.harjacober.obouncechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harjacober.obouncechat.App;
import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.Message;
import com.example.harjacober.obouncechat.utils.DateAndTimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messageList;
    private Context context;
    private static final int VIEW_TYPE_MESSAGE_SENT = 83730;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 97786;

    public MessageListAdapter(
            List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            View view = LayoutInflater.from(
                    viewGroup.getContext()).inflate(
                    R.layout.item_message_received, viewGroup, false);
            return new ReceivedMessageHolder(view);
        }else if (viewType == VIEW_TYPE_MESSAGE_SENT){
            View view = LayoutInflater.from(
                    viewGroup.getContext()).inflate(
                    R.layout.item_message_sent, viewGroup, false);
            return new SentMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Message message = messageList.get(position);
        switch (viewHolder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {

        return messageList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Message message = messageList.get(position);
        if (message.getSender().getUserId().equals(user.getUid())){
            //if message was sent ny user
            return VIEW_TYPE_MESSAGE_SENT;
        }else{
            //if message was received
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText, nameText;
        CircleImageView profileImage;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }
        void bind(final Message message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateAndTimeUtils
                    .formatDateTime(message.getCreatedAt()));
            nameText.setText(message.getSender().getUsername());

            // Insert the profile image from the URL into the ImageView.
            String path =
                    message
                            .getSender()
                            .getThumbnail();
            if (!path.isEmpty()) {
                App.picassoWithCache.get().load(path)
                        .into(profileImage);
            }else {
                profileImage.setImageDrawable(
                        context.getResources()
                        .getDrawable(R.drawable.ic_person_partwhite_24dp));
            }
        }
    }
    public class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }
        void bind(Message message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateAndTimeUtils.formatDateTime(message.getCreatedAt()));
        }
    }

    public void update(List<Message> list) {
        messageList = list;
        notifyDataSetChanged();
    }

}
