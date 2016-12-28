package com.itgao.mediaplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itgao.mediaplayer.R;
import com.itgao.mediaplayer.adapter.MusicListAdapter;
import com.itgao.mediaplayer.domain.Mp3Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ListView mMusicList;
    // private SimpleAdapter mAdapter;
    public static final int PLAY_MSG = 1;		//播放
    public static final int PAUSE_MSG = 2;		//暂停
    public static final int STOP_MSG = 3;		//停止
    public static final int CONTINUE_MSG = 4;	//继续
    public static final int PRIVIOUS_MSG = 5;	//上一首
    public static final int NEXT_MSG = 6;		//下一首
    public static final int PROGRESS_CHANGE = 7;//进度改变
    public static final int PLAYING_MSG = 8;	//正在播放

    private List<Mp3Info> mp3Infos = null;
    // private SimpleAdapter mAdapter; // 简单适配器
    MusicListAdapter listAdapter; // 改为自定义列表适配器
    private Button previousBtn; // 上一首
    private Button repeatBtn; // 重复（单曲循环、全部循环）
    private Button playBtn; // 播放（播放、暂停）
    private Button shuffleBtn; // 随机播放
    private Button nextBtn; // 下一首
    private TextView musicTitle;// 歌曲标题
    private TextView musicDuration; // 歌曲时间
    private Button musicPlaying; // 歌曲专辑
    private ImageView musicAlbum; // 专辑封面
    private Button music_button;

    private int repeatState; // 循环标识
    private final int isCurrentRepeat = 1; // 单曲循环
    private final int isAllRepeat = 2; // 全部循环
    private final int isNoneRepeat = 3; // 无重复播放
    private boolean isFirstTime = true;
    private boolean isPlaying; // 正在播放
    private boolean isPause; // 暂停
    private boolean isNoneShuffle = true; // 顺序播放
    private boolean isShuffle = false; // 随机播放

    private int listPosition = 0; // 标识列表位置
    private HomeReceiver homeReceiver; // 自定义的广播接收器
    // 一系列动作
    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION"; // 更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION"; // 控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT"; // 当前音乐改变动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION"; // 音乐时长改变动作
    public static final String REPEAT_ACTION = "com.wwj.action.REPEAT_ACTION"; // 音乐重复改变动作
    public static final String SHUFFLE_ACTION = "com.wwj.action.SHUFFLE_ACTION"; // 音乐随机播放动作

    private int currentTime; // 当前时间
    private int duration; // 时长



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        repeatState = isNoneRepeat;
        homeReceiver = new HomeReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        filter.addAction(REPEAT_ACTION);
        filter.addAction(SHUFFLE_ACTION);
        registerReceiver(homeReceiver,filter);
    }

    public void initView(){
        mMusicList = (ListView) findViewById(R.id.music_list);
        mMusicList.setOnItemClickListener(new MusicListItemClickListener());
        mp3Infos = getMp3Infos(this);
        // setListAdapter(getMp3Infos(this));
        Log.v("msg",mp3Infos.size()+"");
        listAdapter = new MusicListAdapter(this,mp3Infos);
        mMusicList.setAdapter(listAdapter);

        previousBtn = (Button) findViewById(R.id.previous_music);
        repeatBtn = (Button) findViewById(R.id.repeat_music);
        playBtn = (Button) findViewById(R.id.play_music);
        shuffleBtn = (Button) findViewById(R.id.shuffle_music);
        nextBtn = (Button) findViewById(R.id.next_music);
        musicTitle = (TextView) findViewById(R.id.music_title);
        musicDuration = (TextView) findViewById(R.id.music_duration);
        musicPlaying = (Button) findViewById(R.id.playing);
        musicAlbum = (ImageView) findViewById(R.id.music_album);
        music_button = (Button) findViewById(R.id.music_search);


        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        previousBtn.setOnClickListener(viewOnClickListener);
        repeatBtn.setOnClickListener(viewOnClickListener);
        playBtn.setOnClickListener(viewOnClickListener);
        shuffleBtn.setOnClickListener(viewOnClickListener);
        nextBtn.setOnClickListener(viewOnClickListener);
        musicPlaying.setOnClickListener(viewOnClickListener);
        music_button.setOnClickListener(viewOnClickListener);
    }

    private class ViewOnClickListener implements View.OnClickListener{
        Intent intent = new Intent();

        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.music_search:
                    Intent act_intent = new Intent(MainActivity.this,SearchActivity.class);
                    startActivity(act_intent);
                    break;
                case R.id.previous_music: // 上一首
                    playBtn.setBackgroundResource(R.drawable.play_selector);
                    isFirstTime = false;
                    isPlaying = true;
                    isPause = false;
                    previous();
                    break;
                case R.id.repeat_music: // 重复
                    if(repeatState == isNoneRepeat){ // 为顺序 则为单次循环
                        repeat_one();
                        shuffleBtn.setClickable(false);
                        repeatState = isCurrentRepeat;
                    }else if (repeatState == isCurrentRepeat){ // 为当前 则为全部循环
                        repeat_all();
                        shuffleBtn.setClickable(false);
                        repeatState = isAllRepeat;
                    }else if (repeatState == isAllRepeat){
                        repeat_none();
                        shuffleBtn.setClickable(true);
                        repeatState = isNoneRepeat;
                    }
                    switch (repeatState){
                        case isCurrentRepeat:
                            repeatBtn.setBackgroundResource(R.drawable.repeat_current_selector);
                            Toast.makeText(MainActivity.this,R.string.repeat_current,Toast.LENGTH_LONG).show();
                            break;
                        case isAllRepeat:
                            repeatBtn.setBackgroundResource(R.drawable.repeat_all_selector);
                            Toast.makeText(MainActivity.this,R.string.repeat_all,Toast.LENGTH_LONG).show();
                            break;
                        case isNoneRepeat:
                            repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
                            Toast.makeText(MainActivity.this,R.string.repeat_none,Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case R.id.play_music:
                    if(isFirstTime){ // 第一次播放
                        play();
                        isFirstTime = false;
                        isPlaying = true;
                        isPause = false;
                    }else { // 非第一次
                        if(isPlaying){ // 正在播放时又按了播放 暂停
                            playBtn.setBackgroundResource(R.drawable.pause_selector);
                            intent.setAction("com.wwj.media.MUSIC_SERVICE");
                            intent.putExtra("MSG",PAUSE_MSG);
                            startService(intent);
                            isPlaying = false;
                            isPause = true;
                        }else if(isPause){ // 暂停 继续播放
                            playBtn.setBackgroundResource(R.drawable.play_selector);
                            intent.setAction("com.wwj.media.MUSIC_SERVICE");
                            intent.putExtra("MSG",CONTINUE_MSG);
                            startService(intent);
                            isPause = false;
                            isPlaying = true;
                        }
                    }
                    break;
                case R.id.shuffle_music: // 随机播放按钮
                    if(isNoneShuffle){  // 原先不是随机播放 开始随机播放
                        shuffleBtn.setBackgroundResource(R.drawable.shuffle_selector);
                        Toast.makeText(MainActivity.this,R.string.shuffle,Toast.LENGTH_LONG).show();
                        isNoneShuffle = false;
                        isShuffle = true;
                        shuffleMusic();
                        repeatBtn.setClickable(false);
                    }else if(isShuffle){
                        shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
                        Toast.makeText(MainActivity.this,R.string.shuffle_none,Toast.LENGTH_LONG).show();
                        isShuffle = false;
                        isNoneShuffle = true;
                        repeatBtn.setClickable(true);
                    }
                    break;
                case R.id.next_music:
                    playBtn.setBackgroundResource(R.drawable.play_selector);
                    isFirstTime = false;
                    isPlaying = true;
                    isPause = false;
                    next();
                    break;
                case R.id.playing: // 正在播放 转到歌词界面
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                    intent.putExtra("title", mp3Info.getTitle());
                    intent.putExtra("url", mp3Info.getUrl());
                    intent.putExtra("artist", mp3Info.getArtist());
                    intent.putExtra("listPosition", listPosition);
                    intent.putExtra("currentTime", currentTime);
                    intent.putExtra("duration", duration);
                    intent.putExtra("MSG", PLAYING_MSG);
                    startActivity(intent);
                    break;
            }
        }
    }


    public static List<Mp3Info> getMp3Infos(Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        for(int i = 0;i<cursor.getCount();i++){
            Mp3Info mp3Info = new Mp3Info();
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));              //文件路径
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if(isMusic != 0){
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
            }
        }

        return mp3Infos;
    }

    public void setListAdapter(List<Mp3Info> mp3Infos){
        List<HashMap<String,String>> mapList = new ArrayList<HashMap<String,String>>();
        for(Iterator iterator = mp3Infos.iterator();iterator.hasNext();){
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mapList.add(map);
        }

    }


    private class MusicListItemClickListener implements AdapterView.OnItemClickListener{
        // 点击列表播放音乐
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            listPosition = i;
            playMusic(listPosition);
        }
    }

    public void next(){
        listPosition = listPosition + 1;
        if(listPosition <= mp3Infos.size() - 1){
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition",listPosition);
            intent.putExtra("url",mp3Info.getUrl());
            intent.putExtra("MSG",NEXT_MSG);
            startService(intent);
        }else {
            Toast.makeText(MainActivity.this,"没有了",Toast.LENGTH_LONG).show();
        }
    }

    public void previous(){
        listPosition = listPosition - 1;
        if (listPosition >= 0){
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition",listPosition);
            intent.putExtra("url",mp3Info.getUrl());
            intent.putExtra("MSG",PRIVIOUS_MSG);
            startService(intent);
        }else {
            Toast.makeText(MainActivity.this,"没有了",Toast.LENGTH_LONG).show();
        }
    }

    public void play(){
        playBtn.setBackgroundResource(R.drawable.play_selector);
        Mp3Info mp3Info = mp3Infos.get(listPosition);
        musicTitle.setText(mp3Info.getTitle());
        musicDuration.setText(formatTime(mp3Info.getDuration()));
        Intent intent = new Intent();
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("listPosition", 0);
        intent.putExtra("url", mp3Info.getUrl());
        Log.v("url",mp3Info.getUrl());
        intent.putExtra("MSG", PLAY_MSG);
        startService(intent);
    }
    // 播放样式 循环单次 随机之类的
    public void repeat_one(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 1);
        sendBroadcast(intent);
    }

    public void repeat_all(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 2);
        sendBroadcast(intent);
    }

    public void repeat_none(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 3);
        sendBroadcast(intent);
    }
    public void shuffleMusic(){
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 4);
        sendBroadcast(intent);
    }

    public void playMusic(int listPosition) {
        if (mp3Infos != null) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle()); // 这里显示标题
            //    Bitmap bitmap = MediaUtil.getArtwork(this, mp3Info.getId(),
            //            mp3Info.getAlbumId(), true, true);// 获取专辑位图对象，为小图
            //    musicAlbum.setImageBitmap(bitmap); // 这里显示专辑图片
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class); // 定义Intent对象，跳转到PlayerActivity
            // 添加一系列要传递的数据
            intent.putExtra("title", mp3Info.getTitle());
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("artist", mp3Info.getArtist());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("currentTime", currentTime);
            intent.putExtra("repeatState", repeatState);
            intent.putExtra("shuffleState", isShuffle);
            intent.putExtra("MSG", PLAY_MSG);
            startActivity(intent);
        }
    }
    public class HomeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MUSIC_CURRENT)){
                currentTime = intent.getIntExtra("currentTime",-1);
                musicDuration.setText(formatTime(currentTime));
            } else if (action.equals(MUSIC_DURATION)){
                duration = intent.getIntExtra("duration",-1);
            }else if (action.equals(UPDATE_ACTION)){
                listPosition = intent.getIntExtra("current",-1);
                if(listPosition >= 0){
                    musicTitle.setText(mp3Infos.get(listPosition).getTitle());
                }
            }else if (action.equals(REPEAT_ACTION)){
                repeatState = intent.getIntExtra("repeatSate",-1);
                switch (repeatState){
                    case isCurrentRepeat:
                        repeatBtn.setBackgroundResource(R.drawable.repeat_current_selector);
                        shuffleBtn.setClickable(false);
                        break;
                    case isAllRepeat:
                        repeatBtn.setBackgroundResource(R.drawable.repeat_all_selector);
                        shuffleBtn.setClickable(false);
                        break;
                    case isNoneRepeat:
                        repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
                        shuffleBtn.setClickable(true);
                        break;
                }
            }else if (action.equals(SHUFFLE_ACTION)){
                isShuffle = intent.getBooleanExtra("shuffleState",false);
                if(isShuffle){
                    isNoneShuffle = false;
                    shuffleBtn.setBackgroundResource(R.drawable.shuffle_selector);
                    repeatBtn.setClickable(false);
                }else {
                    isNoneShuffle = true;
                    shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
                    repeatBtn.setClickable(true);
                }
            }
        }
    }


    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }
}
