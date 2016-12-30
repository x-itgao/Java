package com.itgao.bookshelf.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.itgao.bookshelf.R;
import com.itgao.bookshelf.adapter.NovelAdapter;
import com.itgao.bookshelf.db.NovelDB;
import com.itgao.bookshelf.model.Chapter;
import com.itgao.bookshelf.model.Novel;
import com.itgao.bookshelf.util.ReplaceTool;
import com.itgao.bookshelf.util.StreamTool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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


    private static int index_select = -1;
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

                    refresh();
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                        index_select = i;
                        contextMenu.add(0,0,0,"下载全部");
                        contextMenu.add(0,1,0,"移除");
                    }
                });
                return false;
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
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int mid = (int) info.id;
        switch (item.getItemId()){
            case 0:
                download_All(mid);
                break;
            case 1:
                // 删除
                delete(mid);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            refresh();
        }
    }

    public void download_All(int id){
        Novel novel = list.get(id);
        load_chapters(novel.getNovel_url(),novel.getId());


    }
    public void load_chapters(final String url,final int id){
        final List<Chapter> chapters = new ArrayList<Chapter>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String html = new String(StreamTool.getHtml(url),"utf-8");
                    Document document = Jsoup.parse(html);
                    Element item = document.getElementById("list");
                    Elements dd = item.getElementsByTag("a");
                    for(Element a : dd){
                        String link = "http://www.biquge.com"+a.attr("href");
                        String name = a.text();
                        Chapter chapter = new Chapter();
                        chapter.setText_url(link);
                        chapter.setNovel_id(id);
                        chapter.setNow_index(0);
                        chapter.setChapter_name(name);

                        chapters.add(chapter);
                        novelDB.saveChapter(chapter);
                    }
                    novelDB.update_net(id,0);
                    get_Novel(chapters);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void get_Novel(final List<Chapter> chapters){

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Chapter chapter:chapters){
                    try{

                        String text = chapter.getNovel();
                        if(text != null && !text.equals("")){
                            return ;
                        }

                        if(StreamTool.getHtml(chapter.getText_url()) == null){
                            Message msg = new Message();
                            msg.what = -1;
                            msg.obj = "网络异常";
                            handler.sendMessage(msg);
                            return ;
                        }
                        text = new String(StreamTool.getHtml(chapter.getText_url()),"utf-8");
                        Pattern pattern = Pattern.compile(NovelActivity.findChapterUrl);
                        Matcher matcher = pattern.matcher(text);

                        if(matcher.find()){
                            String update = "                 "+chapter.getChapter_name()+"\n\n"+ ReplaceTool.replaceAll(matcher.group(1));
                            Log.v("download",chapter.getChapter_name());
                            novelDB.update_novel(chapter.getId(),update);
                        }
                    }catch (Exception e){
                        Message msg = new Message();
                        msg.what = -1;
                        msg.obj = "网络异常";
                        handler.sendMessage(msg);
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
    public void delete(int id){
        Novel novel = list.get(id);
        novelDB.novel_delete(novel.getId());
        novelDB.chapter_delete(novel.getId());
        Log.v("delete","delete");
        refresh();
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
            getUpdating(novel);

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
    public void getUpdating(final Novel novel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String html = null;
                try {
                    html = new String(StreamTool.getHtml(novel.getNovel_url()),"utf-8");
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
                    Log.v("newkjk",now+"  "+novel.getMax_chapter());
                    // 现在先不做判断，全部更新一波
                //    now_novel = novel;
                    novel.setMax_chapter(now);
                //    now_novel.setMax_chapter(now);
                    novelDB.updateNovel(novel);

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
