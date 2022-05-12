package com.example.loginproject.Community.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Community.DetailActivity;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private ArrayList<FeedDTO> itemList;

    public FeedAdapter(ArrayList<FeedDTO> list){
        itemList =list;

    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.feeditem_list,parent,false);

        FeedViewHolder viewHolder = new FeedViewHolder(context, view, itemList);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {

        if(getItemCount()!= 0) {
            FeedDTO dto = itemList.get(position);
            holder.title.setText(dto.getTitle());
            holder.userNameText.setText(dto.getUserName());
            holder.feedTime.setText(holder.dateForm(dto.getDate()));
            if(dto.getFeedType() == 2){
                holder.feedType.setText("운동");
            }else {
                holder.feedType.setText("맛집");
            }
        }
    }

    @Override
    public int getItemCount() {
        if(itemList!= null) {
            return itemList.size();
        }else{
            return 0;
        }
    }


    class FeedViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView userNameText;
        public TextView feedTime;
        public TextView feedType;

        public FeedViewHolder(Context context, @NonNull View itemView, ArrayList<FeedDTO> itemList) {
            super(itemView);

            title = itemView.findViewById(R.id.titleText);
            userNameText = itemView.findViewById(R.id.userNameText);
            feedTime = itemView.findViewById(R.id.dateText);
            feedType = itemView.findViewById(R.id.feedTypeText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        Intent intent = new Intent(context, DetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("dto",itemList.get(pos));
                        context.startActivity(intent);
                    }

                }

            });
        }

        public String dateForm(String date){
            long mNow = new Date().getTime();
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


