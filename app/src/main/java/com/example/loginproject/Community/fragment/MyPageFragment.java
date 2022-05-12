package com.example.loginproject.Community.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Community.CommunityActivity;
import com.example.loginproject.Community.adapter.CommentAdapter;
import com.example.loginproject.Community.adapter.FeedAdapter;
import com.example.loginproject.Community.model.CommentDTO;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPageFragment extends Fragment{
    Context context;

    TextView profileText;
    Button btnProfileFeed;
    Button btnProfileComment;
    TextView profileTabName;

    ArrayList<CommentDTO> commentList;

    ArrayList<FeedDTO> dtoList;

    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    CommentAdapter commentAdapter;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    String userName;
    String userIdToken;

    public MyPageFragment(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        userName = CommunityActivity.getUserName();
        userIdToken = CommunityActivity.getUserIdToken();

        profileText = (TextView) view.findViewById(R.id.profileIdText);
        btnProfileFeed = (Button) view.findViewById(R.id.btnProfileFeed);
        btnProfileComment = (Button) view.findViewById(R.id.btnProfileComment);
        profileTabName = (TextView) view.findViewById(R.id.profileTabName);

        profileText.setText(userName);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("DB");

        recyclerView = view.findViewById(R.id.mypage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        dtoList = new ArrayList<>();
        commentList = new ArrayList<>();

        btnProfileFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Query myQuery = databaseReference.child("Feeds").orderByChild("userID").equalTo(userIdToken);

                myQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dtoList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            FeedDTO feed = snapshot.getValue(FeedDTO.class);
                            dtoList.add(feed);
                            profileTabName.setText("내가 쓴 게시글");
                        }
                        feedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                feedAdapter = new FeedAdapter(dtoList);
                recyclerView.setAdapter(feedAdapter);
                if(dtoList.size() == 0){
                    profileTabName.setText("내가 쓴 글 없음");
                }else{


                }
            }
        });

        btnProfileComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Query myQuery = databaseReference.child("Comments").orderByChild("comment_userID").equalTo(userIdToken);

                myQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            CommentDTO comment = snapshot.getValue(CommentDTO.class);
                            commentList.add(comment);
                            profileTabName.setText("내가 쓴 댓글");
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                commentAdapter = new CommentAdapter(commentList);
                recyclerView.setAdapter(commentAdapter);

                if(commentList.size() == 0){
                    profileTabName.setText("내가 쓴 댓글 없음");
                }else{

                }
            }
        });

        return view;
    }

}