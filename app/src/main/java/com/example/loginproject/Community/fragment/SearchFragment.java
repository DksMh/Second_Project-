package com.example.loginproject.Community.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Community.adapter.FeedAdapter;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class SearchFragment extends Fragment {
    Context context;

    Button btnSearch;

    EditText searchText;
    TextView resultText;

    ArrayList<FeedDTO> dtoList;

    RecyclerView recyclerView;
    FeedAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    InputMethodManager imm;

    RadioGroup radioGroup;
    public SearchFragment( Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("DB");

        dtoList = new ArrayList<>();

        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        searchText = (EditText) view.findViewById(R.id.searchText);
        resultText = (TextView) view.findViewById(R.id.resultText);
        radioGroup = view.findViewById(R.id.search_radioGroup);

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        recyclerView = view.findViewById(R.id.search_rectclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        adapter = new FeedAdapter(dtoList);
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchText.getText().toString();
                ArrayList<FeedDTO> feedList = new ArrayList<>();

                if(!search.equals("")){

                    RadioButton rb = (RadioButton)view.findViewById(radioGroup.getCheckedRadioButtonId());
                    if(!rb.getText().toString().equals("전체")) {

                        int feedType = ((rb.getText().toString()).equals("운동") ? 2 : 1);

                        Query myQuery = databaseReference.child("Feeds").orderByChild("feedType").equalTo(feedType);
                        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dtoList.clear();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    FeedDTO feed = snapshot.getValue(FeedDTO.class);
                                    if(feed.getTitle().contains(search) || feed.getMainText().contains(search)){
                                        dtoList.add(feed);
                                    }
                                }
                                Collections.sort(dtoList);
                                Collections.sort(dtoList,Collections.reverseOrder());
                                adapter.notifyDataSetChanged();
                                if(dtoList.size() == 0){
                                    resultText.setText("'"+rb.getText().toString() +"' 피드 중 '"+search+"'의 내용을 포함하는 검색 결과 없음");
                                }else{
                                    resultText.setText("'"+rb.getText().toString() +"' 피드 중 '"+search+"'의 내용을 포함하는 검색 결과");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else{
                        databaseReference.child("Feeds").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dtoList.clear();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    if(! (snapshot.getValue() instanceof Long)){
                                        FeedDTO feed = snapshot.getValue(FeedDTO.class);
                                        if(feed.getTitle().contains(search) || feed.getMainText().contains(search)){
                                            dtoList.add(feed);
                                        }

                                    }
                                }
                                Collections.sort(dtoList);
                                Collections.sort(dtoList,Collections.reverseOrder());
                                adapter.notifyDataSetChanged();
                                if(dtoList.size() == 0){
                                    resultText.setText("'"+search+"'의 내용을 포함하는 검색 결과 없음");
                                }else{
                                    resultText.setText("'"+search+"'의 내용을 포함하는 검색 결과");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("검색 오류");
                    builder.setMessage("검색할 내용을 입력해 주세요!");

                    builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create().show();
                }
            }
        });

        return view;
    }

}