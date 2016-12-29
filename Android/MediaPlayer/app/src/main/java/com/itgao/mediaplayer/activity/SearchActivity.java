package com.itgao.mediaplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itgao.mediaplayer.R;
import com.itgao.mediaplayer.adapter.SearchListAdapter;
import com.itgao.mediaplayer.db.Mp3DB;
import com.itgao.mediaplayer.domain.LrcContent;
import com.itgao.mediaplayer.domain.Mp3Info;
import com.itgao.mediaplayer.util.InternetUtil;
import com.itgao.mediaplayer.util.MusicNetWork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Button search;
    private EditText text_search;
    private ListView listView;
    private List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
    private SearchListAdapter adapter;

    private Mp3DB mp3DB = Mp3DB.getInstance(this);

    private Handler handler =   new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    break;
                case 1:
                    adapter = new SearchListAdapter(SearchActivity.this,mp3Infos);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = (Button) findViewById(R.id.music_activity_search);
        listView = (ListView) findViewById(R.id.search_result_listview);
        text_search = (EditText) findViewById(R.id.search_edit);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String name = text_search.getText().toString();
                    SearchMusic(SearchActivity.this, URLEncoder.encode(name,"UTF-8"),10,1,0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Mp3Info mp3Info = mp3Infos.get(i);
                Intent intent = new Intent(SearchActivity.this, PlayerActivity.class); // 定义Intent对象，跳转到PlayerActivity
                // 添加一系列要传递的数据
                intent.putExtra("id",mp3Info.getId());
                intent.putExtra("title", mp3Info.getTitle());
                intent.putExtra("url", mp3Info.getUrl());
                intent.putExtra("artist", mp3Info.getArtist());
                intent.putExtra("listPosition", -1);
                intent.putExtra("currentTime", 0);
                intent.putExtra("repeatState", 1);
                intent.putExtra("shuffleState", false);
                intent.putExtra("MSG", 1);
            //    Cloud_Muisc_getLrcAPI(SearchActivity.this,"pc",String.valueOf(mp3Info.getId()));
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick                      (AdapterView<?> adapterView, View view, int i, long l) {
                listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                        contextMenu.add(0,0,0,"加入列表");
                    }
                });
                return false;
            }
        });
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int mid = (int) info.id;
        switch (item.getItemId()){
            case 0:
                Mp3Info mp3Info = mp3Infos.get(mid);
                mp3DB.saveMp3s(mp3Info);
                Toast.makeText(SearchActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void SearchMusic(Context context, String s, int limit, int type, int offset){
        String url = MusicNetWork.CLOUD_MUSIC_API_SEARCH+"type="+type+"&s='"+s+"'&limit="+limit+"&offset="+offset;
        RequestQueue requestQueue = InternetUtil.getRequestQueue(context);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject json = null;
                try {
                    json = new JSONObject(s);
                    parse_json(json);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("response",volleyError.toString());
            }
        });
        requestQueue.add(stringRequest);

    }

    public void parse_json(JSONObject json){
        try {
            JSONArray array = json.getJSONObject("result").getJSONArray("songs");
            for(int i = 0;i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                long id = object.getInt("id");
                String author = object.getJSONArray("artists").getJSONObject(0).getString("name");
                String url = object.getString("audio");
                String name = object.getString("name");
                String img = object.getJSONObject("album").getString("picUrl");
                Mp3Info mp3Info = new Mp3Info();
                mp3Info.setArtist(author);
                mp3Info.setId(id);
                mp3Info.setTitle(name);
                mp3Info.setUrl(url);
                mp3Info.setAlbum(img);
                mp3Infos.add(mp3Info);
                handler.sendEmptyMessage(1);
                Log.v("msg",id+author+url+name+img);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



/*

"tlyric":
{"lyric":null,"version":0},"qfy":false,"sgc":false,
"lyricUser":{"uptime":1475129957005,"userid":44033417,"id":431791851,"nickname":"莎士比亚书店","status":0,"demand":0},
"klyric":{"version":0},
"lrc":
{"lyric":"[by:莎士比亚书店]\n[00:00.00] 作曲 : 薛之谦\n[00:01.00] 作词 : 薛之谦,罗艺恒\n[00:21.31]简单点 说话的方式简单点\n[00:30.41]递进的情绪请省略\n[00:33.65]你又不是个演员\n[00:36.25]别设计那些情节\n[00:42.32]没意见 我只想看看你怎么圆\n[00:51.47]你难过的太表面 像没天赋的演员\n[00:57.07]观众一眼能看见\n[01:02.39]该配合你演出的我 演视而不见\n[01:07.46]在逼一个最爱你的人 即兴表演\n[01:12.68]什么时候我们 开始收起了底线\n[01:17.91]顺应时代的改变 看那些拙劣的表演\n[01:23.21]可你曾经那么爱我 干嘛演出细节\n[01:28.39]我该变成什么样子 才能延缓厌倦\n[01:33.67]原来当爱放下防备后的这些那些\n[01:39.15]才是考验\n[01:43.09]\n[01:46.39]Yeah, here we go again\n[01:48.72]Why you always gotta act, standing in the spot light\n[01:51.48]You just want me to react.\n[01:53.78]But I just want a conversation, I don't read off the script no more\n[01:58.02]I'm tired of the games, now the stage is flawed.\n[02:00.66]Scene by scene, day by day, It's like we’re stuck on replay\n[02:04.41]Can we take the smoke and mirrors away?\n[02:06.41]Never know if the mask is off, always put me on the spot\n[02:09.91]Playing every trick that you got\n[02:11.64]Though it never seems to go your way\n[02:13.70]I’m not the easy to manipulate\n[02:16.33]Although the story, will twist and change\n[02:18.92]The characters remain at heart the same\n[02:21.51]So baby look into my eyes, and say the lines\n[02:23.94]Before the curtain closes and this love is out of time\n[02:26.10]该配合你演出的我 演视而不见\n[02:30.92]别逼一个最爱你的人 即兴表演\n[02:36.20]什么时候我们开始 没有了底线\n[02:41.42]顺着别人的谎言被动就不显得可怜\n[02:46.61]可你曾经那么爱我 干嘛演出细节\n[02:51.71]我该变成什么样子 才能配合出演\n[02:57.12]原来当爱放下防备后的这些那些\n[03:02.43]都有个期限\n[03:06.47]\n[03:08.25]其实台下的观众就我一个\n[03:13.42]其实我也看出你有点不舍\n[03:18.61]场景也习惯我们来回拉扯\n[03:23.25]还计较着什么\n[03:29.11]其实说分不开的也不见得\n[03:34.36]其实感情最怕的就是拖着\n[03:39.54]越演到重场戏越哭不出了\n[03:44.35]是否还值得\n[03:49.59]该配合你演出的我 尽力在表演\n[03:54.48]像情感节目里的嘉宾 任人挑选\n[03:59.76]如果还能看出我有爱你的那面\n[04:04.93]请剪掉那些情节 让我看上去体面\n[04:10.20]可你曾经那么爱我 干嘛演出细节\n[04:15.26]不在意的样子是我最后的表演\n[04:21.21]是因为爱你我才选择表演 这种成全\n[04:29.46]\n[04:31.82]简单点 说话的方式简单点\n[04:40.78]递进的情绪请省略\n[04:44.06]你又不是个演员\n","version":5},
"code":200,"sfy":false}





    {"result":
        {"songCount":1368,"songs":
            [
            {"id":26562231,"artists":
                [
                {"id":11096,"picUrl":null,"name":"BOBO"}
                ],
                "djProgramId":0,"audio":"http:\/\/m2.music.126.net\/ZTKjJxRpUrw4ZnmMeFrhVw==\/7941772489089653.mp3","page":"http:\/\/music.163.com\/m\/song\/26562231",
                    "album":{"id":2523118,"picUrl":"http:\/\/p1.music.126.net\/GsZ0mC4H34fe7GkLS3s9EQ==\/2323268069554620.jpg",
                    "artist":{"id":0,"picUrl":null,"name":""},
                "name":"光荣"},
                "name":"光荣"},

            {"id":33911288,"artists":[{"id":1132440,"picUrl":null,"name":"朱刚"}],
                "djProgramId":0,"audio":"http:\/\/m2.music.126.net\/p9Pc6Jftrk1piKt8FyhMYw==\/7945071024137342.mp3",
                    "page":"http:\/\/music.163.com\/m\/song\/33911288",
                    "album":{"id":3266117,"picUrl":"http:\/\/p1.music.126.net\/B3F-mVNbAkwdnum7343-gw==\/3311729023116774.jpg",
                    "artist":{"id":0,"picUrl":null,"name":""},"name":"在你身后"},"name":"光荣"},
            {"id":358838,"artists":[{"id":11809,"picUrl":null,"name":"Happy King"}],"djProgramId":0,
                    "audio":"http:\/\/m2.music.126.net\/y8RU20D5Npgl0L2Gq8Iy1g==\/1902155116117399.mp3",
                    "page":"http:\/\/music.163.com\/m\/song\/358838",
                    "album":{"id":35442,"picUrl":"http:\/\/p1.music.126.net\/L1MWY7_7gnuqGzLSBZfNTw==\/79164837215171.jpg",
                    "artist":{"id":0,"picUrl":null,"name":""},"name":"光荣"},"name":"光荣"},
            {"id":437753524,"artists":[{"id":1049276,"picUrl":null,"name":"知性的小方块"}],"djProgramId":0,
                    "audio":"http:\/\/m2.music.126.net\/42eBEvF7b2053t16n_q6Yw==\/3402988505665956.mp3",
                    "page":"http:\/\/music.163.com\/m\/song\/437753524",
                    "album":{"id":34946232,"picUrl":"http:\/\/p1.music.126.net\/H-UeM0GI1RTBy4pzeN6sxg==\/17729624998257222.jpg",
                    "artist":{"id":0,"picUrl":null,"name":""},"name":"光荣"},"name":"光荣"},
            {"id":31421394,"artists":[{"id":203003,"picUrl":null,"name":"郭晓东"},{"id":5418,"picUrl":null,"name":"王宝强"},{"id":0,"picUrl":null,"name":"张丰毅"},{"id":12074935,"picUrl":null,"name":"袁弘"},{"id":0,"picUrl":null,"name":"杜海涛"},{"id":0,"picUrl":null,"name":"刘昊然"}],"djProgramId":0,"audio":"http:\/\/m2.music.126.net\/0g3WWn43MxhENdnKVyd0Ig==\/3283141720642598.mp3","page":"http:\/\/music.163.com\/m\/song\/31421394","album":{"id":3119387,"picUrl":"http:\/\/p1.music.126.net\/UrGopRYyHRhWaVupaaXFhQ==\/2898312651058605.jpg","artist":{"id":0,"picUrl":null,"name":""},"name":"渴望光荣"},"name":"渴望光荣"},{"id":37764339,"artists":[{"id":1158147,"picUrl":null,"name":"卖血上网a"}],"djProgramId":0,"audio":"http:\/\/m2.music.126.net\/CocCIoN2OhmDSGUA1i2ZVg==\/18700493766134385.mp3","page":"http:\/\/music.163.com\/m\/song\/37764339","album":{"id":3413539,"picUrl":"http:\/\/p1.music.126.net\/m86E9vWoZAQyQAPxflH-cQ==\/765260102952794.jpg","artist":{"id":0,"picUrl":null,"name":""},"name":"渴望光荣"},"name":"渴望光荣"},{"id":431552029,"artists":[{"id":12050072,"picUrl":null,"name":"DLB"},{"id":8766,"picUrl":null,"name":"李琳"},{"id":0,"picUrl":null,"name":"王旭波"},{"id":0,"picUrl":null,"name":"邓博宇"}],"djProgramId":0,"audio":"http:\/\/m2.music.126.net\/mFs22F5Ku7UaqDGk2EXrdQ==\/18709289858656482.mp3","page":"http:\/\/music.163.com\/m\/song\/431552029","album":{"id":34891016,"picUrl":"http:\/\/p1.music.126.net\/iJlA7GPDAeDkwQGWT3mtsg==\/17679047463360165.jpg","artist":{"id":0,"picUrl":null,"name":""},"name":"劳动最光荣"},"name":"劳动最光荣(live)"},{"id":409941478,"artists":[{"id":999220,"picUrl":null,"name":"王俊凯"}],"djProgramId":0,"audio":"http:\/\/m2.music.126.net\/HiRt-fjyKeCq_fbpk1hCIg==\/1399678308750730.mp3","page":"http:\/\/music.163.com\/m\/song\/409941478","album":{"id":34611516,"picUrl":"http:\/\/p1.music.126.net\/ByaWRFeDo6hGgEvOFNmv8A==\/1420569029527047.jpg","artist":{"id":0,"picUrl":null,"name":""},"name":"王俊凯翻唱集"},"name":"光荣"},{"id":406000306,"artists":[{"id":12003038,"picUrl":null,"name":"凯瑟喵"}],"djProgramId":0,"audio":"http:\/\/m2.music.126.net\/cjPtK5woBUCgfzivMy0KHw==\/1417270490925073.mp3","page":"http:\/\/music.163.com\/m\/song\/406000306","album":{"id":34511834,"picUrl":"http:\/\/p1.music.126.net\/QeATAzdet4JuNrQzx9t9kg==\/16657601161393113.jpg","artist":{
*/
    }
