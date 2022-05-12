package com.example.loginproject.Community.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Community.DetailActivity;
import com.example.loginproject.Community.model.CommentDTO;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ArrayList<CommentDTO> commentList;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseAuth mFirebaseAuth;     // 파이어베이스 계정 가져오기
    FirebaseUser firebaseUser;      // 현재 로그인 한 유저

    String userName, userIdToken;

    public CommentAdapter(ArrayList<CommentDTO> list){

        commentList =list;

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        userName = firebaseUser.getEmail();
        userIdToken = firebaseUser.getUid();

        userName = userName.substring(0,userName.indexOf('@'));

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.comment,parent,false);

        String str = context.getClass().toString();

        String className = str.substring(str.lastIndexOf('.')+1);

        CommentViewHolder viewHolder = new CommentViewHolder(context, view, commentList, className);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        if(getItemCount()!= 0) {
            CommentDTO dto = commentList.get(position);
            holder.commentUserID.setText(dto.getComment_user());
            holder.commentText.setText(dto.getComment_text());
            holder.commentTime.setText(dto.getDate().substring(0,16));

            if(dto.getComment_userID().equals(userIdToken)){
                holder.btnCommentMenu.setVisibility(View.VISIBLE);
            }else{
                holder.btnCommentMenu.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(commentList!= null) {
            return commentList.size();
        }else{
            return 0;
        }
    }



    public class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView commentUserID;
        public TextView commentText;
        public TextView commentTime;
        public ImageButton btnCommentMenu;

        String className;

        public CommentViewHolder(Context context, @NonNull View itemView, ArrayList<CommentDTO> itemList, String className) {
            super(itemView);

            commentUserID = itemView.findViewById(R.id.commentUserID);
            commentText = itemView.findViewById(R.id.commentText);
            commentTime = itemView.findViewById(R.id.commentTime);
            btnCommentMenu = itemView.findViewById(R.id.btnCommentMenu);

            this.className = className;

            if(className.equals("CommunityActivity")) {
                btnCommentMenu.setVisibility(View.INVISIBLE);
            }else{
                btnCommentMenu.setVisibility(View.VISIBLE);
            }

            btnCommentMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        final PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), view);
                        ((Activity)view.getContext()).getMenuInflater().inflate(R.menu.comment_menu, popupMenu.getMenu());


                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId() == R.id.comment_update){

                                    CommentDTO comment = itemList.get(pos);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("댓글 수정");
                                    builder.setMessage("수정할 메세지를 입력해주세요!");
                                    final EditText editComment = new EditText(context);
                                    editComment.setText(comment.getComment_text());

                                    builder.setView(editComment);

                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            database = FirebaseDatabase.getInstance();
                                            databaseReference = database.getReference().child("DB");

                                            String newText = editComment.getText().toString();
                                            comment.setComment_text(newText);

                                           databaseReference.child("Comments").child(comment.getNo()+"").setValue(comment);

                                        }
                                    });

                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "수정 취소되었습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    builder.show();

                                }else if(item.getItemId() == R.id.comment_delete){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("삭제");
                                    builder.setMessage("삭제하시겠습니까?");

                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            database = FirebaseDatabase.getInstance();
                                            databaseReference = database.getReference().child("DB");

                                            databaseReference.child("Comments").child(itemList.get(pos).getNo()+"").setValue(null);
                                            Toast.makeText(context, "삭제됨",Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "취소되었습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    builder.show();

                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                }
            });


            if(className.equals("CommunityActivity")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){

                            CommentDTO dto = itemList.get(pos);

                            String feedNo = dto.getFeed_no()+"";

                            // feed_no로 feed 객체 생성

                            database = FirebaseDatabase.getInstance();
                            databaseReference = database.getReference().child("DB");

                            databaseReference.child("Feeds").child(feedNo).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    FeedDTO feed = snapshot.getValue(FeedDTO.class);

                                    if(feed != null) {
                                        Intent intent = new Intent(context, DetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("dto", feed);
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.w("Data Read Error", "ProfilePage Comment Click Error : "+error.getMessage());
                                }
                            });

                        }
                    }

                });
            }
        }

        public void redirect(Context context){
            Intent intent = ((Activity)context).getIntent();
            ((Activity)context).finish(); //현재 액티비티 종료 실시
            ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
            ((Activity)context).startActivity(intent); //현재 액티비티 재실행 실시
            ((Activity)context).overridePendingTransition(0, 0); //효과 없애기
            Toast.makeText(context, "수정되었습니다.",Toast.LENGTH_SHORT).show();
        }

        public boolean commentDelete(CommentDTO item){
            int num = item.getNo();

//            String query = "delete from comment where no = "+num;
//            sqlDB = myHelper.getWritableDatabase();
//            sqlDB.execSQL(query);
//            sqlDB.close();

            return true;
        }

        public boolean commentUpdate(CommentDTO item){
            String query = "update comment set comment_text = '"+item.getComment_text()+"' where no ="+item.getNo();
//            sqlDB = myHelper.getWritableDatabase();
//            sqlDB.execSQL(query);
//            sqlDB.close();

            return true;
        }


        public String dateForm(String date){
            long mNow = System.currentTimeMillis();
            Date format = null;
            String result = "방금 전";

            try {
                format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(format != null) {
                long diffSec = (mNow - format.getTime()) / 1000; //초 차이
                long diffMin = (mNow - format.getTime()) / 60000; //분 차이
                long diffHor = (mNow - format.getTime()) / 3600000; //시 차이
                long diffDays = (mNow / (24 * 60 * 60)/1000) - (format.getTime()/(24*60*60)/1000);

                if(diffDays != 0){
                    result = diffDays+"일 전";
                    return result;
                }

                if(diffDays==0 && diffHor != 0){
                    result = diffHor + "시간 전";
                    return result;
                }

                if(diffDays==0 && diffHor == 0 && diffMin != 0){
                    result = diffMin + "분 전";
                    return result;
                }
            }
            return result;
        }
    }
}


