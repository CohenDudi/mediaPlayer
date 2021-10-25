package com.example.android2mediaplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Song_Adapter extends RecyclerView.Adapter<Song_Adapter.PersonViewHolder>   {
    private List<Song> Persons;
    private MyCountryListener listener;
    private Context context;

    interface MyCountryListener {
        void onCountryClicked(int position,View view);
        void onCountryLongClicked(int position,View view);
    }

    public void setListener(MyCountryListener listener) {
        this.listener = listener;
    }


    public Song_Adapter(List<Song> Persons,Context context) {

        this.Persons = Persons;
        this.context = context;
    }


    public class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imgIv;


        public PersonViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.personName);
            imgIv = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener!=null)
                        listener.onCountryClicked(getAdapterPosition(),view);
                }
            });
        }


    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        PersonViewHolder personViewHolder = new PersonViewHolder(view);
        return personViewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        Song person = Persons.get(position);
        holder.name.setText(person.getName());
        byte[] imageAsBytes = Base64.decode(person.getEnc().getBytes(), Base64.DEFAULT);
        Bitmap temp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        Glide.with(this.context).load(temp).into(holder.imgIv);
    }

    @Override
    public int getItemCount() {
        return Persons.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


}
