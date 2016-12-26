package com.itgao.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itgao.bookshelf.R;
import com.itgao.bookshelf.db.NovelDB;
import com.itgao.bookshelf.model.Chapter;
import com.itgao.bookshelf.model.Novel;
import com.itgao.bookshelf.util.ReplaceTool;
import com.itgao.bookshelf.util.StreamTool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NovelActivity extends AppCompatActivity {

    private TextView text;
    private Novel novel;

    private NovelDB novelDB = NovelDB.getInstance(this);
    private List<Chapter> chapterList;

    public static final String findChapterUrl = "<div.*?id=\"content\"><script>.*?</script>(.*?)</div>";
    // 滑动
    private GestureDetector gestureDetector;
    // 防止滑动
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    private int wordCount = 0;

    // 保存当前章节的文本
    private String now_text = "";
    private int index = 0;
    private List<String> strings;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    now_text = msg.obj.toString();
                    strings = handle();
                    text.setText(strings.get(index));
                    break;
            }
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);

        gestureDetector = new GestureDetector(this,onGestureListener);
        Intent intent = getIntent();
        novel = (Novel) intent.getSerializableExtra("novel");
        chapterList = novelDB.loadAllChapters(novel.getId());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        text = (TextView) findViewById(R.id.novel);
        wordCount = getLength();
        Log.v("msg",wordCount+"");
        getNovelText(chapterList.get(chapterList.size()-1));
        super.onWindowFocusChanged(hasFocus);

    }

    // 设置滑动
    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    float x = e2.getX() - e1.getX();
                    float y = e2.getY() - e1.getY();

                    if (x > 0) {
                        updateNovel(-1);
                    } else if (x < 0) {
                        updateNovel(1);
                    }
                    return true;
                }
            };

    public boolean onTouchEvent(MotionEvent event){
        // center 348 547
        // left 240  right 540
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if(currentTime - lastClickTime < MIN_CLICK_DELAY_TIME){
            return false;
        }
        lastClickTime = currentTime;
        float eventX = event.getX();
        if(eventX<240){
        //      左翻页
        //    updateNovel(-1);
            if(index == 0){
                index = 0;
                updateNovel(1);
                return false;
            }
            text.setText(strings.get(--index));
        }else if(eventX > 540){
            if(index == strings.size() - 1){
                index = 0;
                updateNovel(-1);
                return false;
            }
        //    updateNovel(1);
            text.setText(strings.get(++index));

        }else{
            /**
             * 文章的点击事件 弹出目录 并定位到当前所读的章节
             */
            showPopupWindow();
        }
        return false;
    }

    public void getNovelText(final Chapter chapter){

        String s = chapter.getNovel();
        if(s != null && !s.equals("")){
            Log.v("s",s);
            now_text = s;
            strings = handle();
            text.setText(strings.get(index));
            Log.v("status","hava");
            return ;
        }

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


    /**
     * 工具栏界面的实现 采用PopupWindow
     */
    public void showPopupWindow() {
        // 利用LayoutInflater获取view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.novel_util_window, null);

        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置窗体可以点击
        window.setFocusable(true);
        // 实例化一个colorDrawable 颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xd0000000);
        window.setBackgroundDrawable(dw); // 必须设置
        window.setAnimationStyle(R.style.mypopwindow_anim_style);


        // 设置弹出工具栏出现在哪个位置
        int []location = new int[2];
        this.findViewById(R.id.novel_screen).getLocationOnScreen(location);
        window.showAtLocation(this.findViewById(R.id.novel_screen), Gravity.BOTTOM, 0, 0);
        Log.v("dd","test");
        ImageButton pre = (ImageButton) view.findViewById(R.id.novel_util_pre_page);
        ImageButton next = (ImageButton) view.findViewById(R.id.novel_util_next_page);
        ImageButton novel_util_catalog = (ImageButton) view.findViewById(R.id.novel_util_catalog);
        /**
         *  pre next 的点击事件实现 上一页 和 下一页
         */

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNovel(1);
                index = 0;
                window.dismiss();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNovel(-1);
                index = 0;
                window.dismiss();
            }
        });
        /**
         * 目录的实现
         *
         */
        novel_util_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                showCatalogWindow();
            }
        });


        // 重写点击空白区域 window消失
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    /**
     * 弹出目录window
     */
    private void showCatalogWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.novel_catalog_window, null);

        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置窗体可以点击
        window.setFocusable(true);
        // 实例化一个colorDrawable 颜色为半透明
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.colorCatalog));
        window.setBackgroundDrawable(dw); // 必须设置
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.showAtLocation(this.findViewById(R.id.novel_test), Gravity.TOP, 0, 0);

        ListView listView = (ListView) view.findViewById(R.id.novel_catalog_list);
        List<String> listItem = new ArrayList<String>();
        for (Chapter c : chapterList) {
            listItem.add(c.getChapter_name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NovelActivity.this, android.R.layout.simple_list_item_1, listItem);
        listView.setAdapter(adapter);

        /**
         * 目录点击切换
         */
        listView.setSelection(novel.getChapter_index() - 5 > 0 ? novel.getChapter_index() - 5 : 0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                getNovelText(chapterList.get(position));
                novel.setChapter_index(position);
                window.dismiss();
            }
        });
    }

    /**
     * 更换小说界面
     *
     * @param direction
     */
    private void updateNovel(int direction) {
        int index = novel.getChapter_index() + direction;
        if (index > chapterList.size() || index < 0) {
            Toast.makeText(NovelActivity.this, "没有章节了", Toast.LENGTH_SHORT).show();
            return;
        }
        // 这个改变只适应当前活动
        novel.setChapter_index(index);
        // 即时保存进度
        novelDB.updateNovel(novel);
        getNovelText(chapterList.get(index));
    }

    /**
     * TextView可以容纳多少字符
     * @return
     */
    public int getLength(){
        int height = text.getHeight();

        int lineHeight = text.getLineHeight();
        int lineCount = height / lineHeight+6;

        float textSize = text.getTextSize();
        float lineWords = text.getWidth() / textSize;
        return (int)(lineCount * lineWords);
    }

    /**
     * 测试所用
     * @param start
     * @param end
     * @return
     */
    public String read(int start,int end){
        char[] c_array = now_text.toCharArray();
        char[] temp = new char[wordCount];

        for(int i = start;i<end;i++){
            temp[index-start] = c_array[index];
            if (c_array[index] == '\n'){
                i += (int) text.getWidth() / text.getTextSize();
            }
            index ++;
        }
        return new String(temp);
    }

    /**
     * 处理正文部分，分页
     * @return
     */
    public List<String> handle(){

        if(wordCount == 0){
            Log.v("msg2",""+wordCount);
            finish();
        }

        List<String> stringList = new ArrayList<String>();
        char[] array = now_text.toCharArray();
        int index = 0;
        int dop = 0;

        while(true){

            char[] temp = new char[wordCount];
            for (int i = 0;i<wordCount;i++){
                temp[index-dop] = array[index];
                if(array[index] == '\n'){
                    i += (int) text.getWidth() / text.getTextSize();
                }
                index ++;
                Log.v("index",index+"");
                if(index >= now_text.length()){
                    stringList.add(new String(temp));
                    return stringList;
                }
            }
            dop = index;
            stringList.add(new String(temp));

        }
    }

    // 当返回的时候保存一下当前进度
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
