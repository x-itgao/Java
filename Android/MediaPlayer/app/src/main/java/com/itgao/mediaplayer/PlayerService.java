package com.itgao.mediaplayer;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerService extends Service {

    private List<File> musicList;
    private MediaPlayer player;
    private int curPage;
    public static final String MFILTER = "broadcast.intent.action.text";
    public static final String NAME = "name";
    public static final String TOTALTIME = "totaltime";
    public static final String CURTIME = "curtime";


    public class MBinder extends Binder{//2
        public PlayerService getService(){
            return PlayerService.this;
        }
        public MediaPlayer getPlayer(){
            return player;
        }
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        musicList = new ArrayList<File>();
        File rootDir = Environment.getExternalStorageDirectory();//3
        Log.d("rootname", rootDir.getName());
        Log.d("rootname", rootDir.getAbsolutePath());
        fillMusicList(rootDir);
        Log.d("musiclist", String.valueOf(musicList.size()));
        player = new MediaPlayer();
        if (musicList.size() != 0) {
            startPlay();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                player.reset();
                curPage = curPage == musicList.size() - 1 ? (curPage + 1) % musicList.size() : curPage + 1;
                startPlay();
            }
        });
    }
    /*迭代获取 音乐 文件*/
    private void fillMusicList(File dir){
        File[] sourceFiles = dir.listFiles();
        Log.d("长度",String.valueOf(sourceFiles.length));
        for(File file : sourceFiles){
            if (file.isDirectory()) {
                Log.d("文件夹名称",String.valueOf(file.getName()));
//    if (!file.getName().equals("lost+found")) {
                fillMusicList(file);
//    }

            }
            else {
                String name = file.getName();
                Log.d("childname",file.getName());
                if (name.endsWith(".mp3")||name.endsWith(".acc")) {//支持的格式
                    musicList.add(file);
                }
            }
        }
    }

    private void startPlay(){
        mSendBroadCast(NAME,musicList.get(curPage).getName());//4
        try {
            player.setDataSource(musicList.get(curPage).getAbsolutePath());
            player.prepare();
            player.start();
            player.getDuration();
            mSendBroadCast(TOTALTIME,player.getDuration());
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mSendBroadCast(CURTIME,player.getCurrentPosition());
                }
            },0,1000);

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void playNext(){
        curPage = curPage==musicList.size()-1? (curPage+1)%musicList.size() : curPage+1;
        Log.d("curpage",String.valueOf(curPage));
        player.reset();
        startPlay();
    }
    public void playPrevious(){
        curPage = curPage==0? 0 : curPage-1;
        Log.d("curpage",String.valueOf(curPage));
        player.reset();
        startPlay();
    }
    public void parse(){
        player.pause();
    }
    public void restart(){
        player.start();
    }
    private void mSendBroadCast(String key, String value){
        Intent intent = new Intent(MFILTER);
        intent.putExtra(key,value);//发送广播
        sendBroadcast(intent);
    }

    private void mSendBroadCast(String key, int value){
        Intent intent = new Intent(MFILTER);
        intent.putExtra(key,value);//发送广播
        sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MBinder();
    }


}


















    /*
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String path;
    private boolean isPause = false;

    public PlayerService() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer.isPlaying()){
            stop();
        }
        path = intent.getStringExtra("url");
        int msg = intent.getIntExtra("MSG",0);
        if(msg == 1){
            play(0);
        }else if(msg == 2){
            pause();
        }else if (msg == 3){
            stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void play(int position){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;
        }
    }

    public void stop(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop之后如果要再次通过start启动，需要调用prepare函数
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public final class PreparedListener implements MediaPlayer.OnPreparedListener{
        private int position;

        public PreparedListener(int position){
            this.position = position;
        }
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            if(position > 0){
                mediaPlayer.seekTo(position);
            }
        }
    } */

