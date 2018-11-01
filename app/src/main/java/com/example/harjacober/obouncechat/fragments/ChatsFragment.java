package com.example.harjacober.obouncechat.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.harjacober.obouncechat.R;
import com.example.harjacober.obouncechat.adapters.ChatsListAdapter;
import com.example.harjacober.obouncechat.data.Chats;
import com.example.harjacober.obouncechat.viewmodel.ChatsListViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.harjacober.obouncechat.fragments.CallsFragments.displayImageInDialog;

public class ChatsFragment extends Fragment
implements ChatsListAdapter.ListItmeClickedListener{
    private List<Chats> chatList;
    private RecyclerView mrecyclerView;
    private ChatsListAdapter adapter;
    private LinearLayout emptyView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        emptyView = view.findViewById(R.id.empty_view);
        chatList = new ArrayList<>();
        mrecyclerView = view.findViewById(R.id.chats_recycler_view);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatsListAdapter(chatList,
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
    public void fetchDataFromFirebase(){
        ChatsListViewModel userViewModel =
                ViewModelProviders.of(this)
                        .get(ChatsListViewModel.class);
        LiveData<List<Chats>> allGroupLivedata = userViewModel.getAllChatsLivedata();
        allGroupLivedata.observe(this, new Observer<List<Chats>>() {
            @Override
            public void onChanged(@Nullable List<Chats> list) {
                chatList.clear();
                if (list != null){
                    emptyView.setVisibility(View.INVISIBLE);
                    mrecyclerView.setVisibility(View.VISIBLE);
                    chatList = list;
                    adapter.update(chatList);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
                    mrecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
