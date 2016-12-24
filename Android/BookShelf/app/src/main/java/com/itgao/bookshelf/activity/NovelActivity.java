package com.itgao.bookshelf.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.itgao.bookshelf.R;
import com.itgao.bookshelf.db.NovelDB;
import com.itgao.bookshelf.model.Chapter;
import com.itgao.bookshelf.model.Novel;
import com.itgao.bookshelf.util.ReplaceTool;
import com.itgao.bookshelf.util.StreamTool;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NovelActivity extends AppCompatActivity {

    private TextView text;
    private Novel novel;

    private NovelDB novelDB = NovelDB.getInstance(this);
    private List<Chapter> chapterList;

    private final String findChapterUrl = "<div.*?id=\"content\"><script>.*?</script>(.*?)</div>";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    text.setText(msg.obj.toString());

                    break;
            }
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);
        init_view();
        Intent intent = getIntent();
        novel = (Novel) intent.getSerializableExtra("novel");
        chapterList = novelDB.loadAllChapters(novel.getId());
    }

    public void init_view(){
        text = (TextView) findViewById(R.id.novel);
    }

    public void getNovelText(final Chapter chapter){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = "";
                try{
                    if(StreamTool.getHtml(chapter.getText_url()) == null){
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "网络异常";
                        handler.sendMessage(msg);
                        return ;
                    }
                    text = new String(StreamTool.getHtml(chapter.getText_url()),"utf-8");
                    Pattern pattern = Pattern.compile(findChapterUrl);
                    Matcher matcher = pattern.matcher(text);
                    if(matcher.find()){
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "                 "+chapter.getChapter_name()+"\n\n"+ ReplaceTool.replaceAll(matcher.group(1));
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "网络异常";
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
