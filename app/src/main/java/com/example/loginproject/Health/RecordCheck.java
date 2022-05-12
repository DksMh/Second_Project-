package com.example.loginproject.Health;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class RecordCheck extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;     // 파이어베이스 계정 가져오기
    FirebaseUser firebaseUser;      // 현재 로그인 한 유저
    DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    ListView userRunData;
    ArrayList<String> midList;
    final String items[] = {"걷기","자전거"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_check);

        // listview
        userRunData = (ListView) findViewById(R.id.listview);
        // initializing our array list
        midList = new ArrayList<String>();

        AlertDialog.Builder mmBuilder = new AlertDialog.Builder(this);
        mmBuilder.setTitle("원하시는 기록을 선택하세요");
        int mmSelectedNum = 0;
        mmBuilder.setSingleChoiceItems(items, mmSelectedNum, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                if(items[i] == items[0] ){
                    initializeListView();
                    System.out.println("걷기를 선택했습니다.");
                }else{
                    initializeListView2();
                    System.out.println("자전거를 선택했습니다.");
                }
                Toast.makeText(RecordCheck.this, items[i]+"를 눌렀습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // 누르면 바로 닫히는 형태
            }
        }).setCancelable(false); // 밖에 창 눌러지 안꺼지게
        mmBuilder.create();
        mmBuilder.show();

    }
    // 걷기 기록 가져오기
    private void initializeListView() {
        // creating a new array adapter for our list view
        // 기존에 있는 layout말고 customlayout으로 바꿈
        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, midList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecordCheck.this, R.layout.records_layout, midList);

        // below line is used for getting reference
        // of our Firebase Database.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        // in below line we are calling method for add child event
        // listener to get the child of our database.
        mDatabaseRef.child("Record").orderByChild("idToken").equalTo(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when new child is added to
                // our data base and after adding new child
                // we are adding that item inside our array list and
                // notifying our adapter that the data in adapter is changed.
                Record record = snapshot.getValue(Record.class);


                if(record.getType() == 1){
                    midList.add("\t\t"+record.getDate()+"\t\t\t\t\t\t\t"+record.getNum()+"m\t\t\t\t\t\t\t"+record.getKcal()+"Kcal");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when the new child is added.
                // when the new child is added to our list we will be
                // notifying our adapter that data has changed.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // below method is called when we remove a child from our database.
                // inside this method we are removing the child from our array list
                // by comparing with it's value.
                // after removing the data we are notifying our adapter that the
                // data has been changed.
                midList.remove(snapshot.getValue(Record.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when we move our
                // child in our database.
                // in our code we are note moving any child.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // this method is called when we get any
                // error from Firebase with error.
            }
        });
        // below line is used for setting
        // an adapter to our list view.
        userRunData.setAdapter(adapter);
    }


    // 자전거 기록 가져오기
    private void initializeListView2() {
        // creating a new array adapter for our list view
        // 기존에 있는 layout말고 customlayout으로 바꿈
        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, midList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecordCheck.this, R.layout.records_layout, midList);

        // below line is used for getting reference
        // of our Firebase Database.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        // in below line we are calling method for add child event
        // listener to get the child of our database.
        mDatabaseRef.child("Record").orderByChild("idToken").equalTo(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when new child is added to
                // our data base and after adding new child
                // we are adding that item inside our array list and
                // notifying our adapter that the data in adapter is changed.
                Record record = snapshot.getValue(Record.class);


                if(record.getType() == 2){
                    midList.add("\t\t"+record.getDate()+"\t\t\t\t\t\t\t"+record.getNum()+"m\t\t\t\t\t\t\t"+record.getKcal()+"Kcal");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when the new child is added.
                // when the new child is added to our list we will be
                // notifying our adapter that data has changed.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // below method is called when we remove a child from our database.
                // inside this method we are removing the child from our array list
                // by comparing with it's value.
                // after removing the data we are notifying our adapter that the
                // data has been changed.
                midList.remove(snapshot.getValue(Record.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when we move our
                // child in our database.
                // in our code we are note moving any child.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // this method is called when we get any
                // error from Firebase with error.
            }
        });
        // below line is used for setting
        // an adapter to our list view.
        userRunData.setAdapter(adapter);
    }




}