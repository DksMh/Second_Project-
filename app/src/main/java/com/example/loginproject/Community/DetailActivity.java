package com.example.loginproject.Community;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginproject.Community.adapter.CommentAdapter;
import com.example.loginproject.Community.model.CommentDTO;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    FeedDTO dto;

    InputMethodManager imm;

    Button btnNewComment;
    EditText newCommnetText;

    RecyclerView detailCommentRecyclerView;
    CommentAdapter commentAdapter;
    ArrayList<CommentDTO> commentList;

    RecyclerView recyclerView;
    MyRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Uri> photoList;

    FirebaseAuth mFirebaseAuth;     // 파이어베이스 계정 가져오기
    FirebaseUser firebaseUser;      // 현재 로그인 한 유저

    FirebaseDatabase database ;
    DatabaseReference databaseReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailpage);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        Intent intent = getIntent();
        dto = (FeedDTO) intent.getSerializableExtra("dto");

        btnNewComment = (Button)findViewById(R.id.btnNewComment);
        newCommnetText = (EditText)findViewById(R.id.newCommnetText);
        recyclerView = findViewById(R.id.detail_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        photoList = new ArrayList<>();

        detailCommentRecyclerView = findViewById(R.id.detailCommentRecyclerView);
        detailCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        // 이미지 로드
        if(dto.getImageList()!= null){

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child(dto.getUserID());
            if(storageReference == null){
                Toast.makeText(DetailActivity.this, "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
            }else{
                for(int i =0; i<dto.getImageList().size(); i++) {
                    final int index;
                    index = i;
                    System.out.println(dto.getImageList().get(index));
                    StorageReference loadImageUrl = storageReference.child(dto.getImageList().get(index));
                    System.out.println(loadImageUrl);
                    loadImageUrl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            photoList.add(uri);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }

                    });
                }
                recyclerViewAdapter = new MyRecyclerViewAdapter(photoList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("DB");

        commentList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("상세보기");

        TextView title = (TextView) findViewById(R.id.detalTitle);
        TextView mainText = (TextView) findViewById(R.id.detailMainText);
        TextView dateText = (TextView) findViewById(R.id.detailDateText);

        title.setText(dto.getTitle());
        mainText.setText(dto.getMainText());
        dateText.setText(dto.getDate().substring(0,10));


        Query myQuery = databaseReference.child("Comments").orderByChild("feed_no").equalTo(dto.getNo());

        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CommentDTO comment = snapshot.getValue(CommentDTO.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("feed List Read Error : "+String.valueOf(error.toException()));
            }
        });

        commentAdapter = new CommentAdapter(commentList);
        detailCommentRecyclerView.setAdapter(commentAdapter);



        btnNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newCommnetText.getText().toString().equals("")){

                    databaseReference.child("Comments").child("Sequence").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(!task.isSuccessful()){
                                System.out.println("실패");
                            }else{

                                long seq = (Long)task.getResult().getValue();
                                CommentDTO comment = new CommentDTO((int)seq, CommunityActivity.getUserName() , CommunityActivity.getUserIdToken(),newCommnetText.getText().toString(), dto.getUserID(), dto.getNo(), currentTime());
                                databaseReference.child("Comments").child(seq+"").setValue(comment);
                                databaseReference.child("Comments").child("Sequence").setValue(seq +1);
                                imm.hideSoftInputFromWindow(newCommnetText.getWindowToken(), 0);
                                newCommnetText.setText("");
                            }
                        }
                    });


                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setTitle("오류");
                    builder.setMessage("댓글을 입력해 주세요!");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                }
            }
            });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(CommunityActivity.getUserIdToken().equals(dto.getUserID())){
        getMenuInflater().inflate(R.menu.detailmenu, menu);
        }
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.detail_menuUpdate:
                    Intent intent = new Intent(DetailActivity.this, InsertActivity.class);
                    intent.putExtra("dto",dto);
                    startActivity(intent);
                    finish();
                return true;
            case R.id.detail_menuDelete:

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(dto.getImageList() != null) {
                            for (int i = 0; i < dto.getImageList().size(); i++) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                // 스토리지 레퍼런스 주소 수정
                                StorageReference storageRef = storage.getReference().child(dto.getUserID() + "/" + dto.getImageList().get(i));


                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        System.out.println("삭제 성공");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        System.out.println("삭제 실패");
                                    }
                                });
                            }
                        }
                        if(commentList != null || commentList.size() != 0) {
                            for (int i = 0; i < commentList.size(); i++) {
                                databaseReference.child("Comments").child(commentList.get(i).getNo() + "").setValue(null);

                                System.out.println(commentList.get(i).getNo() +" 삭제");
                            }
                        }
                        databaseReference.child("Feeds").child(dto.getNo()+"").setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();
                            }
                        });

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DetailActivity.this, "취소되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.create().show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String currentTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = sdf.format(date);
        return getTime;
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>{
        ArrayList<Uri> imageList;

        public MyRecyclerViewAdapter(ArrayList<Uri> list){
            imageList = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.detail_photo, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view, imageList);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if(getItemCount() != 0){
                Glide.with(DetailActivity.this).load(imageList.get(position)).into(holder.image);
            }
        }

        @Override
        public int getItemCount() {
            if(imageList!= null) {
                return imageList.size();
            }else{
                return 0;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public ImageView image;

            public MyViewHolder(@NonNull View itemView, ArrayList<Uri> list){
                super(itemView);

                image = itemView.findViewById(R.id.detail_photoItem);

            }
        }
    }
}
