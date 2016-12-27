package com.itgao.mediaplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.itgao.mediaplayer.activity.MainActivity;
import com.itgao.mediaplayer.activity.PlayerActivity;
import com.itgao.mediaplayer.domain.Mp3Info;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer; // 媒体播放器对象
    private String path; 			// 音乐文件路径
    private int msg;				//播放信息
    private boolean isPause; 		// 暂停状态
    private int current = 0; 		// 记录当前正在播放的音乐
    private List<Mp3Info> mp3Infos;	//存放Mp3Info对象的集合
    private int status = 3;			//播放状态，默认为顺序播放
    private MyReceiver myReceiver;	//自定义广播接收器
    private int currentTime;		//当前播放进度
    private int duration;			//播放长度
    // private LrcProcess mLrcProcess;	//歌词处理
    // private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
    private int index = 0;			//歌词检索值

    //服务要发送的一些Action
    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";	//更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";		//控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";	//当前音乐播放时间更新动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//新音乐长度更新动作
    public static final String SHOW_LRC = "com.wwj.action.SHOW_LRC";			//通知显示歌词

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                if(mediaPlayer != null){
                    currentTime = mediaPlayer.getCurrentPosition();
                    Intent intent = new Intent();
                    intent.setAction(MUSIC_CURRENT);
                    intent.putExtra("currentTime",currentTime);
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(1,1000);
                }
            }
        }
    };

    public void onCreate(){
        super.onCreate();
        Log.v("service","service started");
        mediaPlayer = new MediaPlayer();
        mp3Infos = MainActivity.getMp3Infos(PlayerService.this);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(status == 1){ // 单曲循环
                    mediaPlayer.start();
                }else if (status == 2){ // 全部循环
                    current ++;
                    if(current > mp3Infos.size() - 1){
                        current = 0;
                    }
                    Intent sendIntent = new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current",current);
                    // 发送广播 BroadcastReceiver
                    sendBroadcast(sendIntent);
                    path = mp3Infos.get(current).getUrl();
                    play(0);
                }else if (status == 3){ // 顺序播放
                    current ++;
                    if(current <= mp3Infos.size()-1){
                        Intent sendIntent = new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current",current);
                        sendBroadcast(sendIntent);
                        path = mp3Infos.get(current).getUrl();
                        play(0);
                    }else {
                        mediaPlayer.seekTo(0);
                        current = 0;
                        Intent sendIntent = new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current",current);
                        sendBroadcast(sendIntent);
                    }
                }else if (status == 4){  //随机播放
                    current = getRandomIndex(mp3Infos.size()-1);
                    Intent sendIntent = new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current",current);
                    sendBroadcast(sendIntent);
                    path = mp3Infos.get(current).getUrl();
                    play(0);
                }
            }
        });
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerActivity.CTL_ACTION);
        registerReceiver(myReceiver,filter);
    }

    protected int getRandomIndex(int end){
        int index = (int)(Math.random() * end);
        return index;
    }

    public void onStart(Intent intent,int startId){
        path = intent.getStringExtra("url"); // 歌曲路径
        current = intent.getIntExtra("listPosition",-1); // 当前播放歌曲在mp3Infos的位置
        msg = intent.getIntExtra("MSG",0);
        if(msg == MainActivity.PLAY_MSG){
            play(0);
        }else if (msg == MainActivity.PAUSE_MSG){
            pause();
        }else if(msg == MainActivity.STOP_MSG){
            stop();
        }else if(msg == MainActivity.CONTINUE_MSG){
            resume();
        }else if(msg == MainActivity.PRIVIOUS_MSG){
            previous();
        }else if(msg == MainActivity.NEXT_MSG){
            next();
        }else if(msg == MainActivity.PROGRESS_CHANGE){ // 进度更新
            currentTime = intent.getIntExtra("progress",-1);
            play(currentTime);
        }else if(msg == MainActivity.PLAYING_MSG){
            handler.sendEmptyMessage(1);
        }
    }

    private void play(int currentTime){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void pause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;
        }
    }

    private void resume(){
        if(isPause){
            mediaPlayer.start();
            isPause = false;
        }
    }

    private void previous(){
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current",current);
        sendBroadcast(sendIntent);
        play(0);
    }

    private void next(){
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current",current);
        sendBroadcast(sendIntent);
        play(0);
    }

    private void stop(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener{

        private int currentTime;

        public PreparedListener(int currentTime){
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            if(currentTime > 0){
                mediaPlayer.seekTo(currentTime);

            }
            Intent intent = new Intent();
            intent.setAction(MUSIC_DURATION);
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration",duration);
            sendBroadcast(intent);
        }
    }

    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control",-1);
            switch (control){
                case 1:
                    status = 1; // 单曲循环
                    break;
                case 2:
                    status = 2;// 全部循环
                    break;
                case 3:
                    status = 3;// 顺序循环
                    break;
                case 4:
                    status = 4;// 随机循环
                    break;
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}