package com.itgao.mediaplayer.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by xiaogao.XU on 2016/12/28.
 */
public class InternetUtil {

    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue(Context context){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }
}
