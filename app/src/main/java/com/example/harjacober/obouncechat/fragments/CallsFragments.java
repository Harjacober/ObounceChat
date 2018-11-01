package com.example.harjacober.obouncechat.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.harjacober.obouncechat.App;
import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.adapters.FriendsListAdapter;
import com.example.harjacober.obouncechat.data.User;
import com.example.harjacober.obouncechat.utils.Blur;
import com.example.harjacober.obouncechat.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallsFragments extends Fragment
implements FriendsListAdapter.ListItmeClickedListener{
    private RecyclerView mrecyclerView;
    private FriendsListAdapter adapter;
    private List<User> friendsList;
    private List<String> friendId;
    private LinearLayout emptyView;

    public CallsFragments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls_fragments, container, false);
        friendsList = new ArrayList<>();
        friendId = new ArrayList<>();
        emptyView = view.findViewById(R.id.empty_view);
        mrecyclerView = view.findViewById(R.id.calls_recycler_view);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendsListAdapter(friendsList,
                getContext(),
                this);
        mrecyclerView.setAdapter(adapter);

        fetchDataFromFirebase();
        return view;
    }

    @Override
    public void onProfilePicClickedListener(String thumbnail,
                                            String profileUrl,
                                            String fullName) {
        displayImageInDialog(thumbnail,
                profileUrl,
                fullName,
                getContext(),
                getActivity());
    }

    public static void displayImageInDialog(final String thumbnail,
                                            final String profileUrl,
                                            final String name,
                                            final Context context,
                                            Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final Rect rect = new Rect();
        Window window = activity.getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(rect);

        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(
                R.layout.image_dialog, null);
        view.setMinimumWidth (rect.width());
        builder.setView(view);

        //Get instance of all views
        final LinearLayout linearLayout = view.findViewById(R.id.dia_app_bar);
        final ImageView profileImage = view.findViewById(R.id.dia_profile_picture);
        ImageView backButton = view.findViewById(R.id.dia_app_bar_back);
        final TextView fullName = view.findViewById(R.id.dia_tv_full_name);
        final TextView appBarFullName = view.findViewById(R.id.dia_app_bar_full_name);
        final ProgressBar loadIndicator = view.findViewById(R.id.dia_load_indicator);
        final RelativeLayout rootlayout = view.findViewById(R.id.dia_root_layout);
        final View background = view.findViewById(R.id.view);
        loadIndicator.setVisibility(View.VISIBLE);
        fullName.setText(name);

        final Transformation blurTransformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap blurred = Blur.fastblur(context, source, 10);
                source.recycle();
                return blurred;
            }

            @Override
            public String key() {
                return "blur()";
            }
        };
        //load blurr image into dialog
        App.picassoWithCache.get().load(thumbnail)
                .transform(blurTransformation)
        .into(profileImage, new Callback() {
            @Override
            public void onSuccess() {
                //load clearer image into dialog
                App.picassoWithCache.get()
                        .load(profileUrl)
                        .placeholder(profileImage.getDrawable())
                        .into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                loadIndicator.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
            }

            @Override
            public void onError(Exception e) {
            }
        });

        //let the dialog fit the screen when image is clicked
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarFullName.setText(name);
                linearLayout.setVisibility(View.VISIBLE);
                fullName.setVisibility(View.INVISIBLE);
                background.setVisibility(View.INVISIBLE);
                //TODO let the dialog fit the screen
                view.setMinimumWidth(rect.width());
                view.setMinimumHeight (rect.height());
                builder.setView(view);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void fetchDataFromFirebase(){
        UserViewModel userViewModel =
                ViewModelProviders.of(this)
                .get(UserViewModel.class);
        LiveData<List<User>>  userLiveData = userViewModel.getUserLiveData();
        userLiveData.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> userList) {
                friendsList.clear();
                if (userList != null){
                    emptyView.setVisibility(View.INVISIBLE);
                    mrecyclerView.setVisibility(View.VISIBLE);
                    friendsList = userList;
                    adapter.update(friendsList);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
                    mrecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
