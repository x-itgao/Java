package com.itgao.bookshelf.activity;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itgao.bookshelf.R;
import com.itgao.bookshelf.db.NovelDB;
import com.itgao.bookshelf.model.Chapter;
import com.itgao.bookshelf.model.Novel;
import com.itgao.bookshelf.util.StreamTool;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private TextView test;
    private EditText edit_search;
    private ImageButton button_search;
    private TextView wait_text;
    NovelDB novelDB = NovelDB.getInstance(this);

    /**
     * 从笔趣阁获取各种信息专用正则表达式
     * findNovelUrl 小说主页的url
     * findChaptersUrl 小说章节的url 可以获取当页所有的章节url
     * findLatestChapter 最新章节
     * findImgUrl 小说的封面
     */
    private final String findNovelUrl = "window.location=\'(.*?)\'\"";
    private final String findChaptersUrl = "<dd>.*?<a.*?href=\"(.*?)\">(.*?)</a>";
    public static final String findLatestChapter = "<p>.*?最新章节.*?<a.*?>(.*?)</a>";
    private final String findImgUrl = "<img.*?src=\"(.*?)\".*?alt=\"<em>.*?</em>";
    private final String uri = "http://zhannei.baidu.com/cse/search?s=287293036948159515&q=";
    private final int RESULT_CODE = 0;

    private String novelName;
    private String chapter;
    private String imgUrl;
    private File cache;
    private int novel_id;
    private String novel_URL;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    wait_text.setText("成功收入！");
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
        setContentView(R.layout.activity_search);
        init_view();
        listen();


    }

    public void init_view(){
        edit_search = (EditText) findViewById(R.id.edit_search);
        button_search = (ImageButton) findViewById(R.id.button_search);
        wait_text = (TextView) findViewById(R.id.wait_text);
        test = (TextView) findViewById(R.id.test);
        cache = new File(Environment.getExternalStorageDirectory(),"cache");
        if(!cache.exists()){
            cache.mkdirs();
        }
    }

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
                wait_text.setText("等待片刻");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            get_novel(novelName);
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }catch (Exception e){

                            e.printStackTrace();
                            Log.v("novel_namegrfg",novelName+"dsad");

                        }
                    }
                }).start();

            }
        });
    }
    public boolean isSaved(String name){
        // 不想写了，先凑合着用，以以后再改
        List<Novel> list = novelDB.loadAllNovels();
        for (Novel novel : list){
            Log.v("name",novel.getNovel_name());
            if(name.equals(novel.getNovel_name())){
                return true;
            }
        }
        return false;
    }
    public void get_novel(String novel_name){
        parse_info(novel_name);
    }

    public void parse_info(String novel_name){
        String name = null;
        Log.v("url",uri);
        try {
            name = URLEncoder.encode(novel_name,"UTF-8");

            String url = uri + name;

            String html = new String(StreamTool.getHtml(url),"utf-8");

            Pattern pattern1 = Pattern.compile(findImgUrl);
            Matcher matcher1 = pattern1.matcher(html);
            if(matcher1.find()){
                imgUrl = matcher1.group(1);
            }

            Log.v("imgUrl",imgUrl);
            pattern1 = Pattern.compile(findNovelUrl);
            matcher1 = pattern1.matcher(html);
            String novel_uri = "";

            if (matcher1.find()){
                novel_uri = matcher1.group(1);
            //    novel_uri = novel_uri.replace("m","www");
                novel_URL = novel_uri;
                String novelList = new String(StreamTool.getHtml(novel_uri),"utf-8");
                Pattern pattern2 = Pattern.compile(findLatestChapter);
                Matcher matcher2 = pattern2.matcher(novelList);
                Log.v("pattern2",findLatestChapter);
                if(matcher2.find()){
                    chapter = matcher2.group(1);
                    Log.v("newchapter",chapter);
                }
                save_novel(novel_name);
                pattern1 = Pattern.compile(findChaptersUrl);
                matcher1 = pattern1.matcher(novelList);
                int index = 0;
                while(matcher1.find()){
                    save_chapter(novel_uri,matcher1,novel_id,++index);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void save_novel(String novel_name){
        Novel novel = new Novel();
        novel.setNovel_name(novel_name);
        novel.setChapter_index(0);

        File file = new File(cache, UUID.randomUUID().toString()+".jpg");
        StreamTool.getImage(imgUrl,file);
        imgUrl = file.getAbsolutePath();

        novel.setNovel_url(novel_URL);
        novel.setImg_path(imgUrl);
        novel.setMax_chapter(chapter);
        novel_id = novelDB.saveNovel(novel);

        Log.v("nddsaovel",novel.toString());
    }
    public void save_chapter(String novel_uri,Matcher matcher,int novel_id,int index){
        String chapter_url = "http://www.biquge.com"+matcher.group(1);
        String chapter_name = matcher.group(2);

        Chapter chapter = new Chapter();
        chapter.setChapter_name(chapter_name);
        chapter.setText_url(chapter_url);
        chapter.setNovel("");
        chapter.setNovel_id(novel_id);
        chapter.setNow_index(index);
        Log.v("chapter",chapter.toString());
        novelDB.saveChapter(chapter);
    }

    public void onBackPressed(){
        setResult(0);
        super.onBackPressed();
    }
}
