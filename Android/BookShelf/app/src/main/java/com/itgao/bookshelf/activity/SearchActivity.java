package com.itgao.bookshelf.activity;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private TextView test;
    private EditText edit_search;
    private ImageButton button_search;
    private ListView listView;
    NovelDB novelDB = NovelDB.getInstance(this);
    NovelAdapter adapter;
    List<Novel> novels = new ArrayList<Novel>();
    /**
     * 从笔趣阁获取各种信息专用正则表达式
     * findNovelUrl 小说主页的url
     * findChaptersUrl 小说章节的url 可以获取当页所有的章节url
     * findLatestChapter 最新章节
     * findImgUrl 小说的封面
     */



    private final String findNovelUrl = "window.location=\'(.*?)\'\"";
    public static final String findChaptersUrl = "<dd>.*?<a.*?href=\"(.*?)\">(.*?)</a>";

    public static final String findLatestChapter = "<p>.*?最新章节.*?<a.*?>(.*?)</a>";
    private final String uri = "http://zhannei.baidu.com/cse/search?s=287293036948159515&q=";


    private String novelName;
    private File cache;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    adapter = new NovelAdapter(SearchActivity.this,R.layout.novel_list_item,novels);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    test.setText(msg.obj.toString());
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        init_view();
        listen();


    }

    /**
     *  初始化view
     */
    public void init_view(){
        edit_search = (EditText) findViewById(R.id.edit_search);
        button_search = (ImageButton) findViewById(R.id.button_search);
        test = (TextView) findViewById(R.id.test);
        cache = new File(Environment.getExternalStorageDirectory(),"cache");
        if(!cache.exists()){
            cache.mkdirs();
        }

        listView = (ListView) findViewById(R.id.search_list);

    }

    /**
     * 监听事件
     */
    public void listen(){

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edit_search.getText().toString())){
                    return;
                }
                novelName = edit_search.getText().toString();
                if(isSaved(novelName)){
                    Message message = new Message();
                    message.what = 2;
                    message.obj = "已经有了";
                    handler.sendMessage(message);
                    return ;
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            getNovelList(novelName);
                        }catch (Exception e){

                            e.printStackTrace();
                            Log.v("novel_namegrfg",novelName+"dsad");

                        }
                    }
                }).start();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Novel novel = novels.get(i);
                Intent intent = new Intent(SearchActivity.this,NovelActivity.class);
                intent.putExtra("novel",novel);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                        contextMenu.add(0,0,0,"加入书架");
                    }
                });
                return false;
            }
        });
    }

    public void getNovelList(String novel_name){
        try {
            String name = URLEncoder.encode(novel_name,"UTF-8");
            String url = uri + name;

            String html = new String(StreamTool.getHtml(url),"utf-8");

            // 解析网页
            Document document = Jsoup.parse(html);
            Elements items = document.getElementsByClass("result-item result-game-item");
            for(Element item : items){
                Elements img = item.getElementsByTag("img");
                String imgUrl = img.attr("src");
                String title = ReplaceTool.replaceAll(img.attr("alt"));
                Pattern pattern = Pattern.compile(findNovelUrl);
                Matcher matcher = pattern.matcher(item.toString());
                String novel_url = "";
                if(matcher.find()){
                    novel_url = matcher.group(1);
                }

                pattern = Pattern.compile("<span class=\"result-game-item-uspan\".*?>(.*?)</span>");
                matcher = pattern.matcher(item.toString());
                String new_chapter = "";
                if(matcher.find()){
                    new_chapter = matcher.group(1);
                }
                Log.v("img",imgUrl+title+new_chapter+novel_url);
                Novel novel = new Novel();
                novel.setImg_path(imgUrl);
                novel.setMax_chapter(new_chapter);
                novel.setNovel_url(novel_url);
                novel.setChapter_index(0);
                novel.setNovel_name(title);
                novel.setId(-1);
                novels.add(novel);
            }

            Message message = new Message();
            message.what = 1;
            message.obj = html;
            handler.sendMessage(message);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int mid = (int) info.id;
        switch (item.getItemId()){
            case 0:
                add2Shelf(mid);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void add2Shelf(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Novel novel = novels.get(id);
                novel.setIs_net(1);

                File file = new File(cache,UUID.randomUUID()+".jpg");
                StreamTool.getImage(novel.getNovel_url(),file);
                novel.setImg_path(file.getAbsolutePath());
                Log.v("path",novel.getImg_path());
                novelDB.saveNovel(novel);

                Log.v("msg",novel.toString());
            }
        }).start();


    }

    public boolean isSaved(String name){
        // 不想写了，先凑合着用，以后再改
        List<Novel> list = novelDB.loadAllNovels();
        for (Novel novel : list){
            Log.v("name",novel.getNovel_name());
            if(name.equals(novel.getNovel_name())){
                return true;
            }
        }
        return false;
    }



    public void onBackPressed(){
        setResult(0);
        super.onBackPressed();
    }
}
