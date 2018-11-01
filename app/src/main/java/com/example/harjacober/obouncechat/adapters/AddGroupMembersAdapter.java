package com.example.harjacober.obouncechat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.data.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMembersAdapter extends RecyclerView.Adapter<AddGroupMembersAdapter.GroupObjectHolder> {

    public interface onCreateCLicked {

        void onMenuItemClicked(ArrayList<User> selectedList);
    }
    private final onCreateCLicked dialogCreated;
    List<User> usersList;
    Context context;
    private boolean isHighlighted = false;
    private boolean multiSelect = false;
    private ArrayList<User> selectedItems = new ArrayList<>();
    private ArrayList<User> selectedItems2 = new ArrayList<>();

    public AddGroupMembersAdapter(onCreateCLicked dialogCreated, List<User> infoList,
                                  Context context) {
        this.dialogCreated = dialogCreated;
        this.usersList = infoList;
        this.context = context;
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
        User user = usersList.get(position);
        groupObjectHolder.bind(user);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class GroupObjectHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePic;
        TextView fullName;
        TextView statusText;

        public GroupObjectHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.tv_full_name);
            statusText = itemView.findViewById(R.id.status);
            profilePic = itemView.findViewById(R.id.circleImageView);
        }

        private ActionMode.Callback callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                multiSelect = true;
                MenuInflater menuInflater = new MenuInflater(context);
                menuInflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_add:
                        dialogCreated.onMenuItemClicked(selectedItems2);
                        actionMode.finish();
                        isHighlighted = false;
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                multiSelect = false;
                isHighlighted = false;

                selectedItems.clear();
                notifyDataSetChanged();
            }
        };


        void selectItem(User item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    selectedItems2.remove(item);
                    itemView.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    selectedItems2.add(item);
                    itemView.setBackgroundColor(Color.parseColor("#37966F"));
                }
            }
        }

        void bind(final User user) {
            fullName.setText(user.getFullName());
            statusText.setText(user.getStatusText());
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    profilePic.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get().load(user
                    .getThumbnail())
                    .into(target);
            if (selectedItems.contains(user)) {
                itemView.setBackgroundColor(Color.parseColor("#37966F"));
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    isHighlighted = true;
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(callback);
                    if (selectedItems.size() < 200) {
                        selectItem(user);
                    } else {
                        Toast.makeText(context,
                                "You can't have more than 200 in a group",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isHighlighted) {
                        if (selectedItems.size() < 200) {
                            selectItem(user);
                        } else {
                            Toast.makeText(context,
                                    "You can't have more than 200 in a group",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }
    public void update(List<User> list) {
        usersList = list;
        notifyDataSetChanged();
    }
}
