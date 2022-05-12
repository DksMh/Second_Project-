package com.example.loginproject.Community.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginproject.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private ArrayList<Uri> photoList;
    private ArrayList<Uri> updatePhotoList;
    TextView photoNum;


    public PhotoAdapter(ArrayList<Uri> list, TextView photoNum){
        photoList = list;
        this.photoNum = photoNum;


    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_photo, parent, false);
        PhotoViewHolder viewHolder = null;

            viewHolder = new PhotoViewHolder(context, view, photoList, this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {

        Glide.with(holder.context).load(photoList.get(position)).into(holder.imgItem);

    }

    @Override
    public int getItemCount() {
        if (photoList != null) {
            return photoList.size();
        } else {
            return 0;
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgItem;
        public ImageButton btnImgDelete;
        Context context;

        public PhotoViewHolder(Context context, @NonNull View itemView, ArrayList<Uri> itemList, PhotoAdapter adapter) {
            super(itemView);
            this.context = context;
            imgItem = itemView.findViewById(R.id.photoImg);
            btnImgDelete = itemView.findViewById(R.id.btnphotoDelete);

            btnImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        photoList.remove(pos);
                        photoNum.setText(photoList.size()+"/4");
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        public PhotoViewHolder(@NonNull View itemView, ArrayList<Uri> uriList, PhotoAdapter adapter) {
            super(itemView);

            imgItem = itemView.findViewById(R.id.photoImg);
            btnImgDelete = itemView.findViewById(R.id.btnphotoDelete);

            btnImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        photoList.remove(pos);
                        photoNum.setText(uriList.size()+"/4");
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
