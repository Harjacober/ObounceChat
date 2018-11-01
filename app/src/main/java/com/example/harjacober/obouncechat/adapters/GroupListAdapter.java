package com.example.harjacober.obouncechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harjacober.obouncechat.App;
import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.Group;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.ui.ChatActivity;
import com.example.harjacober.obouncechat.utils.SharedPreferenceUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupObjectHolder> {

    List<GroupInfo> infoList;
    List<Group> groupList;
    Context context;
    ListItmeClickedListener listener;
    public interface ListItmeClickedListener{
        void onProfilePicClickedListener(String thumbnail,
                                         String profileUrl, String fullName);
    }

    public GroupListAdapter(List<GroupInfo> infoList,
                              List<Group> groupList,
                              Context context,
                              ListItmeClickedListener listener) {
        this.infoList = infoList;
        this.context = context;
        this.listener = listener;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupObjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_contacts_list, viewGroup, false);
        return new GroupObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupObjectHolder groupObjectHolder, int position) {
        GroupInfo groupInfo = infoList.get(position);
        groupObjectHolder.bind(groupInfo);

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class GroupObjectHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView fullName;
        TextView statusText;
        public GroupObjectHolder(@NonNull View itemView) {
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
                            infoList.get(getAdapterPosition())
                                    .getGroupId());
                    SharedPreferenceUtils.saveCurrentGroupInfo(
                            context,
                            infoList.get(getAdapterPosition())
                    );
                    SharedPreferenceUtils.saveCurrentGroup(
                            context,
                            groupList.get(getAdapterPosition())
                    );
                    context.startActivity(intent);
                }
            });
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProfilePicClickedListener(infoList.get(getAdapterPosition()).getGroupThumnail(),
                            infoList.get(
                                    getAdapterPosition())
                                    .getGroupProfileUrl(),
                            infoList.get(getAdapterPosition()).getGroupName());
                }
            });
        }
        public void bind(GroupInfo groupInfo){
            fullName.setText(groupInfo.getGroupName());
            statusText.setText(groupInfo.getGrouPurpose());
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

            if (!groupInfo.getGroupThumnail().isEmpty()) {
                App.picassoWithCache.get().load(groupInfo
                        .getGroupThumnail())
                        .into(target);
            }else {
                profilePic.setImageDrawable(
                        context.getResources()
                                .getDrawable(R.drawable.ic_person_white_24dp));
            }
        }
    }

    public void update(List<GroupInfo> list,
                       List<Group> groups){
        infoList = list;
        groupList = groups;
        notifyDataSetChanged();
    }
}
