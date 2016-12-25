package com.itgao.bookshelf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.itgao.bookshelf.R;
import com.itgao.bookshelf.adapter.NovelAdapter;
import com.itgao.bookshelf.db.NovelDB;
import com.itgao.bookshelf.model.Novel;
import com.itgao.bookshelf.util.StreamTool;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private ImageButton create_frag_search;
    private ListView listView;
    private List<Novel> list;
    private NovelAdapter adapter;
    private NovelDB novelDB ;
    private static final int Request_Code = 0;

    // 下拉刷新实现
    private static final int REFRESH_COMPLETE = 0x110;
    private SwipeRefreshLayout mSwipeLayout;

    private String now;
    private Novel now_novel;
    private int is_to_update = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_COMPLETE:

                    break;
                case -1:
                    showToast(msg.obj.toString());
                    break;
                case 0:
                    now = msg.obj.toString();
                    now_novel.setMax_chapter(now);
                    novelDB.updateNovel(now_novel);
                    is_to_update = 1;
                    break;
                case 1:
                    refresh();
                    break;
                case 2:
                    showToast("刷新");
                    break;
            }
            mSwipeLayout.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        novelDB = NovelDB.getInstance(this);


        init_view();
        listen();
    }

    private void init_view(){
        create_frag_search = (ImageButton) findViewById(R.id.create_frag_search);
        listView = (ListView) findViewById(R.id.novel_list);
        list = novelDB.loadAllNovels();
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.blue),
                                            getResources().getColor(R.color.green),
                                            getResources().getColor(R.color.yellow),
                                            getResources().getColor(R.color.red));
        adapter = new NovelAdapter(this,R.layout.novel_list_item,list);
        listView.setAdapter(adapter);


    }
    public void listen(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Novel novel = list.get(i);
                 Intent intent = new Intent(MainActivity.this,NovelActivity.class);
                 intent.putExtra("novel",novel);
                 startActivityForResult(intent,0);
            }
        });
        create_frag_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(intent,Request_Code);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            refresh();
        }
    }

    public void refresh(){
        List<Novel> ll = novelDB.loadAllNovels();
        list.clear();
        list.addAll(ll);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        waiting();
    }

    public void waiting(){
        Message msg = new Message();
        for(Novel novel:list){
            now_novel = novel;
            getUpdating(novel.getNovel_url(),novel.getMax_chapter());

        }
        if(is_to_update == 1){
            msg.what = is_to_update;
        }else {
            msg.what = 2;
        }

        handler.sendMessage(msg);
    }

    /**
     * 获取更新
     */
    public void getUpdating(final String url,final String old){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String html = null;
                try {
                    html = new String(StreamTool.getHtml(url),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    Message error = new Message();
                    error.what = -1;
                    error.obj = "网络异常";
                    handler.sendMessage(error);
                    e.printStackTrace();
                }
                Pattern pattern = Pattern.compile(SearchActivity.findLatestChapter);
                Matcher matcher = pattern.matcher(html);
                if(matcher.find()){
                    String now = matcher.group(1);
                    // 现在先不做判断，全部更新一波
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = now;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
