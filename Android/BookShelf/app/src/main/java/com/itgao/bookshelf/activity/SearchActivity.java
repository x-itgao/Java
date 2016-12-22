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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

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
    private final String findLatestChapter = "<p>最新章节.*?<a.*?>(.*?)</a>";
    private final String findImgUrl = "<img.*?src=\"(.*?)\".*?alt=\"<em>.*?</em>";
    private final String uri = "http://zhannei.baidu.com/cse/search?s=287293036948159515&q=";
    private final int RESULT_CODE = 0;

    private String novelName;
    private String chapter;
    private String imgUrl;
    private File cache;
    private int novel_id;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    wait_text.setText("成功收入！");
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
                wait_text.setText("等待片刻");
                Log.v("novel_name",novelName);
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
                            return ;
                        }
                    }
                }).start();

            }
        });
    }

    public void get_novel(String novel_name){
        parse_info(novel_name);
    }

    public void parse_info(String novel_name){
        String name = null;
        try {
            name = URLEncoder.encode(novel_name,"UTF-8");
            String url = uri + name;
            String html = new String(StreamTool.getHtml(url),"utf-8");
            Log.v("html",html);
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

                String novelList = new String(StreamTool.getHtml(novel_uri),"gbk");
                pattern1 = Pattern.compile(findLatestChapter);
                matcher1 = pattern1.matcher(novelList);
                if(matcher1.find()){
                    chapter = matcher1.group(1);
                }
                save_novel(novel_name);
                pattern1 = Pattern.compile(findChaptersUrl);
                matcher1 = pattern1.matcher(novelList);
                int index = 0;
                while(matcher1.find()){
                    save_chapter(novel_uri,matcher1,novel_id,++index);
                }

            }
        } catch (UnsupportedEncodingException e) {
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

        novel.setImg_path(imgUrl);
        novel.setMax_chapter(chapter);
        novel_id = novelDB.saveNovel(novel);

        Log.v("novel",novel.getMax_chapter()+novel_id);
    }
    public void save_chapter(String novel_uri,Matcher matcher,int novel_id,int index){
        String chapter_url = novel_uri+matcher.group(1);
        String chapter_name = matcher.group(2);

        Chapter chapter = new Chapter();
        chapter.setChapter_name(chapter_name);
        chapter.setText_url(chapter_url);
        chapter.setNovel("");
        chapter.setNovel_id(novel_id);
        chapter.setNow_index(index);
        Log.v("chapter_name",chapter_name);
        novelDB.saveChapter(chapter);
    }

    public void onBackPressed(){
        setResult(0);
        super.onBackPressed();
    }
}
