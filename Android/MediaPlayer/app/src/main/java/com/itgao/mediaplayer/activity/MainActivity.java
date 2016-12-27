package com.itgao.mediaplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itgao.mediaplayer.PlayerService;
import com.itgao.mediaplayer.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    SeekBar seekBar;
    TextView curTime,totalTime;
    TextView title;

    private ServiceConnection sc;
    private PlayerService playerService;
    private boolean isStop;
    private double totalTimeInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(PlayerService.MFILTER);
        registerReceiver(new MusicReceiver(),filter);
        sc = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                playerService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                playerService = ((PlayerService.MBinder)service).getService();//1

            }
        };
        Button previous = (Button) findViewById(R.id.previous);
        Button next = (Button) findViewById(R.id.next);
        Button stop = (Button) findViewById(R.id.stop);
        Button stopService = (Button) findViewById(R.id.stopService);
        seekBar = (SeekBar) findViewById(R.id.mSeekbar);
        curTime = (TextView) findViewById(R.id.curTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
        title = (TextView) findViewById(R.id.title);

        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        stop.setOnClickListener(this);
        stopService.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous:
                playerService.playPrevious();//2
                break;
            case R.id.next:
                playerService.playNext();
                break;
            case R.id.stop:
                if (isStop) {
                    playerService.restart();
                }
                else {
                    playerService.parse();
                }
                isStop = !isStop;
                break;
            case R.id.stopService:
                Intent intent = new Intent("com.intent.musicplayer.MusicService");
                unbindService(sc);
                stopService(intent);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Intent intent = new Intent("com.intent.musicplayer.MusicService");
        bindService(intent,sc,Context.BIND_AUTO_CREATE);//当然你可以用startService的方式启动服务，这样结束了activity以后并不会结束service

    }

    private String transferMilliToTime(int millis){
        DateFormat format = new SimpleDateFormat("mm:ss");
        String result = format.format(new Date(millis));
        return result;
    }
    private class MusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(PlayerService.CURTIME,0)!=0) {
                double curTimeInt = intent.getIntExtra(PlayerService.CURTIME,0);
                curTime.setText(transferMilliToTime((int)curTimeInt));
                double result = curTimeInt/totalTimeInt*100;
                seekBar.setProgress((int) Math.floor(result));

            }
            else if(intent.getIntExtra(PlayerService.TOTALTIME,0)!=0) {
                totalTimeInt = intent.getIntExtra(PlayerService.TOTALTIME,0);
                totalTime.setText(transferMilliToTime((int)(totalTimeInt)));
            }
            else if (!TextUtils.isEmpty(intent.getStringExtra(PlayerService.NAME))) {
                title.setText(intent.getStringExtra(PlayerService.NAME));
            }
        }
    }
}
