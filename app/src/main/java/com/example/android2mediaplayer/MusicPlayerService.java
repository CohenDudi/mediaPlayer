package com.example.android2mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by erankatsav on 03/04/2018.
 */

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{
    MediaPlayer mediaPlayer = new MediaPlayer();
    RemoteViews remoteViews;
    NotificationCompat.Builder builder;

    SongsArray songs;
    ArrayList<Song> songsArray;

    int currentPlaying = 0;
    Gson gson = new Gson();
    final int NOTIF_ID = 1;

    TextView headText;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        String channelId = "channel_id";
        String channelName = "Some channel";
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(android.R.drawable.ic_media_play);
        remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);

        Intent playIntent = new Intent(this,MusicPlayerService.class);
        playIntent.putExtra("command","play");
        PendingIntent playPendingIntent  = PendingIntent.getService(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.play_btn,playPendingIntent);

        Intent pauseIntent = new Intent(this,MusicPlayerService.class);
        pauseIntent.putExtra("command","pause");
        PendingIntent pausePendingIntent  = PendingIntent.getService(this,1,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.pause_btn,pausePendingIntent);

        Intent nextIntent = new Intent(this,MusicPlayerService.class);
        nextIntent.putExtra("command","next");
        PendingIntent nextPendingIntent  = PendingIntent.getService(this,2,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.next_btn,nextPendingIntent);

        Intent prevIntent = new Intent(this,MusicPlayerService.class);
        prevIntent.putExtra("command","prev");
        PendingIntent prevPendingIntent  = PendingIntent.getService(this,3,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.prev_btm,prevPendingIntent);


        Intent closeIntent = new Intent(this,MusicPlayerService.class);
        closeIntent.putExtra("command","close");
        PendingIntent closePendingIntent  = PendingIntent.getService(this,4,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.close_media,closePendingIntent);


        builder.setContent(remoteViews);

        startForeground(NOTIF_ID,builder.build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command  = intent.getStringExtra("command");
        switch (command) {
            case "new_instance":
                if(!mediaPlayer.isPlaying()) {
                    String temp = intent.getStringExtra("list");
                    //songs = intent.getStringArrayListExtra("list");
                    songs = gson.fromJson(temp, SongsArray.class);
                    try {
                        mediaPlayer.setDataSource(songs.get(currentPlaying));
                        mediaPlayer.prepareAsync();
                        playSong(true);
                        playSong(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                remoteViews.setTextViewText(R.id.mediaTitle,songs.getName(0));
                break;
            case "play":
                if(!mediaPlayer.isPlaying())
                    mediaPlayer.start();
                break;
            case "next":
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playSong(true);

                break;
            case "prev":
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playSong(false);
                break;
            case "pause":
                if(mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;
            case "close":
                stopSelf();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playSong(boolean isNext)  {
        if(isNext) {
            currentPlaying++;
            if (currentPlaying == songs.size())
                currentPlaying = 0;
        }
        else {
            currentPlaying--;
            if(currentPlaying < 0)
                currentPlaying = songs.size() - 1;
        }
        remoteViews.setTextViewText(R.id.mediaTitle,songs.getName(currentPlaying));
        builder.setContent(remoteViews);
        startForeground(NOTIF_ID,builder.build());


        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(songs.get(currentPlaying));
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        playSong(true);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) {
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


}
