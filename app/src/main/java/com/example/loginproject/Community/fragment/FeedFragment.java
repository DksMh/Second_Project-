package com.example.loginproject.Community.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Community.InsertActivity;
import com.example.loginproject.Community.adapter.FeedAdapter;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FeedFragment extends Fragment {
    Context context;

    ImageButton btnNewFeed;

    ArrayList<FeedDTO> feedList;

    RecyclerView recyclerView;
    FeedAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference databaseReference;


    public FeedFragment(){

    }

    public FeedFragment(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.feed_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        btnNewFeed = (ImageButton)view.findViewById(R.id.btnNewFeed);

        feedList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("DB").child("Feeds");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(! (snapshot.getValue() instanceof Long)){
                        FeedDTO feed = snapshot.getValue(FeedDTO.class);
                        feedList.add(feed);
                    }
                }
                Collections.sort(feedList);
                Collections.sort(feedList,Collections.reverseOrder());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("feed List Read Error : "+String.valueOf(error.toException()));
            }
        });

        adapter = new FeedAdapter(feedList);
        recyclerView.setAdapter(adapter);


        btnNewFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}