package com.example.android2mediaplayer;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    SongsArray songs ;
    Gson gson = new Gson();
    int counter = 1;
    boolean isRunning = false;
    Button button1,button2,button3,camera,gallery;
    EditText songName,songLink;
    ImageView pic;
    ImageButton playBtn;
    Bitmap imageBitmap;
    String encoded = null;
    SharedPreferences sp;


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        String jsonSave = gson.toJson(songs);
        editor.putString("SCORE_TEST", jsonSave);
        editor.commit();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        camera = (Button)findViewById(R.id.camera);
        gallery = (Button)findViewById(R.id.gallery);

        songName = (EditText)findViewById(R.id.Song_Name);
        songLink = (EditText)findViewById(R.id.Song_Link);

        playBtn = (ImageButton)findViewById(R.id.play);
        pic = (ImageView)findViewById(R.id.Pic);
        sp = getSharedPreferences("HIGH_SCORE",MODE_PRIVATE);

        if(sp.contains("SCORE_TEST")){
            String json = sp.getString("SCORE_TEST", "");
            songs = gson.fromJson(json, SongsArray.class);
        }
        else{
            songs = new SongsArray();
        }
        //final EditText link1Et = findViewById(R.id.link_1);
        //final EditText link2Et = findViewById(R.id.link_2);
        //final EditText link3Et = findViewById(R.id.link_3);


        //String link1 = link1Et.getText().toString();
        //String link2 = link2Et.getText().toString();
        //String link3 = link3Et.getText().toString();

        //Song song1 = new Song("song " + String.valueOf(counter++), link1);
        //Song song2 = new Song("song " + String.valueOf(counter++), link2);
        //Song song3 = new Song("song " + String.valueOf(counter++), link3);

        //songs.add(song1);
        //songs.add(song2);
        //songs.add(song3);

        Song_Adapter songAdapter = new Song_Adapter(songs.getList(),getBaseContext());
        songAdapter.setListener(new Song_Adapter.MyCountryListener() {
            @Override
            public void onCountryClicked(int position, View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                View viewTemp = layoutInflaterAndroid.inflate(R.layout.song_dialog, null);
                builder.setView(viewTemp);
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                viewTemp.findViewById(R.id.return_to_main).setOnClickListener(l -> alertDialog.dismiss());
                TextView text = viewTemp.findViewById(R.id.line);
                text.setText(songs.getName(position));
                ImageView img = viewTemp.findViewById(R.id.imgDialog);


                byte[] imageAsBytes = Base64.decode(songs.getSong(position).getEnc().getBytes(), Base64.DEFAULT);
                Bitmap temp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                Glide.with(getBaseContext()).load(temp).into(img);


            }

            @Override
            public void onCountryLongClicked(int position, View view) {

            }
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                //songs.getList().remove(viewHolder.getAdapterPosition());
                //songAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                //songAdapter.notify();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                View viewTemp = layoutInflaterAndroid.inflate(R.layout.delete_box, null);
                builder.setView(viewTemp);
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                viewTemp.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        songAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                viewTemp.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        songs.getList().remove(viewHolder.getAdapterPosition());
                        songAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        alertDialog.dismiss();

                    }
                });
                //TextView text = viewTemp.findViewById(R.id.line);
                //text.setText(songs.getName(position));
                //ImageView img = viewTemp.findViewById(R.id.imgDialog);

            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //return false;
                Collections.swap(songs.getList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                //songAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                songAdapter.notifyDataSetChanged();
                return true;

            }


            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /**
                if(isLongPressDragEnabled()){
                    return makeMovementFlags(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP |ItemTouchHelper.START|ItemTouchHelper.END );

                }
                else{
                    return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                }
                //return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                */
                return makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP |ItemTouchHelper.START|ItemTouchHelper.END );

            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerView.setAdapter(songAdapter);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsonSave = gson.toJson(songs);
                Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
                intent.putExtra("list", jsonSave);
                intent.putExtra("command", "new_instance");
                if(!isRunning) {
                    if (songs.getList().size() > 0) {
                        isRunning = true;
                        startService(intent);
                        playBtn.setImageResource(R.drawable.pause);
                    }
                }
                else{
                    stopService(intent);
                    isRunning = false;
                    playBtn.setImageResource(R.drawable.play);
                }
            }
        });

        Button buttonAdd = findViewById(R.id.addID);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(encoded == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                    View viewTemp = layoutInflaterAndroid.inflate(R.layout.check_pic, null);
                    builder.setView(viewTemp);
                    builder.setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    viewTemp.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                }
                else{
                    String link = songLink.getText().toString();
                    Song song = new Song(songName.getText().toString(), link,encoded);
                    songs.add(song);
                    songAdapter.notifyDataSetChanged();

                }
                }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.syntax.org.il/xtra/bob.m4a";
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.p1);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 1, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedTemp = Base64.encodeToString(b, Base64.DEFAULT);

                Song song = new Song("Bob 1", link,encodedTemp);
                songs.add(song);
                songAdapter.notifyDataSetChanged();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.syntax.org.il/xtra/bob2.mp3";
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.p2);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 1, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedTemp = Base64.encodeToString(b, Base64.DEFAULT);

                Song song = new Song("Bob 2", link,encodedTemp);
                songs.add(song);
                songAdapter.notifyDataSetChanged();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.syntax.org.il/xtra/bob1.m4a";
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.p3);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 1, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedTemp = Base64.encodeToString(b, Base64.DEFAULT);

                Song song = new Song("Bob 3", link,encodedTemp);
                songs.add(song);
                songAdapter.notifyDataSetChanged();
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, 1);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent takeGallery = new Intent(MediaStore.GA)
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encoded = Base64.encodeToString(b, Base64.DEFAULT);
            pic.setImageBitmap(imageBitmap);
            Glide.with(this).load(imageBitmap).into(pic);
        }
        //if(requestCode == 2)
        if (resultCode == RESULT_OK && requestCode == 100) {
            Uri imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encoded = Base64.encodeToString(b, Base64.DEFAULT);
            pic.setImageBitmap(imageBitmap);
            Glide.with(this).load(imageBitmap).into(pic);

        }
        }



}
