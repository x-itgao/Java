package com.itgao.mediaplayer.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaogao.XU on 2016/12/28.
 */
public class MusicNetWork {

    /**
     * 云音乐搜索API网址
     */
    public static final String CLOUD_MUSIC_API_SEARCH = "http://s.music.163.com/search/get/?";
    /**
     * 歌曲信息API网址
     */
    public static final String CLOUD_MUSIC_API_MUSICINGO = "http://music.163.com/api/song/detail/?";
    /**
     * 获取歌曲的歌词
     */
    public static final String CLOUD_MUSIC_API_MUSICLRC = "http://music.163.com/api/song/lyric?";
    /**
     * 获取歌单
     */
    public static final String CLOUD_MUSIC_API_MUSICLIST = "http://music.163.com/api/playlist/detail?";

    /**
     * 网易音乐搜索API
     * http://s.music.163.com/search/get/
     * 获取方式：GET
     * 参数：
     * src: lofter //可为空
     * type: 1
     * filterDj: true|false //可为空
     * s: //关键词
     * limit: 10 //限制返回结果数
     * offset: 0 //偏移
     * callback: //为空时返回json，反之返回jsonp callback
     * @param s
     * @param context
     * @return
     * 注意废数字才用‘’符号，要不不能用，否则出错！！
     */

    /**
     * 网易云音乐歌曲信息API
     * @param context
     * @param id 歌曲id
     * @param ids 用[]包裹起来的歌曲id 写法%5B %5D
     * @return
     */
    public static void Cloud_Music_MusicInfoAPI(Context context,String id,String ids){
        String url = CLOUD_MUSIC_API_MUSICINGO + "id="+id+"&ids=%5B"+ids+"%5D";
        RequestQueue requestQueue = InternetUtil.getRequestQueue(context);
        StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
            @Override
            public void onResponse(String s){
                try {
                    JSONObject json = new JSONObject(s);
                    Log.i("onResponse: ",json.toString());
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError){
                Log.i("onResponse: ",volleyError.toString());
            }
        });
        requestQueue.add(straingRequest);
    }
    /**
     * 获取歌曲歌词的API
     *URL：

     GET http://music.163.com/api/song/lyric

     必要参数：

     id：歌曲ID

     lv：值为-1，我猜测应该是判断是否搜索lyric格式

     kv：值为-1，这个值貌似并不影响结果，意义不明

     tv：值为-1，是否搜索tlyric格式
     * @param context
     * @param os
     * @param id
     */
    public static void Cloud_Muisc_getLrcAPI(Context context,String os,String id)
    {
        String url = CLOUD_MUSIC_API_MUSICLRC + "os="+os+"&id="+id+"&lv=-1&kv=-1&tv=-1";
        RequestQueue requestQueue = InternetUtil.getRequestQueue(context);
        StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
            @Override
            public void onResponse(String s){
                try {
                    JSONObject json = new JSONObject(s);
                    Log.i("onResponse: ",json.toString());
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError){
                Log.i("onResponse: ",volleyError.toString());
            }
        });
        requestQueue.add(straingRequest);
    }
    public static JSONObject json = null;
    public static JSONObject getInfoFromUrl_Volley(String url,Context context)
    {
        json = null;
        RequestQueue requestQueue = InternetUtil.getRequestQueue(context);
        StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
            @Override
            public void onResponse(String s){
                try {
                    json = new JSONObject(s);
                    Log.i("onResponse: ",json.toString());
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError){
                Log.i("onResponse: ",volleyError.toString());
            }
        });
        requestQueue.add(straingRequest);
        return json;
    }
}
